package solutions._2023

import LongUpdateCoordinate
import solutions._2023.SpaceTile.Empty
import solutions._2023.SpaceTile.Galaxy
import kotlin.math.abs

typealias GalaxyGrid = MutableList<MutableList<SpaceTile>>

const val GALAXY = '#'

sealed class SpaceTile(val char: Char) {
    abstract val coordinate: LongUpdateCoordinate
    data class Empty(override val coordinate: LongUpdateCoordinate) : SpaceTile('.') {
        override fun toString() = coordinate.toString()
    }
    data class Galaxy(override val coordinate: LongUpdateCoordinate) : SpaceTile(GALAXY) {
        override fun toString() = coordinate.toString()
        fun manhattanDistance(otherGalaxy: Galaxy) = abs(coordinate.y - otherGalaxy.coordinate.y) + abs(coordinate.x - otherGalaxy.coordinate.x)
    }
    fun prettyPrint() = char.toString()
}

fun calculateSumOfGalaxyDistances(input: Sequence<String>) {
    val galaxies = mutableMapOf<Galaxy, MutableSet<Galaxy>>()
    val galaxyGrid = MutableList(input.count()) { y ->
        MutableList(input.first().length) { x ->
            val char = input.elementAt(y)[x]
            val coordinate = LongUpdateCoordinate(y = y.toLong(), x = x.toLong())

            if (char == GALAXY) {
                val galaxy = Galaxy(coordinate)

                galaxies[galaxy] = galaxies.values.lastOrNull()?.toMutableSet() ?: mutableSetOf()
                galaxies.entries.forEach { if (it.key != galaxy) it.value.add(galaxy)}

                galaxy
            } else {
                Empty(coordinate)
            }
        }
    }

    val toExpandInY = galaxyGrid.mapIndexedNotNull { index, spaceTiles -> if (spaceTiles.all { it is Empty }) index else null }
    val toExpandInX = mutableSetOf<Int>()

    for (x in 0..galaxyGrid.first().lastIndex) {
        if (galaxyGrid.map { it[x] }.all { it is Empty }) {
            toExpandInX.add(x)
        }
    }

    val multipier = 999999L

    for (galaxy in galaxies.keys) {
        val toAddInY = toExpandInY.count { y -> galaxy.coordinate.y > y } * multipier
        val toAddInX = toExpandInX.count { x -> galaxy.coordinate.x > x } * multipier
        galaxy.coordinate.y += toAddInY
        galaxy.coordinate.x += toAddInX
    }

    val sumOfDistance = galaxies.entries.sumOf { galaxyMap ->
        galaxyMap.value.sumOf { it.manhattanDistance(galaxyMap.key) }
    }

    println()
    println("The sum of lengths is $sumOfDistance")
}