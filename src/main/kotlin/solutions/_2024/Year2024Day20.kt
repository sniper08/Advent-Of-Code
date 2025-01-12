package solutions._2024

import ANSI_BLUE
import ANSI_CYAN
import ANSI_RED
import ANSI_RESET
import Coordinate
import createAnsi
import day.Day
import solutions._2024.Year2024Day20.RaceTrackSection
import solutions._2024.Year2024Day20.RaceTrackSection.TrackSegment
import solutions._2024.Year2024Day20.RaceTrackSection.Wall
import utils.Grid
import utils.GridElement
import kotlin.math.abs

typealias RaceTrack = Grid<RaceTrackSection>

class Year2024Day20 : Day {

    /**
     * Find sum of all cheats that last 2 picoseconds and saved more than 100 picoseconds
     */
    override fun part1(input: Sequence<String>): String {
        val raceTrack = createRaceTrack(input = input)
       // println("Total race cost: ${raceTrack.totalCostNoCheats}")
        val cheatStepsCost = 2L
        val allWalls = raceTrack
            .raceTrack
            .flatten()
            .filterIsInstance<Wall>()
            .filter { it.coordinate.y in 1..<raceTrack.raceTrack.yLastIndex() && it.coordinate.x in 1..<raceTrack.raceTrack.xLastIndex() }

        for (wall in allWalls) {
            val verticalNeighbours = raceTrack.raceTrack.findVerticalNeighbours(coordinate = wall.coordinate)

            if (verticalNeighbours.northNeighbour is TrackSegment && verticalNeighbours.southNeighbour is TrackSegment) {
                val stepsDistance = abs(verticalNeighbours.northNeighbour.cost - verticalNeighbours.southNeighbour.cost)
                wall.verticalCheatSaves = stepsDistance - cheatStepsCost
            }

            val horizontalNeighbours = raceTrack.raceTrack.findHorizontalNeighbours(coordinate = wall.coordinate)

            if (horizontalNeighbours.westNeighbour is TrackSegment && horizontalNeighbours.eastNeighbour is TrackSegment) {
                val stepsDistance = abs(horizontalNeighbours.westNeighbour.cost - horizontalNeighbours.eastNeighbour.cost)
                wall.horizontalCheatSaves = stepsDistance - cheatStepsCost
            }
        }

//        println()
//        raceTrack.raceTrack.print()

        var cheatsThatSaveAtLeast100Picoseconds = 0L
        val cheatingCounts = mutableMapOf<Long, Long>()

        allWalls
            .forEach { wall ->
                if (wall.canCheat()) {
                    if (wall.horizontalCheatSaves > 0) {
                        if (wall.horizontalCheatSaves >= 100) cheatsThatSaveAtLeast100Picoseconds++
                      //  cheatingCounts.merge(wall.horizontalCheatSaves, 1) { oldValue, _ -> oldValue + 1 }
                    }
                    if (wall.verticalCheatSaves > 0) {
                        if (wall.verticalCheatSaves >= 100) cheatsThatSaveAtLeast100Picoseconds++
                       // cheatingCounts.merge(wall.verticalCheatSaves, 1) { oldValue, _ -> oldValue + 1 }
                    }
                }
            }

//        cheatingCounts
//            .toSortedMap()
//            .forEach {
//                println("There are ${it.value} cheats that save ${it.key} picoseconds")
//            }

        return "$cheatsThatSaveAtLeast100Picoseconds"
    }

