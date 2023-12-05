fun main() {

    val valueToWords = mapOf(
        1 to listOf("1", "one"),
        2 to listOf("2", "two"),
        3 to listOf("3", "three"),
        4 to listOf("4", "four"),
        5 to listOf("5", "five"),
        6 to listOf("6", "six"),
        7 to listOf("7", "seven"),
        8 to listOf("8", "eight"),
        9 to listOf("9", "nine"),
    )

    val wordToValue = valueToWords.invert()

    fun String.extractCalibrationValue(): Int {
        val first = findAnyOf(wordToValue.keys)!!.second.let { wordToValue[it]!! }
        val last = findLastAnyOf(wordToValue.keys)!!.second.let { wordToValue[it]!! }
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

private fun <K, V> Map<K, Collection<V>>.invert(): Map<V, K> =
    asSequence()
        .flatMap { (key, values) -> values.map { it to key } }
        .toMap()
