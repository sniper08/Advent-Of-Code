package solutions._2015

import solutions._2021.Coordinate

const val ON = '#'
const val OFF = '.'

typealias SantaLightGrid = Array<Array<SantaLight>>

data class SantaLight(
    val x: Int,
    val y: Int,
    var status: Boolean
) {
    private val xRange = (x - 1)..(x + 1)
    private val yRange = (y - 1)..(y + 1)
    private var nextStatus: Boolean = status

    private fun findNeighbours(santaLightGrid: SantaLightGrid): List<SantaLight> {
        val neighbours = mutableListOf<SantaLight>()
        for (xB in xRange) {
            for (yB in yRange) {
                val canAdd = !(x == xB && y == yB)

                try {
                    if (canAdd) neighbours.add(santaLightGrid[yB][xB])
                } catch (e: Exception) {
                    // Do nothing
                }
            }
        }
        return neighbours
    }

    fun updateNextStatus(santaLightGrid: SantaLightGrid) {
        val neighboursOn = findNeighbours(santaLightGrid).count { it.status }
        nextStatus = if (status) {
            neighboursOn in 2..3
        } else {
            neighboursOn == 3
        }
    }

    fun finishUpdate() {
        status = nextStatus
    }
}

fun calculateLightOnSantaGrid(input: Sequence<String>) {
    val santaGrid = createGrid(input.toList())

    println("Initial State:")
    santaGrid.print()

    val allSantaLight = santaGrid.flatten()
    val steps = 100

    allSantaLight.animateLights(steps, santaGrid)

    println("\n${allSantaLight.count { it.status }} lights on after $steps steps")
}

fun calculateLightOnSantaGridFixed(input: Sequence<String>) {
    val santaGrid = createGrid(input.toList(), cornersOn = true)

    println("Initial State:")
    santaGrid.print()

    val allSantaLight = santaGrid.flatten()
    val steps = 100

    allSantaLight.animateLights(steps, santaGrid, cornersStuck = true)

    println("\n${allSantaLight.count { it.status }} lights on after $steps steps")
}

fun List<SantaLight>.animateLights(steps: Int, santaLightGrid: SantaLightGrid, cornersStuck: Boolean = false) {
    val corners = findCorners(santaLightGrid.first().lastIndex, santaLightGrid.lastIndex)

    repeat(steps) { step ->
        forEach {
            val canAnimate = !cornersStuck || !corners.contains(Coordinate(it.x, it.y))
            if (canAnimate) {
                it.updateNextStatus(santaLightGrid)
            }
        }
        forEach { it.finishUpdate() }

        println("\nAfter step ${step + 1}:")
        santaLightGrid.print()
    }
}

fun createGrid(input: List<String>, cornersOn: Boolean = false) : SantaLightGrid {
    val corners = findCorners(input.first().lastIndex, input.lastIndex)

    return SantaLightGrid(input.size) { y ->
        Array(input.first().length) { x ->
            val char = input[y][x]
            val status = (cornersOn && corners.contains(Coordinate(x, y))) || char == ON

            SantaLight(x, y, status)
        }
    }
}

fun SantaLightGrid.print() {
    forEach { line ->
        println(line.joinToString("") { if (it.status) ON.toString() else OFF.toString() })
    }
}

fun findCorners(maxX: Int, maxY: Int) = setOf(
    Coordinate(0, 0),
    Coordinate(maxX, 0),
    Coordinate(0, maxY),
    Coordinate(maxX, maxY)
)
