data class Hand(val cards: String)

private const val FIVE_OF_A_KIND = 6
private const val FOUR_OF_A_KIND = 5
private const val FULL_HOUSE = 4
private const val THREE_OF_A_KIND = 3
private const val TWO_PAIRS = 2
private const val ONE_PAIRS = 1

val Hand.strength: Int
    get() {
        val cardToCount = cards.groupBy { it }
            .mapValues { it.value.size }
        return when {
            cardToCount.any { (_, count) -> count == 5 } -> FIVE_OF_A_KIND
            cardToCount.any { (_, count) -> count == 4 } -> FOUR_OF_A_KIND
            cardToCount.any { (_, count) -> count == 3 } -> when {
                cardToCount.any { (_, count) -> count == 2 } -> FULL_HOUSE
                else -> THREE_OF_A_KIND
            }

            cardToCount.count { (_, count) -> count == 2 } == 2 -> TWO_PAIRS
            cardToCount.any { (_, count) -> count == 2 } -> ONE_PAIRS
            else -> 0
        }
    }

val Hand.strengthWithJokers: Int
    get() {
        val cardToCount = cards.groupBy { it }
            .filter { it.key != 'J' }
            .mapValues { it.value.size }

        val availableJokers = cards.count { it == 'J' }

        return when {
            availableJokers >= 4 || cardToCount.any { (_, count) -> count >= 5 - availableJokers } -> FIVE_OF_A_KIND
            availableJokers >= 3 || cardToCount.any { (_, count) -> count >= 4 - availableJokers } -> FOUR_OF_A_KIND
            isFullHouse(cardToCount, availableJokers) -> FULL_HOUSE
            availableJokers >= 2 || cardToCount.any { (_, count) -> count >= 3 - availableJokers } -> THREE_OF_A_KIND
            isTwoPairs(cardToCount, availableJokers) -> TWO_PAIRS
            availableJokers >= 1 || cardToCount.any { (_, count) -> count == 2 } -> ONE_PAIRS
            else -> 0
        }
    }

private fun isFullHouse(cardToCount: Map<Char, Int>, availableJokers: Int): Boolean {
    if (cardToCount.isEmpty()) return availableJokers == 5
    val (maxCard, maxCount) = cardToCount.maxBy { it.value }
    return when (maxCount) {
        3 -> cardToCount
            .filterKeys { it != maxCard }
            .any { (_, count) -> count >= 2 - availableJokers }

        2 -> cardToCount
            .filterKeys { it != maxCard }
            .any { (_, count) -> count >= 3 - availableJokers }

        else -> availableJokers >= 4
    }
}

private fun isTwoPairs(cardToCount: Map<Char, Int>, availableJokers: Int): Boolean {
    return availableJokers >= 2
        || (availableJokers == 1 && cardToCount.any { (_, count) -> count == 2 })
        || cardToCount.count { (_, count) -> count == 2 } == 2
}

private val strengthOrderedCards = listOf('A', 'K', 'Q', 'J', 'T', '9', '8', '7', '6', '5', '4', '3', '2')
private val strengthOrderedCardsWithJokers = listOf('A', 'K', 'Q', 'T', '9', '8', '7', '6', '5', '4', '3', '2', 'J')

val Char.strength: Int
    get() = strengthOrderedCards.size - strengthOrderedCards.indexOf(this)
val Char.strengthWithJokers: Int
    get() = strengthOrderedCardsWithJokers.size - strengthOrderedCardsWithJokers.indexOf(this)

fun main() {

    fun parseHandToBidList(lines: List<String>): List<Pair<Hand, Int>> {
        val handToBidList = lines.map { line ->
            val (hand, bid) = line.split(" +".toRegex()).let { Hand(it[0]) to it[1].toInt() }
            hand to bid
        }
        return handToBidList
    }

    fun compareByCard(hand1: Hand, hand2: Hand): Int {
        for ((c1, c2) in hand1.cards.zip(hand2.cards)) {
            val c1Strength = c1.strength
            val c2Strength = c2.strength
            when {
                c1Strength > c2Strength -> return 1
                c1Strength < c2Strength -> return -1
            }
        }
        return 0
    }


    fun part1(lines: List<String>): Int {
        return parseHandToBidList(lines)
            .sortedWith { (hand1), (hand2) ->
                compareBy<Hand> { it.strength }
                    .thenComparing(::compareByCard)
                    .compare(hand1, hand2)
            }
            .mapIndexed { i, (_, bid) -> (i + 1) * bid }
            .sum()
    }

    fun compareByCardWithJokers(hand1: Hand, hand2: Hand): Int {
        for ((c1, c2) in hand1.cards.zip(hand2.cards)) {
            val c1Strength = c1.strengthWithJokers
            val c2Strength = c2.strengthWithJokers
            when {
                c1Strength > c2Strength -> return 1
                c1Strength < c2Strength -> return -1
            }
        }
        return 0
    }

    fun part2(lines: List<String>): Int {
        return parseHandToBidList(lines)
            .sortedWith { (hand1), (hand2) ->
                compareBy<Hand> { it.strengthWithJokers }
                    .thenComparing(::compareByCardWithJokers)
                    .compare(hand1, hand2)
            }
            .mapIndexed { i, (_, bid) -> (i + 1) * bid }
            .sum()
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day07_test")
    part1(testInput).println()
    part2(testInput).println()

    val input = readInput("Day07")
    part1(input).println()
    part2(input).println()
}
