package solutions._2021

import kotlin.math.max
import kotlin.math.min

data class Cube(
    val xRange: IntRange,
    val yRange: IntRange,
    val zRange: IntRange,
    val on: Boolean
) {
    companion object {
        const val ON = "on"
        const val OFF = "off"
    }

    fun volume() = xRange.count().toLong() * yRange.count().toLong() * zRange.count().toLong()

    fun intersection(other: Cube): Cube? =
        if (xRange.first > other.xRange.last
            || xRange.last < other.xRange.first
            || yRange.first > other.yRange.last
            || yRange.last < other.yRange.first
            || zRange.first > other.zRange.last
            || zRange.last < other.zRange.first) {
            null
        } else {
            Cube(
                xRange = max(xRange.first, other.xRange.first)..min(xRange.last, other.xRange.last),
                yRange = max(yRange.first, other.yRange.first)..min(yRange.last, other.yRange.last),
                zRange = max(zRange.first, other.zRange.first)..min(zRange.last, other.zRange.last),
                on = !on
            )
        }

    override fun toString(): String {
        return "${if (on) ON else OFF} x=$xRange, y=$yRange, z=$zRange -- Volume: ${volume()}"
    }
}

fun calculateReactorCubesCount(input: Sequence<String>) {
    val startingCubes = input.toList().map { parseCubes(it) }
    val offIntersections = mutableListOf<Cube>()

    var totalOn = startingCubes.withIndex().fold(0L) { acc, cube ->
        println("${cube.value}")

        offIntersections.addAll(offIntersections.mapNotNull { it.intersection(cube.value) })
        offIntersections.addAll(
            startingCubes.subList(0, cube.index)
                .filter { it.on }
                .mapNotNull { it.intersection(cube.value) }
        )

        acc + if (cube.value.on) cube.value.volume() else 0L
    }

    offIntersections.forEach { totalOn += if (it.on) it.volume() else it.volume() * -1L }
    println("Total on: $totalOn")
}

fun parseCubes(cube: String): Cube {
    return cube
        .replace(" ", "")
        .split("..", "x=", ",y=", ",z=")
        .let {
            Cube(
                xRange = it[1].toInt()..it[2].toInt(),
                yRange = it[3].toInt()..it[4].toInt(),
                zRange = it[5].toInt()..it[6].toInt(),
                on = it[0] == Cube.ON
            )
        }
}
