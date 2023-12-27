package solutions._2023

import ANSI_CYAN
import ANSI_RED
import ANSI_RESET
import ANSI_YELLOW
import Coordinate

enum class BeamDirection(val direction: Char) { N('^'), S('v'), W('<'), E('>') }
typealias BeamPath = Array<Array<BeamPathTile>>

data class LitPath(
    val path: BeamPath,
    val y: Int,
    val x: Int,
    val energized: Int
)

fun BeamPath.print() {
    forEach {
        println(
            it.joinToString("") { tile ->
                when {
                    tile.energized && tile.type == BeamPathType.EMPTY -> ANSI_RED + tile.toString() + ANSI_RESET
                    tile.energized -> ANSI_CYAN + tile.toString() + ANSI_RESET
                    tile.type == BeamPathType.EMPTY -> tile.toString()
                    else -> ANSI_YELLOW + tile.toString() + ANSI_RESET
                }
            }
        )
    }
}

enum class BeamPathType(val value: Char) { EMPTY('.'), MIRROR_RIGHT('/'), MIRROR_LEFT('\\'), SPLITTER_HORIZONTAL('-'), SPLITTER_VERTICAL('|') }

data class BeamPathTile(
    val type: BeamPathType,
    val coordinate: Coordinate,
) {

    private var lastBeamDirection: BeamDirection? = null
    val nextDirections = mutableSetOf<BeamDirection>()

    private var timesEnergizedNorth = 0
    private var timesEnergizedSouth = 0
    private var timesEnergizedWest = 0
    private var timesEnergizedEast = 0

    private val energizedCount get() = timesEnergizedNorth + timesEnergizedSouth + timesEnergizedWest + timesEnergizedEast
    val energized get() = energizedCount > 0

    private fun north(path: BeamPath) = path.getOrNull(coordinate.y - 1)?.getOrNull(coordinate.x)?.also { it.nextDirections.add(BeamDirection.N) }
    private fun south(path: BeamPath) = path.getOrNull(coordinate.y + 1)?.getOrNull(coordinate.x)?.also { it.nextDirections.add(BeamDirection.S) }
    private fun east(path: BeamPath) = path.getOrNull(coordinate.y)?.getOrNull(coordinate.x + 1)?.also { it.nextDirections.add(BeamDirection.E) }
    private fun west(path: BeamPath) = path.getOrNull(coordinate.y)?.getOrNull(coordinate.x - 1)?.also { it.nextDirections.add(BeamDirection.W) }

    fun energize(path: BeamPath): Set<BeamPathTile> {
        val next = mutableSetOf<BeamPathTile>()
        for (beamDirection in nextDirections) {
            when (beamDirection) {
                BeamDirection.N -> {
                    if (timesEnergizedNorth == 0) {
                        timesEnergizedNorth = 1
                        when (type) {
                            BeamPathType.EMPTY,
                            BeamPathType.SPLITTER_VERTICAL -> north(path)?.let(next::add)
                            BeamPathType.MIRROR_RIGHT -> east(path)?.let(next::add)
                            BeamPathType.MIRROR_LEFT -> west(path)?.let(next::add)
                            BeamPathType.SPLITTER_HORIZONTAL -> {
                                west(path)?.let(next::add)
                                east(path)?.let(next::add)
                            }
                        }
                    }
                }
                BeamDirection.S -> {
                    if (timesEnergizedSouth == 0) {
                        timesEnergizedSouth = 1
                        when (type) {
                            BeamPathType.EMPTY,
                            BeamPathType.SPLITTER_VERTICAL -> south(path)?.let(next::add)
                            BeamPathType.MIRROR_RIGHT -> west(path)?.let(next::add)
                            BeamPathType.MIRROR_LEFT -> east(path)?.let(next::add)
                            BeamPathType.SPLITTER_HORIZONTAL -> {
                                west(path)?.let(next::add)
                                east(path)?.let(next::add)
                            }
                        }
                    }
                }
                BeamDirection.W -> {
                    if (timesEnergizedWest == 0) {
                        timesEnergizedWest = 1
                        when (type) {
                            BeamPathType.EMPTY,
                            BeamPathType.SPLITTER_HORIZONTAL -> west(path)?.let(next::add)
                            BeamPathType.MIRROR_RIGHT -> south(path)?.let(next::add)
                            BeamPathType.MIRROR_LEFT -> north(path)?.let(next::add)
                            BeamPathType.SPLITTER_VERTICAL -> {
                                north(path)?.let(next::add)
                                south(path)?.let(next::add)
                            }
                        }
                    }
                }
                BeamDirection.E -> {
                    if (timesEnergizedEast == 0) {
                        timesEnergizedEast = 1
                        when (type) {
                            BeamPathType.EMPTY,
                            BeamPathType.SPLITTER_HORIZONTAL -> east(path)?.let(next::add)
                            BeamPathType.MIRROR_RIGHT -> north(path)?.let(next::add)
                            BeamPathType.MIRROR_LEFT -> south(path)?.let(next::add)
                            BeamPathType.SPLITTER_VERTICAL -> {
                                north(path)?.let(next::add)
                                south(path)?.let(next::add)
                            }
                        }
                    }
                }
            }
            lastBeamDirection = beamDirection
        }
        nextDirections.clear()
        return next
    }

    override fun toString(): String =
        when {
            type == BeamPathType.EMPTY && energizedCount > 1 -> energizedCount.toString()
            type == BeamPathType.EMPTY && energized -> lastBeamDirection?.direction?.toString() ?: BeamPathType.EMPTY.value.toString()
            else -> type.value.toString()
        }
}

