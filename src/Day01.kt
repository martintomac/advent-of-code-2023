fun main() {

    val wordValues = mapOf(
        "1" to 1,
        "one" to 1,
        "2" to 2,
        "two" to 2,
        "3" to 3,
        "three" to 3,
        "4" to 4,
        "four" to 4,
        "5" to 5,
        "five" to 5,
        "6" to 6,
        "six" to 6,
        "7" to 7,
        "seven" to 7,
        "8" to 8,
        "eight" to 8,
        "9" to 9,
        "nine" to 9,
    )

    fun String.extractCalibrationValue(): Int {
        findAnyOf(wordValues.keys)!!.second.let { wordValues[it] }
        val first = findAnyOf(wordValues.keys)!!.second.let { wordValues[it]!! }
        val last = findLastAnyOf(wordValues.keys)!!.second.let { wordValues[it]!! }
        return first * 10 + last
    }

    fun machine(input: List<String>): Int {
        return input.sumOf { it.extractCalibrationValue() }
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day01_test")
    machine(testInput).println()

    val input = readInput("Day01")
    machine(input).println()
}
