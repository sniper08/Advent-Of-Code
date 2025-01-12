package solutions._2024

import ANSI_RESET
import Coordinate
import LinearDirection.*
import AllSidesDirection.*
import createAnsi
import day.Day
import solutions._2024.Year2024Day12.GardenPlot
import utils.Grid
import utils.GridElement

typealias Garden = Grid<GardenPlot>

class Year2024Day12 : Day {

    /**
     * Calculate price per garden region, area * perimeter, perimeter counts every garden plot side
     */
    override fun part1(input: Sequence<String>): String {
        val garden = createGarden(input)
       // garden.print()

        val allGardenPlots = garden.flatten()
        var nextUncounted = allGardenPlots.firstOrNull { !it.counted }
        var totalPrice = 0L

        while (nextUncounted != null) {
            val region = Region(plantType = nextUncounted.plantType)
            nextUncounted.computeRegion(garden = garden, region = region)
            totalPrice += region.price()

           // println(region)

            nextUncounted = allGardenPlots.firstOrNull { !it.counted }
        }

        return "$totalPrice"
    }

    /**
     * Calculate price per garden region with discount, area * perimeter, perimeter counts every region side
     */
    override fun part2(input: Sequence<String>): String {
        val garden = createGarden(input)
       // garden.print()

        val allGardenPlots = garden.flatten()
        var nextUncounted = allGardenPlots.firstOrNull { !it.counted }
        var totalPrice = 0L

        while (nextUncounted != null) {
            val region = Region(plantType = nextUncounted.plantType)
            nextUncounted.computeRegionWithDiscount(garden = garden, region = region)
            totalPrice += region.price()

           // println(region)

            nextUncounted = allGardenPlots.firstOrNull { !it.counted }
        }

        return "$totalPrice"
    }

    private fun createGarden(input: Sequence<String>) = Garden(input = input) { coordinate, rawChar ->
        GardenPlot(
            plantType = rawChar,
            coordinate = coordinate
        )
    }

    private fun Garden.print() {
        val ansiList = (15..231 step 6).map {
            createAnsi(number = it)
        }.toSet()

        var ansiIndex = 0
        val ansiPerPlantType = mutableMapOf<Char, Int>()

        print { currentGardenPlot ->
            val ansiIndexPerPlantType = ansiPerPlantType.getOrPut(
                key = currentGardenPlot.plantType,
                defaultValue = {
                    ansiIndex = (ansiIndex + 1) % ansiList.size
                    ansiIndex
                }
            )

            "${ansiList.elementAt(ansiIndexPerPlantType)}$currentGardenPlot$ANSI_RESET"
        }
    }

    data class GardenPlot(
        val plantType: Char,
        override val coordinate: Coordinate
    ) : GridElement {
        var counted = false
            private set

        private var fenceAtNorth = false
        private var fenceAtWest = false
        private var fenceAtEast = false
        private var fenceAtSouth = false

        fun computeRegion(garden: Garden, region: Region) {
            counted = true
            region.area++

            val neighbours = garden.findLinearNeighbours(coordinate = coordinate)

            for (neighbour in neighbours) {
                val direction = neighbour.key
                val neighbourGardenPlot = neighbour.value

                when (direction) {
                    NORTH -> if (fenceAtNorth) { region.perimeter++ ; continue }
                    WEST -> if (fenceAtWest) { region.perimeter++ ; continue }
                    EAST -> if (fenceAtEast) { region.perimeter++ ; continue }
                    SOUTH -> if (fenceAtSouth) { region.perimeter++ ; continue }
                }

                if (neighbourGardenPlot?.plantType == plantType) {
                    if (!neighbourGardenPlot.counted) {
                        neighbourGardenPlot.computeRegion(garden = garden, region = region)
                    }
                } else {
                    when (direction) {
                        NORTH -> { fenceAtNorth = true ; neighbourGardenPlot?.fenceAtSouth = true }
                        WEST -> { fenceAtWest = true ; neighbourGardenPlot?.fenceAtEast = true }
                        EAST -> { fenceAtEast = true ; neighbourGardenPlot?.fenceAtWest = true }
                        SOUTH -> { fenceAtSouth = true ; neighbourGardenPlot?.fenceAtNorth = true }
                    }
                    region.perimeter++
                }
            }
        }

        fun computeRegionWithDiscount(garden: Garden, region: Region) {
            counted = true
            region.area++

            val neighbours = garden.findAllNeighbours(coordinate = coordinate)

            val northWest = neighbours[NW]
            val north = neighbours[N]
            val northEast = neighbours[NE]
            val west = neighbours[W]
            val east = neighbours[E]
            val southWest = neighbours[SW]
            val south = neighbours[S]
            val southEast = neighbours[SE]

            val hasWestTheSamePlantType = west?.plantType == plantType
            val hasEastTheSamePlantType = east?.plantType == plantType

            if (north?.plantType == plantType) {
                if (!north.counted) north.computeRegionWithDiscount(garden = garden, region)
                if (hasWestTheSamePlantType && northWest?.plantType != plantType) region.perimeter++ // innerCorner NW
                if (hasEastTheSamePlantType && northEast?.plantType != plantType) region.perimeter++ // innerCorner NE
            } else {
                if (!hasWestTheSamePlantType) region.perimeter++ // outerCorner NW
                if (!hasEastTheSamePlantType) region.perimeter++ // outerCorner NE
            }

            if (hasWestTheSamePlantType && west?.counted == false) west.computeRegionWithDiscount(garden = garden, region = region)
            if (hasEastTheSamePlantType && east?.counted == false) east.computeRegionWithDiscount(garden = garden, region = region)

            if (south?.plantType == plantType) {
                if (!south.counted) south.computeRegionWithDiscount(garden = garden, region)
                if (hasWestTheSamePlantType && southWest?.plantType != plantType) region.perimeter++ // innerCorner SW
                if (hasEastTheSamePlantType && southEast?.plantType != plantType) region.perimeter++ // innerCorner SE
            } else {
                if (!hasWestTheSamePlantType) region.perimeter++ // innerCorner NW
                if (!hasEastTheSamePlantType) region.perimeter++ // outerCorner SE
            }
        }

        override fun toString(): String = "$plantType"
    }

    data class Region(
        val plantType: Char,
        var area: Long = 0,
        var perimeter: Long = 0
    ) {
        fun price() = area * perimeter

        override fun toString(): String = "$area * $perimeter = ${price()}"
    }
}