    /**
     * Find sum of all cheats that last at most 20 picoseconds and saved more than 100 picoseconds
     */
    override fun part2(input: Sequence<String>): String {
        val raceTrack = createRaceTrack(input = input)

        val cheatingCounts = mutableMapOf<Long, Long>()
        var cheatsThatSaveAtLeast100Picoseconds = 0L
        val maxManhattanDistance = 20

        for (cheatInit in raceTrack.raceTrackSegments.dropLast(1)) {
            val possibleCheatExits = raceTrack.raceTrackSegments.toMutableList()
                .apply { remove(cheatInit) }

            for (cheatExit in possibleCheatExits) {
                val manhattanDistance = abs(cheatInit.y - cheatExit.y) + abs(cheatInit.x - cheatExit.x)

                if (manhattanDistance <= maxManhattanDistance) {
                    val normalCost = cheatExit.cost - cheatInit.cost

                    if (manhattanDistance < normalCost) {
                        val saved = normalCost - manhattanDistance
                        if (saved >= 100) cheatsThatSaveAtLeast100Picoseconds++
//                        cheatingCounts.merge(saved, 1) { oldValue, _ -> oldValue + 1 }
                    }
                }
            }
        }

//        cheatingCounts
//            .toSortedMap()
//            .forEach {
//                println("There are ${it.value} cheats that save ${it.key} picoseconds")
//            }

        return "$cheatsThatSaveAtLeast100Picoseconds"
    }

    private fun createRaceTrack(input: Sequence<String>): RaceTrackWrapper {
        var startCoordinate = Coordinate.dummy
        val raceTrackSegments = mutableListOf<TrackSegment>()

        val raceTrack = RaceTrack(input = input) { coordinate, rawChar ->
            when (rawChar) {
                'S' -> {
                    startCoordinate = coordinate
                    TrackSegment(coordinate = coordinate, isStart = true, cost = 0)
                }
                'E' -> TrackSegment(coordinate = coordinate, isEnd = true)
                '#' -> {
                    val isEdge = coordinate.y == 0 || coordinate.x == 0
                            || coordinate.y == input.count() - 1 || coordinate.x == input.first().length - 1
                    Wall(coordinate = coordinate, isEdge = isEdge)
                }
                else -> TrackSegment(coordinate = coordinate)
            }
        }

        //raceTrack.print()

        var nextTrackSegment = raceTrack.getElement(coordinate = startCoordinate) as TrackSegment
        raceTrackSegments.add(nextTrackSegment)

        while (!nextTrackSegment.isEnd) {
            nextTrackSegment = raceTrack.findLinearNeighbours(coordinate = nextTrackSegment.coordinate)
                .values
                .filterNotNull()
                .filterIsInstance<TrackSegment>()
                .first { it.cost == Long.MIN_VALUE }
                .apply {
                    cost = nextTrackSegment.cost + 1
                    raceTrackSegments.add(this)
                }
        }

        return RaceTrackWrapper(
            raceTrack = raceTrack,
            raceTrackSegments = raceTrackSegments,
            end = nextTrackSegment
        )
    }

    private fun RaceTrack.print() {
        print { section ->
            val string = section.toString()

            when {
                section is Wall && section.canCheat() -> "${createAnsi(number = 11)}$string$ANSI_RESET"
                section is Wall && section.isEdge -> "$ANSI_BLUE$string$ANSI_RESET"
                section is Wall -> "$ANSI_CYAN$string$ANSI_RESET"
                section is TrackSegment && section.isStart -> "${createAnsi(number = 47)}$string$ANSI_RESET"
                section is TrackSegment && section.isEnd -> "$ANSI_RED$string$ANSI_RESET"
                else -> string
            }
        }
    }

    data class RaceTrackWrapper(
        val raceTrack: RaceTrack,
        val raceTrackSegments: List<TrackSegment>,
        val end: TrackSegment,
    )

    sealed class RaceTrackSection : GridElement {

        data class Wall(
            override val coordinate: Coordinate,
            var horizontalCheatSaves: Long = 0,
            var verticalCheatSaves: Long = 0,
            var isEdge: Boolean = false
        ) : RaceTrackSection() {
            fun canCheat() = horizontalCheatSaves > 0 || verticalCheatSaves > 0

            override fun toString(): String = if (canCheat()) "1" else "#"
        }

        data class TrackSegment(
            override val coordinate: Coordinate,
            var cost: Long = Long.MIN_VALUE,
            val isStart: Boolean = false,
            val isEnd: Boolean = false,
        ) : RaceTrackSection() {

            override fun toString(): String = when {
                isStart -> "S"
                isEnd -> "E"
                else -> "."
            }
        }
    }
}