fun main() {

    fun findLargerDistanceConfigurations(time: Long, record: Long) = (1..<time).asSequence()
        .map { it to it * (time - it) }
        .dropWhile { it.second <= record }
        .takeWhile { it.second > record }
        .toList()

    fun part1(lines: List<String>): Int {
        val times = lines[0].split(":")[1].trim().split(" +".toRegex()).map { it.toLong() }
        val records = lines[1].split(":")[1].trim().split(" +".toRegex()).map { it.toLong() }

        return times.zip(records)
            .map { (time, record) -> findLargerDistanceConfigurations(time, record).count() }
            .reduce { acc, i -> acc * i }
    }

    fun part2(lines: List<String>): Int {
        val time = lines[0].split(":")[1].replace(" ", "").toLong()
        val record = lines[1].split(":")[1].replace(" ", "").toLong()

        return findLargerDistanceConfigurations(time, record).count()
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day06_test")
    part1(testInput).println()
    part2(testInput).println()

    val input = readInput("Day06")
    part1(input).println()
    part2(input).println()
}
