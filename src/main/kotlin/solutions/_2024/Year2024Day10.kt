package solutions._2024

import ANSI_GREEN
import ANSI_RED
import ANSI_RESET
import Coordinate
import day.Day
import solutions._2024.Year2024Day10.MapSection
import solutions._2024.Year2024Day10.MapSection.Hill
import solutions._2024.Year2024Day10.MapSection.TrailHead
import utils.Grid

private typealias TopographicMap = Grid<MapSection>

class Year2024Day10 : Day {

    override val year: Int = 2024
    override val day: Int = 10

    /**
     * Find the sum of scores per hiking trail, a score is the amount of trails found per TrailHead at height 0
     * A Hiking Trail starts at height 0 and moves at increments of 1 till it reaches height 9
     */
    override fun part1(input: Sequence<String>): String {
        val trailHeads = mutableSetOf<TrailHead>()
        val map = crateTopographicMap(input, trailHeads)

        val nextByHeightPerHillMap = mutableMapOf<Hill, Set<Hill>>()
        val scoresSum = trailHeads.sumOf { trailHead ->
            trailHead.findTrailEnds(map = map, nextByHeightPerHillMap = nextByHeightPerHillMap).size
        }

        return "$scoresSum"
    }

    override fun part2(input: Sequence<String>): String {
        val trailHeads = mutableSetOf<TrailHead>()
        val map = crateTopographicMap(input, trailHeads)

        val nextByHeightPerHillMap = mutableMapOf<Hill, Set<Hill>>()
        val ratingsSum = trailHeads.sumOf { trailHead ->
            val trailEnds = trailHead.findTrailEnds(map = map, nextByHeightPerHillMap = nextByHeightPerHillMap)
                .onEach { it.key.isTrailEnd = true }

            println("Trail head at ${trailHead.coordinate}")
            trailEnds.forEach {
                println("${it.key.coordinate} -> ${it.value}")
            }
            val totalTrails = trailEnds.values.sum()
            println("Total trails: $totalTrails")

            map.print { mapSection ->
                val string = mapSection.toString()

                when {
                    mapSection == trailHead -> "$ANSI_GREEN$string$ANSI_RESET"
                    mapSection is Hill && trailEnds.contains(mapSection) -> "$ANSI_RED$string$ANSI_RESET"
                    else -> string
                }
            }
            println("-------------------------")
            totalTrails
        }

        return "$ratingsSum"
    }

    /**
     * Find the sum of ratings per hiking trail, a rating is the amount of different trails found per TrailHead
     * at height 0. A Hiking Trail starts at height 0 and moves at increments of 1 till it reaches height 9
     */
    private fun crateTopographicMap(
        input: Sequence<String>,
        trailHeads: MutableSet<TrailHead>
    ) = TopographicMap(
        ySize = input.count(),
        xSize = input.first().length
    ) { coordinate ->
        val height = input.elementAt(coordinate.y)[coordinate.x].digitToInt()

        if (height == 0) {
            TrailHead(coordinate = coordinate)
                .also { trailHeads.add(it) }
        } else {
            Hill(coordinate = coordinate, height = height)
        }
    }

    sealed class MapSection {
        abstract val coordinate: Coordinate
        abstract val height: Int

        protected val trailEndHeight = 9

        data class Hill(
            override val coordinate: Coordinate,
            override val height: Int
        ) : MapSection() {
            var isTrailEnd = false

            override fun toString(): String = "$height"
        }

        data class TrailHead(override val coordinate: Coordinate) : MapSection() {
            override val height: Int = 0

            fun findTrailEnds(map: TopographicMap, nextByHeightPerHillMap: MutableMap<Hill, Set<Hill>>): Map<Hill, Int> {
                val trailEnds = findNextByHeight(map = map)
                    .associateWith { 1 }
                    .toMutableMap()

                while (trailEnds.isNotEmpty() && trailEnds.keys.none { it.height == trailEndHeight }) {
                    val currentTrailEnds = trailEnds.toMap()
                    trailEnds.clear()

                    for (hill in currentTrailEnds) {
                        val nextByHeight = nextByHeightPerHillMap.getOrPut(
                            key = hill.key,
                            defaultValue = { hill.key.findNextByHeight(map = map) }
                        )
                        for (next in nextByHeight){
                            val amountVisited = trailEnds[next] ?: 0
                            trailEnds[next] = hill.value + amountVisited
                        }
                    }
                }

                return trailEnds
            }

            override fun toString(): String = "$height"
        }

        fun findNextByHeight(map: TopographicMap): Set<Hill> = map
            .findLinearNeighbours(coordinate = coordinate)
            .values
            .mapNotNull {
                if (it is Hill && it.height - height == 1) {
                    it
                } else {
                    null
                }
            }.toSet()
    }
}
