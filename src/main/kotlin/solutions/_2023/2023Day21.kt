package solutions._2023

import Coordinate
import solutions._2023.GardenTile.Plot
import solutions._2023.GardenTile.Rock
import kotlin.math.pow

private typealias Garden = Array<Array<GardenTile>>

data class GardenSettings(val garden: Garden, val allPlots: Set<Plot>, val start: Plot)

sealed class GardenTile(open val coordinate: Coordinate, private val symbol: String) {

    data class Rock(override val coordinate: Coordinate) : GardenTile(coordinate, "#")

    data class Plot(
        override val coordinate: Coordinate,
        private val isStart: Boolean = false
    ) : GardenTile(coordinate, ".") {
        var top: Plot? = null
        var left: Plot? = null
        var right: Plot? = null
        var bottom: Plot? = null

        val paths get() = listOfNotNull(top, left, right, bottom)

        var distanceFromMiddle = -1
            private set

        fun mapPaths(garden: Garden) {
            if (top == null) {
                top = garden.getOrNull(coordinate.y - 1)?.getOrNull(coordinate.x).asPlot()
                top?.bottom = this
            }
            if (left == null) {
                left = garden.getOrNull(coordinate.y)?.getOrNull(coordinate.x - 1).asPlot()
                left?.right = this
            }
            if (right == null) {
                right = garden.getOrNull(coordinate.y)?.getOrNull(coordinate.x + 1).asPlot()
                right?.left = this
            }
            if (bottom == null) {
                bottom = garden.getOrNull(coordinate.y + 1)?.getOrNull(coordinate.x).asPlot()
                bottom?.top = this
            }
        }

        fun setDistanceFromMiddle(neighbourDistance: Int): Boolean {
            if (distanceFromMiddle < 0) {
                distanceFromMiddle = neighbourDistance + 1
                return true
            }
            return false
        }
    }

    fun GardenTile?.asPlot() = this as? Plot
}

fun createGarden(input: Sequence<String>): GardenSettings {
    var start = Plot(Coordinate(-1, -1))
    val allPlots = mutableSetOf<Plot>()
    val garden = Garden(input.count()) { y ->
        Array(input.first().length) { x ->
            val char = input.elementAt(y)[x]
            val coordinate = Coordinate(y = y, x = x)

            when (char) {
                '.' -> Plot(coordinate).also { allPlots.add(it) }
                'S' -> Plot(coordinate, isStart = true)
                    .also {
                        it.setDistanceFromMiddle(-1)
                        start = it
                        allPlots.add(it)
                    }
                '#' -> Rock(coordinate)
                else -> throw Exception("Not valid")
            }
        }
    }
    return GardenSettings(garden, allPlots, start)
}

fun Garden.print() {
    forEach {
        println(it.joinToString("") { it.toString() })
    }
}

// TODO disappointed that I could find an answer myself
fun findNumberOrPossibleVisitedPlots(input: Sequence<String>) {
    val gardenSettings = createGarden(input)
    gardenSettings.allPlots.forEach { it.mapPaths(gardenSettings.garden) }

    val calculateDistancePlots = mutableSetOf<Plot>(gardenSettings.start)

    while (calculateDistancePlots.isNotEmpty()) {
        val newCalculateDistancePlots = mutableSetOf<Plot>()
        for (plot in calculateDistancePlots) {
            plot.paths.forEach {
                val toAdd = it.setDistanceFromMiddle(plot.distanceFromMiddle)
                if (toAdd) {
                    newCalculateDistancePlots.add(it)
                }
            }
        }
        calculateDistancePlots.clear()
        calculateDistancePlots.addAll(newCalculateDistancePlots)
    }

    println(gardenSettings.allPlots.size)
    println(gardenSettings.allPlots.filter { it.distanceFromMiddle < 0 }.size)

    val even = gardenSettings.allPlots.filter { it.distanceFromMiddle % 2 == 0 }

    // Needed to be able to detect that there are 2 left out plots, sneaky bastards :'D
    val odd = gardenSettings.allPlots.filter { it.distanceFromMiddle % 2 == 1 }
    val evenOver65 = even.filter { it.distanceFromMiddle > 65 }
    val oddOver65 = odd.filter { it.distanceFromMiddle > 65 }

    val steps = 26501365L
    val n: Long = steps / gardenSettings.garden.size

    println(n)

    val oddMapsTotal = (n + 1) * (n + 1) * odd.size
    val evenMapsTotal = (n * n) * even.size
    val cutForOdd = (n + 1) * oddOver65.size
    val addForEven = n * evenOver65.size
    val total = oddMapsTotal + evenMapsTotal - cutForOdd + addForEven

    println(total)
}


