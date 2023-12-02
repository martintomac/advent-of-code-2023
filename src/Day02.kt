import Game.ColorCubes

private val colors = setOf("red", "green", "blue")
private val lineFormat = "Game (\\d+): (.+)".toRegex()

private fun parseGame(line: String): Game {

    fun String.extractNumAndColor(): ColorCubes {
        val (numStr, color) = split(" ")
        return ColorCubes(numStr.toInt(), color)
    }

    fun String.parseAsDrawSetup() =
        split(",")
            .map { it.trim() }
            .map { it.extractNumAndColor() }

    fun String.parseGameSetups() =
        split(";")
            .map { it.trim() }
            .map { it.parseAsDrawSetup() }


    val (gameIdStr, gameSetup) = lineFormat.matchEntire(line)!!.destructured
    return Game(gameIdStr.toInt(), gameSetup.parseGameSetups())
}

data class Game(
    val id: Int,
    val draws: List<List<ColorCubes>>
) {
    data class ColorCubes(
        val count: Int,
        val color: String,
    )
}

fun sumLegalGameIds(lines: List<String>): Int {
    fun getMaxAllowedNumOfCubes(color: String) =
        when (color) {
            "red" -> 12
            "green" -> 13
            "blue" -> 14
            else -> 0
        }

    fun Game.isIllegal() = draws.any { colorCubes ->
        colorCubes.any { it.count > getMaxAllowedNumOfCubes(it.color) }
    }

    fun Game.isLegal() = !isIllegal()

    fun sumLegalGameIds(games: List<Game>) =
        games.filterNot { it.isLegal() }
            .sumOf { it.id }


    val games = lines.map { parseGame(it) }
    return sumLegalGameIds(games)
}

fun sumOfGamePowers(lines: List<String>): Int {
    fun Game.maxCountOfColorOrNull(color: String) =
        draws.flatten()
            .filter { it.color == color }
            .maxOfOrNull { it.count }

    val games = lines.map { parseGame(it) }

    return games.sumOf { game ->
        colors.mapNotNull { color -> game.maxCountOfColorOrNull(color) }
            .reduce { power, count -> power * count }
    }
}

fun main() {

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day02_test")
    sumLegalGameIds(testInput).println()
    sumOfGamePowers(testInput).println()

    val input = readInput("Day02")
    sumLegalGameIds(input).println()
    sumOfGamePowers(input).println()
}
