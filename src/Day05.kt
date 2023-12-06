import kotlin.streams.asStream

fun main() {

    fun parseSeeds(seedsLine: String) =
        "(\\d+)".toRegex().findAll(seedsLine)
            .map { it.value.toLong() }
            .toList()

    data class Mapping(
        val destination: Long,
        val source: Long,
        val length: Long,
    ) {
        val sourceRange = source..<(source + length)
        val delta = destination - source
    }

    fun parseMapping(it: String): Mapping {
        val (destination, source, length) = it.split(" +".toRegex())
        return Mapping(destination.toLong(), source.toLong(), length.toLong())
    }

    fun List<List<String>>.findGroupLinesByName(name: String): List<String>? =
        find { it.first().startsWith(name) }

    fun parseAsMap(groupLines: List<String>): (Long) -> Long {
        val mappings = groupLines.drop(1)
            .map { parseMapping(it) }
        return { value ->
            mappings.find { value in it.sourceRange }
                ?.let { value + it.delta }
                ?: value
        }
    }

    fun part1(lines: List<String>): Long {
        val grouped = lines.groupRuns { line -> line.isBlank() }

        val seeds = parseSeeds(grouped.findGroupLinesByName("seeds")!!.first())

        val seedToSoilMap = parseAsMap(grouped.findGroupLinesByName("seed-to-soil")!!)
        val soilToFertilizerMap = parseAsMap(grouped.findGroupLinesByName("soil-to-fertilizer")!!)
        val fertilizerToWaterMap = parseAsMap(grouped.findGroupLinesByName("fertilizer-to-water")!!)
        val waterToLightMap = parseAsMap(grouped.findGroupLinesByName("water-to-light")!!)
        val lightToTemperatureMap = parseAsMap(grouped.findGroupLinesByName("light-to-temperature")!!)
        val temperatureToHumidityMap = parseAsMap(grouped.findGroupLinesByName("temperature-to-humidity")!!)
        val humidityToLocationMap = parseAsMap(grouped.findGroupLinesByName("humidity-to-location")!!)

        return seeds.minOfOrNull { seed ->
            val soil = seedToSoilMap(seed)
            val fertilizer = soilToFertilizerMap(soil)
            val water = fertilizerToWaterMap(fertilizer)
            val light = waterToLightMap(water)
            val temperature = lightToTemperatureMap(light)
            val humidity = temperatureToHumidityMap(temperature)
            val location = humidityToLocationMap(humidity)
            location
        }!!
    }

    fun part2(lines: List<String>): Long {
        val grouped = lines.groupRuns { line -> line.isBlank() }

        val seedToSoilMap = parseAsMap(grouped.findGroupLinesByName("seed-to-soil")!!)
        val soilToFertilizerMap = parseAsMap(grouped.findGroupLinesByName("soil-to-fertilizer")!!)
        val fertilizerToWaterMap = parseAsMap(grouped.findGroupLinesByName("fertilizer-to-water")!!)
        val waterToLightMap = parseAsMap(grouped.findGroupLinesByName("water-to-light")!!)
        val lightToTemperatureMap = parseAsMap(grouped.findGroupLinesByName("light-to-temperature")!!)
        val temperatureToHumidityMap = parseAsMap(grouped.findGroupLinesByName("temperature-to-humidity")!!)
        val humidityToLocationMap = parseAsMap(grouped.findGroupLinesByName("humidity-to-location")!!)

        return parseSeeds(grouped.findGroupLinesByName("seeds")!!.first())
            .chunked(2)
            .asSequence()
            .flatMap { (start, length) -> (start..<(start + length)) }
            .asStream()
            .parallel()
            .mapToLong { seed ->
                val soil = seedToSoilMap(seed)
                val fertilizer = soilToFertilizerMap(soil)
                val water = fertilizerToWaterMap(fertilizer)
                val light = waterToLightMap(water)
                val temperature = lightToTemperatureMap(light)
                val humidity = temperatureToHumidityMap(temperature)
                val location = humidityToLocationMap(humidity)
                location
            }
            .min()
            .orElseThrow()
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day05_test")
    part1(testInput).println()
    part2(testInput).println()

    val input = readInput("Day05")
    part1(input).println()
    part2(input).println()
}

fun <T> List<T>.groupRuns(splitPredicate: (T) -> Boolean): List<List<T>> =
    fold(mutableListOf<MutableList<T>>(mutableListOf())) { acc, line ->
        if (splitPredicate(line)) {
            acc += mutableListOf<T>()
        } else {
            acc.last() += line
        }
        acc
    }

