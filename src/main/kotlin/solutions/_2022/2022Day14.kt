package solutions._2022

import Coordinate
import solutions._2022.Type.*

typealias TileGrid = Array<MutableList<Tile>>

enum class Type(val char: String) {
    AIR("."),
    ROCK("#"),
    SAND("o"),
    SAND_START("+");

    fun isFree() = this == AIR
}

data class Tile(var type: Type) {
    override fun toString(): String = type.char
}

data class TileLimits(var maxX: Int = 0, var maxY: Int = 0, var minX: Int = Int.MAX_VALUE) {

    fun isCoordinateOutOfLimits(coordinate: UpdateCoordinate) = coordinate.y > maxY
            || coordinate.x > maxX
            || coordinate.x < minX
}
data class UpdateCoordinate(var x: Int, var y: Int) {

    fun isBlocked(tileGrid: TileGrid): Boolean {
        return when {
            tileGrid[y + 1][x].type.isFree() -> { y++; false }
            tileGrid[y + 1][x - 1].type.isFree() -> { y++; x--; false }
            tileGrid[y + 1][x + 1].type.isFree() -> { y++; x++; false }
            else -> true
        }
    }
}

fun findLastSandDropBeforeFallingIntoAbyss(input: Sequence<String>) {
    val limits = TileLimits()
    val sandStart = UpdateCoordinate(x = 50, y = 0)

    val rockCoordinates = input.createRockCoordinates(limits)

    val tileGrid = TileGrid(limits.maxY + 6) { MutableList(limits.maxX + 6) { Tile(AIR) } }.apply {
        get(sandStart.y)[sandStart.x].type = SAND_START
    }

    rockCoordinates.drawRocks(tileGrid)
    tileGrid.print()

    var dropBlocked = true
    var dropNumber = 1

    while(dropBlocked) {
        dropBlocked = doesSandDropTillBlocked(dropNumber++, sandStart, tileGrid, limits)
    }
}

fun findSandImpossibleToDrop(input: Sequence<String>) {
    val limits = TileLimits()
    val sandStart = UpdateCoordinate(x = 50, y = 0)

    val rockCoordinates = input.createRockCoordinates(limits)

    val tileGrid = TileGrid(limits.maxY + 3) { MutableList(limits.maxX + 6) { Tile(AIR) } }.apply {
        get(sandStart.y)[sandStart.x].type = SAND_START
        last().forEach { it.type = ROCK }
    }

    rockCoordinates.drawRocks(tileGrid)
    tileGrid.print()

    var dropNumber = 1

    while (tileGrid[sandStart.y][sandStart.x].type != SAND) {
        dropSandTillBlocked(dropNumber++, sandStart, tileGrid)
    }
}

fun doesSandDropTillBlocked(dropNumber: Int, sandStart: UpdateCoordinate, tileGrid: TileGrid, limits: TileLimits): Boolean {
    println("Sand drop: $dropNumber")
    var blocked = false
    val sandPosition = UpdateCoordinate(sandStart.x, sandStart.y)

    while (!blocked) {
        if (limits.isCoordinateOutOfLimits(sandPosition)) {
            tileGrid.addSand(sandPosition)
            return false
        }

        blocked = sandPosition.isBlocked(tileGrid)
    }
    tileGrid.addSand(sandPosition)
    return true
}

fun dropSandTillBlocked(dropNumber: Int, sandStart: UpdateCoordinate, tileGrid: TileGrid) {
    println("Sand drop: $dropNumber")
    var blocked = false
    val sandPosition = UpdateCoordinate(sandStart.x, sandStart.y)

    while (!blocked) {
        when {
            sandPosition.y + 1 == tileGrid.lastIndex -> { blocked = true; continue }
            sandPosition.x - 1 < 0 -> {
                tileGrid.forEach { it.add(0, Tile(AIR)) }
                tileGrid.last().first().type = ROCK
                sandStart.x++
                sandPosition.x = 1
            }
            sandPosition.x + 1 > tileGrid.first().lastIndex -> {
                tileGrid.forEach { it.add(Tile(AIR)) }
                tileGrid.last().last().type = ROCK
            }
        }

        blocked = sandPosition.isBlocked(tileGrid)
    }
    tileGrid.addSand(sandPosition)
}

fun TileGrid.addSand(sandPosition: UpdateCoordinate) {
    get(sandPosition.y)[sandPosition.x].type = SAND
    print()
}

fun TileGrid.print() {
    forEach { println(it.joinToString("") { it.toString() }) }
    println()
}

fun Sequence<String>.createRockCoordinates(limits: TileLimits) =
    toList().map { raw ->
        raw.split(" -> ")
            .map { rawCoordinate ->
                rawCoordinate.split(",").let {
                    val x = it.first().toInt() - 450
                    val y = it.last().toInt()

                    if (x < limits.minX) limits.minX = x
                    if (x > limits.maxX) limits.maxX = x
                    if (y > limits.maxY) limits.maxY = y

                    Coordinate(x = x, y = y)
                }
            }
    }

fun List<List<Coordinate>>.drawRocks(tileGrid: TileGrid) {
    forEach { coordinates ->
        for (i in 0 until coordinates.lastIndex) {
            val start = coordinates[i]
            val end = coordinates[i + 1]

            when {
                start.y == end.y -> {
                    val range = if (start.x > end.x) end.x..start.x else start.x..end.x
                    range.forEach { x-> tileGrid[start.y][x].type = ROCK }
                }
                start.x == end.x -> {
                    val range = if (start.y > end.y) end.y..start.y else start.y..end.y
                    range.forEach { y -> tileGrid[y][start.x].type = ROCK }
                }
            }
        }
    }
}
