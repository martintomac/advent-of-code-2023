fun main() {

    fun extractSymbols(line: String) = "([^0-9.])".toRegex()
        .findAll(line)
        .map { match -> match.value to match.range.first }
        .toList()

    fun extractNumbers(line: String) = "(\\d+)".toRegex().findAll(line)
        .map { match -> match.value.toInt() to match.range }
        .toList()

    fun Int.toRange() = this..this

    fun IntRange.expand(left: Int, right: Int) = (first - left)..(last + right)

    fun Map<Int, List<Pair<Int, IntRange>>>.findNeighborNumbers(
        symPoint: Pair<Int, Int>
    ): List<Pair<Int, IntRange>> {
        val lineNum = symPoint.first
        val symLocation = symPoint.second

        return symLocation.toRange().expand(1, 1)
            .flatMap { neighborLocation ->
                lineNum.toRange().expand(1, 1)
                    .flatMap { this[it]?.filter { (_, range) -> neighborLocation in range }.orEmpty() }
            }
            .distinct()
    }

    fun sumOfSerialNumbers(lines: List<String>): Int {
        val lineToNumbers = lines.mapIndexed { lineNum, line -> lineNum to extractNumbers(line) }
            .toMap()

        return lines
            .mapIndexed { lineNum, line ->
                val symbols = extractSymbols(line)
                symbols.flatMap { (_, symLocation) ->
                    lineToNumbers.findNeighborNumbers(lineNum to symLocation)
                }
            }
            .flatten()
            .sumOf { it.first }
    }

    fun sumOfGearRatios(lines: List<String>): Int {
        val lineToNumbers = lines.mapIndexed { lineNum, line -> lineNum to extractNumbers(line) }
            .toMap()

        return lines
            .mapIndexed { lineNum, line ->
                val multiplySymbols = extractSymbols(line).filter { (value, _) -> value == "*" }
                multiplySymbols.mapNotNull { (_, mulLocation) ->
                    val operands = lineToNumbers.findNeighborNumbers(lineNum to mulLocation)
                    when {
                        operands.size < 2 -> null
                        operands.size == 2 -> operands[0].first * operands[1].first
                        else -> throw UnsupportedOperationException("Cartesian product not supported")
                    }
                }
            }
            .flatten()
            .sum()
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day03_test")
    sumOfSerialNumbers(testInput).println()
    sumOfGearRatios(testInput).println()

    val input = readInput("Day03")
    sumOfSerialNumbers(input).println()
    sumOfGearRatios(input).println()
}