fun calculateEnergizedTiles(input: Sequence<String>) {
    val energized = runLightBeam(createPath(input), y = 0, x = 0, beamDirection = BeamDirection.E)

    energized.path.print()
    println("Total energized ${energized.energized}")
}

fun calculateEnergizedTilesFromAnyPoint(input: Sequence<String>) {
    var highest = LitPath(BeamPath(0) { arrayOf() }, y = -1, x = -1, energized = Int.MIN_VALUE)

    val lastX = input.first().lastIndex
    val lastY = input.count() - 1

    for (y in 0..lastY) {
        if (y == 0) {
            for (x in 0..lastX) {
                val energized = runLightBeam(createPath(input), y = y, x = x, beamDirection = BeamDirection.S)
                if (energized.energized > highest.energized) highest = energized
            }
        }

        val energizedIn0 = runLightBeam(createPath(input), y = y, x = 0, beamDirection = BeamDirection.E)
        if (energizedIn0.energized > highest.energized) highest = energizedIn0

        val energizedInLast = runLightBeam(createPath(input), y = y, x = lastX, beamDirection = BeamDirection.W)
        if (energizedInLast.energized > highest.energized) highest = energizedInLast

        if (y == lastY) {
            for (x in 0..lastX) {
                val energized = runLightBeam(createPath(input), y = y, x = x, beamDirection = BeamDirection.N)
                if (energized.energized > highest.energized) highest = energized
            }
        }
    }

    highest.path.print()
    println("In Y = ${highest.y} --- In X = ${highest.x}")
    println("Max energized achievable ${highest.energized}")
}

private fun runLightBeam(path: BeamPath, y: Int, x: Int, beamDirection: BeamDirection): LitPath {
    val stackToEnergize = mutableSetOf<BeamPathTile>(path[y][x].also { it.nextDirections.add(beamDirection) })
    var energized = 0

    while (stackToEnergize.isNotEmpty()) { //repeat(80) {
        val nextStackToEnergize = mutableSetOf<BeamPathTile>()
        stackToEnergize.forEach { toEnergize ->
            if (!toEnergize.energized) energized++
            nextStackToEnergize.addAll(toEnergize.energize(path))
        }
        stackToEnergize.clear()
        stackToEnergize.addAll(nextStackToEnergize)
    }

    return LitPath(path, y, x, energized)
}

private fun createPath(input: Sequence<String>) = BeamPath(input.count()) { y ->
    Array(input.first().length) { x ->
        val char = input.elementAt(y)[x]
        val type = when (char) {
            '.' -> BeamPathType.EMPTY
            '/' -> BeamPathType.MIRROR_RIGHT
            '\\' -> BeamPathType.MIRROR_LEFT
            '-' -> BeamPathType.SPLITTER_HORIZONTAL
            '|' -> BeamPathType.SPLITTER_VERTICAL
            else -> throw Exception("NOT VALID")
        }
        BeamPathTile(type, Coordinate(y = y, x = x))
    }
}