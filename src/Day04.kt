import kotlin.math.pow


data class Scratchcard(
    val id: Int,
    val winningNumbers: Set<Int>,
    val myNumbers: Set<Int>,
)

val Scratchcard.winCount: Int get() = myNumbers.count { it in winningNumbers }

fun main() {

    fun parseScratchcard(line: String): Scratchcard {
        val (_, cardNumber, winningNumsPart, myNumsPart) =
            "Card +(\\d+): +(.+) +\\| +(.+)".toRegex().matchEntire(line)!!.groupValues

        val winningNumbers = winningNumsPart.trim().split(" +".toRegex()).map { it.toInt() }.toSet()
        val myNumbers = myNumsPart.trim().split(" +".toRegex()).map { it.toInt() }.toSet()

        return Scratchcard(cardNumber.toInt(), winningNumbers, myNumbers)
    }

    fun sumNumberOfScoredPoints(lines: List<String>): Int {
        return lines.map { parseScratchcard(it) }
            .sumOf { scratchcard ->
                when {
                    scratchcard.winCount == 0 -> 0
                    else -> 2.0.pow(scratchcard.winCount - 1).toInt()
                }
            }
    }

    fun getNumOfTotalScratchcards(lines: List<String>): Int {
        val scratchcards = lines.map { parseScratchcard(it) }

        val idToNumOfScratchcards = scratchcards.associate { it.id to 1 }.toMutableMap()
        scratchcards.forEach { scratchcard ->
            val increment = idToNumOfScratchcards[scratchcard.id]!!
            for (i in 1..scratchcard.winCount) {
                val scratchcardNum = scratchcard.id + i

                if (scratchcardNum > scratchcards.count()) break

                idToNumOfScratchcards[scratchcardNum] =
                    idToNumOfScratchcards[scratchcardNum]!! + increment
            }
        }
        return idToNumOfScratchcards.values.sum()
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day04_test")
    sumNumberOfScoredPoints(testInput).println()
    getNumOfTotalScratchcards(testInput).println()

    val input = readInput("Day04")
    sumNumberOfScoredPoints(input).println()
    getNumOfTotalScratchcards(input).println()
}
