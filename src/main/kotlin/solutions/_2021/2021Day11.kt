package solutions._2021

import java.lang.Exception

typealias OctopusGrid = Array<Array<Octopus>>

class Octopus(
    val y: Int,
    val x: Int,
    var value: Int,
) {
    fun increase() {
        value++
        if (value > 9) {
            value = 0
        }
    }

    fun getSquaresAround(octopusGrid: OctopusGrid): List<Octopus> {
        val squareGrid = mutableListOf<Octopus>()
        for (yb in (y - 1)..(y + 1)) {
            for (xb in (x - 1)..(x + 1)) {
                try {
                    squareGrid.add(octopusGrid[yb][xb])
                } catch (e: Exception) {
                    // Do nothing
                }
            }
        }
        return squareGrid
    }
}

fun calculateOctopusFlashes(input: Sequence<String>) {
    val octopus = input.toList()
    val octopusGrid: OctopusGrid = Array(octopus.size) { y ->
        val octopusLine = octopus[y]
        Array(octopusLine.length) { x ->
            Octopus(y = y, x = x, value = octopusLine[x].digitToInt())
        }
    }

    var totalFlashes = 0

    repeat(100) {
        totalFlashes += getFlashesPerStep(octopusGrid)
    }

    println(totalFlashes)
}

fun calculateOctopusSameFlash(input: Sequence<String>) {
    val octopus = input.toList()
    val octopusGrid: OctopusGrid = Array(octopus.size) { y ->
        val octopusLine = octopus[y]
        Array(octopusLine.length) { x ->
            Octopus(y = y, x = x, value = octopusLine[x].digitToInt())
        }
    }

    var steps = 0

    while (!octopusGrid.flatten().all { it.value == 0 }) {
        getFlashesPerStep(octopusGrid)
        steps++
    }

    println(steps)
}

fun getFlashesPerStep(octopusGrid: OctopusGrid): Int {
    var flashes =  0
    var shouldFlashList = mutableListOf<Octopus>()

    octopusGrid
        .flatten()
        .forEach {
            it.increase()
            if (it.value == 0) {
                flashes++
                shouldFlashList.add(it)
            }
        }

    while (shouldFlashList.isNotEmpty()) {
        val tempShouldFlashList = mutableListOf<Octopus>()
        for (square in shouldFlashList) {
            square.getSquaresAround(octopusGrid).forEach {
                if (it.value > 0) {
                    it.increase()
                    if (it.value == 0) {
                        flashes++
                        tempShouldFlashList.add(it)
                    }
                }
            }
        }
        shouldFlashList = tempShouldFlashList
    }

    return flashes
}
