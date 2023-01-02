package solutions._2022

import solutions._2022.GridCube.*
import kotlin.math.abs

typealias Obsidian = Array<Array<Array<GridCube>>>

interface Cube {
    val x: Int
    val y: Int
    val z: Int

    fun isAdjacent(other: Cube): Boolean =
        when {
            x == other.x && y == other.y -> abs(z - other.z) == 1
            x == other.x && z == other.z -> abs(y - other.y) == 1
            y == other.y && z == other.z -> abs(x - other.x) == 1
            else -> false
        }
}

sealed class GridCube {
    object Empty : GridCube()
    data class CubeDroplet(override val x: Int, override val y: Int, override val z: Int) : GridCube(), Cube
    data class AirCube(override val x: Int, override val y: Int, override val z: Int) : GridCube(), Cube
}

fun calculateTotalSurfaceArea(input: Sequence<String>) {
    val cubes = input.createDroplet()

    val allSides = cubes.size * 6
    var adjacent = 0

    for ((i, cube) in cubes.withIndex()) {
        val checkList = cubes.subList(i, cubes.size)

        for (checkCube in checkList) {
            if (cube.isAdjacent(checkCube)) adjacent += 2
        }
    }

    println("Adjacent = $adjacent")
    println("All sides = $allSides")
    println("Total surface area = ${allSides - adjacent}")
}

fun calculateTotalSurfaceAreaCorrected(input: Sequence<String>) {
    val cubes = input.createDroplet().toMutableList()
    val airCubes = mutableSetOf<AirCube>()
    val allSides = cubes.size * 6

    val obsidianSize = listOf(cubes.maxOf { it.x }, cubes.maxOf { it.y }, cubes.maxOf { it.z }).max() + 1
    val obsidian = Array(obsidianSize) { x ->
        Array(obsidianSize) { y ->
            Array<GridCube>(obsidianSize) { z -> AirCube(x, y, z) }
        }
    }

    for (cube in cubes) { obsidian[cube.x][cube.y][cube.z] = cube }
    obsidian.normalizeRangesPerX()
    obsidian.normalizeTransversal()
    obsidian.normalizeTransversal()
    obsidian.normalizeInXPerCross()

    airCubes.addAll(obsidian.getAllAirCubes())

    var adjacent = 0

    for ((i, cube) in cubes.withIndex()) {
        val checkList = cubes.subList(i, cubes.size)

        for (checkCube in checkList) {
            if (cube.isAdjacent(checkCube)) adjacent += 2
        }

        for (airCube in airCubes) {
            if (cube.isAdjacent(airCube)) adjacent++
        }
    }

    println("Adjacent = $adjacent")
    println("All sides = $allSides")
    println("Total surface area = ${allSides - adjacent}")
}

fun Obsidian.normalizeRangesPerX() {
    forEach { inX ->
        for (y in inX.indices) {
            val inY = inX[y]
            val firstIndex = inY.indexOfFirst { it is CubeDroplet }
            val lastIndex = inY.indexOfLast { it is CubeDroplet }

            if (firstIndex > -1) {
                for (z in 0 until firstIndex) {
                    inY[z] = Empty
                }
            }

            if (lastIndex > -1 && lastIndex + 1 <= size) {
                for (z in (lastIndex + 1)..inY.lastIndex) {
                    inY[z] = Empty
                }
            }

            if (firstIndex < 0 && lastIndex < 0) {
                for (z in inY.indices) {
                    inY[z] = Empty
                }
            }
        }
    }
}

fun Obsidian.normalizeTransversal() {
    // cleans checking next
    forEachIndexed { x, inX ->
        val airCubesInX = inX.flatten().filterIsInstance<AirCube>()

        for (airCube in airCubesInX) {
            val nextX = x + 1

            if (nextX <= lastIndex && get(nextX)[airCube.y][airCube.z] == Empty) {
                get(x)[airCube.y][airCube.z] = Empty
            }
        }
    }

    // cleans checking previous
    forEachIndexed { x, inX ->
        val airCubesInX = inX.flatten().filterIsInstance<AirCube>()

        for (airCube in airCubesInX) {
            val previousX = x - 1

            if (previousX >= 0 && get(previousX)[airCube.y][airCube.z] == Empty) {
                get(x)[airCube.y][airCube.z] = Empty
            }
        }
    }
}

fun Obsidian.normalizeInXPerCross() {
    // cleans up and down
    getAllAirCubes().forEach {
            if (
                get(it.x).getOrNull(it.y - 1)?.getOrNull(it.z) == Empty
                || get(it.x).getOrNull(it.y + 1)?.getOrNull(it.z) == Empty
            ) {
                get(it.x)[it.y][it.z] = Empty
            }
        }

    // cleans right and left
    getAllAirCubes().forEach {
            if (
                get(it.x).getOrNull(it.y)?.getOrNull(it.z - 1) == Empty
                || get(it.x).getOrNull(it.y)?.getOrNull(it.z + 1) == Empty
            ) {
                get(it.x)[it.y][it.z] = Empty
            }
        }
}

fun Obsidian.getAllAirCubes() = flatten().flatMap { it.toList() }.filterIsInstance<AirCube>()


fun Sequence<String>.createDroplet() = toList()
    .map {
        val raw = it.split(",")
        CubeDroplet(x = raw[0].toInt(), y = raw[1].toInt(), z = raw[2].toInt())
    }

