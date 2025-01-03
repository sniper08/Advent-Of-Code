package solutions._2024

import ANSI_GREEN
import ANSI_RED
import ANSI_RESET
import Coordinate
import LinearDirection
import LinearDirection.*
import day.Day
import solutions._2024.Year2024Day6.GuardMove.*
import solutions._2024.Year2024Day6.LabSection
import solutions._2024.Year2024Day6.LabSection.Empty
import solutions._2024.Year2024Day6.LabSection.Obstruction
import utils.Grid
import utils.GridElement

typealias Lab = Grid<LabSection>

class Year2024Day6 : Day {

    override val year: Int = 2024
    override val day: Int = 6

    /**
     * Find all sections visited by Guard
     */
    override fun part1(input: Sequence<String>): String {
        val guard = Guard()
        val lab = createLab(input = input, guard = guard)

//        lab.print()
//
//        println()

        while (guard.move(lab = lab) == SUCCESSFUL) {
//            println()
//            println("-------Move----------")
//            lab.print()
        }

//        println()
//        println("-------End----------")
//        lab.print()

        return "${lab.flatten().count { it is Empty && it.visitedByGuard().isNotEmpty() }}"
    }

    /**
     * Find number of possible ways to obstruct the way so that Guard ends up stuck in a loop
     */
    override fun part2(input: Sequence<String>): String {
        val guard = Guard()
        val lab = createLab(input = input, guard = guard)
        val coordinatesInRoute = mutableSetOf<Coordinate>(guard.current().coordinate)

        while (guard.move(lab = lab) == SUCCESSFUL) {
            coordinatesInRoute.add(guard.current().coordinate)
        }

        val stuckInLoopCountAfterAddingObstruction = coordinatesInRoute.sumOf { coordinate ->
            lab.reset(guard = guard)
            (lab[coordinate] as Empty).obstructed = true

            var nextMove = guard.moveSearchingForLoop(lab = lab)

            while (nextMove == SUCCESSFUL) {
                nextMove = guard.moveSearchingForLoop(lab = lab)
            }

            if (nextMove == STUCK_IN_LOOP) {
//                println()
//                println("-------$coordinate is obstructed--------")
//                lab.print(guard = guard, guardStartPosition = true)
                1L
            } else {
                0L
            }
        }

        return "$stuckInLoopCountAfterAddingObstruction"
    }

    private fun createLab(input: Sequence<String>, guard: Guard) = Grid<LabSection>(input = input) { coordinate, rawChar ->
        when (rawChar) {
            '.' -> Empty(coordinate = coordinate)
            '#' -> Obstruction(coordinate = coordinate)
            else -> {
                Empty(coordinate = coordinate, guardPresent = true)
                    .also {
                        val linearDirection = LinearDirection.from(value = rawChar)
                        it.visit(linearDirection = linearDirection)
                        guard.start(linearDirection = linearDirection, labSection = it)
                    }
            }
        }
    }

    private fun Lab.reset(guard: Guard) {
        val guardStartCoordinate = guard.startCoordinate()
        val guardStartDirection = guard.startDirection()

        flatten()
            .filterIsInstance<Empty>()
            .forEach { emptySection ->
                emptySection.reset()

                if (emptySection.coordinate == guardStartCoordinate && guardStartDirection != null) {
                    emptySection.visit(linearDirection = guardStartDirection)
                    guard.start(linearDirection = guardStartDirection, labSection = emptySection)
                }
            }
    }

    fun Lab.print(
        guard: Guard,
        guardStartPosition: Boolean = false
    ) {
        print { labSection ->
            val string = labSection.toString()
            val guardString = guard.toString()

            when  {
                labSection is Empty && guardStartPosition && guard.startCoordinate() == labSection.coordinate -> "$ANSI_RED${guard.startDirection()?.toString()}$ANSI_RESET"
                labSection is Empty && !guardStartPosition && labSection.guardPresent -> "$ANSI_RED$guardString$ANSI_RESET"
                labSection is Empty && labSection.obstructed -> "$ANSI_GREEN$string$ANSI_RESET"
                labSection is Empty && labSection.visitedByGuard().isNotEmpty() -> "$ANSI_RED$string$ANSI_RESET"
                labSection is Empty -> string
                else -> string
            }
        }
    }

    data class Visited(val linearDirection: LinearDirection)

    sealed class LabSection : GridElement {

        data class Empty(
            override val coordinate: Coordinate,
            var guardPresent: Boolean = false,
            var obstructed: Boolean = false
        ) : LabSection() {
            private val visitedByGuard = mutableListOf<Visited>()
            fun visitedByGuard(): List<Visited> = visitedByGuard

            fun visit(linearDirection: LinearDirection) {
                visitedByGuard.add(Visited(linearDirection = linearDirection))
                guardPresent = true
            }

            fun reset() {
                guardPresent = false
                obstructed = false
                visitedByGuard.clear()
            }

            override fun toString(): String {
                val containsVerticalDirection = visitedByGuard.any { it.linearDirection == NORTH || it.linearDirection == SOUTH }
                val containsHorizontalDirection = visitedByGuard.any { it.linearDirection == WEST || it.linearDirection == EAST }

                return when {
                    obstructed -> "O"
                    containsVerticalDirection && containsHorizontalDirection -> "+"
                    containsVerticalDirection -> "|"
                    containsHorizontalDirection -> "-"
                    else -> "."
                }
            }
        }

        data class Obstruction(override val coordinate: Coordinate) : LabSection() {
            override fun toString(): String = "#"
        }
    }

    enum class GuardMove { SUCCESSFUL, OUT_OF_GRID, STUCK_IN_LOOP }

    class Guard {
        private var startCoordinate: Coordinate? = null
        private var startLinearDirection: LinearDirection? = null
        private var currentLinearDirection: LinearDirection = NORTH
        private var currentLabSection: Empty = Empty(coordinate = Coordinate(y = -1, x = -1))

        fun start(linearDirection: LinearDirection, labSection: Empty) {
            currentLinearDirection = linearDirection
            currentLabSection = labSection
            if (startCoordinate == null && startLinearDirection == null) {
                startCoordinate = labSection.coordinate
                startLinearDirection = linearDirection
            }
        }

        fun current(): Empty = currentLabSection
        fun startCoordinate() = startCoordinate
        fun startDirection() = startLinearDirection

        fun move(lab: Lab): GuardMove {
            val currentY = currentLabSection.coordinate.y
            val currentX = currentLabSection.coordinate.x

            val possibleNext = when (currentLinearDirection) {
                NORTH -> lab[Coordinate(y = currentY - 1, x = currentX)]
                WEST -> lab[Coordinate(y = currentY, x = currentX - 1)]
                EAST -> lab[Coordinate(y = currentY, x = currentX + 1)]
                SOUTH -> lab[Coordinate(y = currentY + 1, x = currentX)]
            }

            fun rotate90degrees() {
                currentLinearDirection = when (currentLinearDirection) {
                    NORTH -> EAST
                    WEST -> NORTH
                    EAST -> SOUTH
                    SOUTH -> WEST
                }
                currentLabSection.visit(linearDirection = currentLinearDirection)
            }

            return when (possibleNext) {
                is Empty -> {
                    if (possibleNext.obstructed) {
                        rotate90degrees()
                    } else {
                        currentLabSection.guardPresent = false
                        possibleNext.visit(linearDirection = currentLinearDirection)
                        currentLabSection = possibleNext
                    }
                    SUCCESSFUL
                }
                is Obstruction -> {
                    rotate90degrees()
                    SUCCESSFUL
                }
                else -> {
                    currentLabSection.guardPresent = false
                    OUT_OF_GRID
                }
            }
        }

        fun moveSearchingForLoop(lab: Lab): GuardMove {
            val guardMove = move(lab = lab)
            val currentVisitedByGuard = currentLabSection.visitedByGuard()

            return if (guardMove == SUCCESSFUL) {
                if (currentVisitedByGuard.size > 1) {
                    val repeatedDirections = currentVisitedByGuard.groupBy { it.linearDirection }

                    // Check if LabSection was visited more than once from the same direction
                    val successful = repeatedDirections.values.none { it.size > 1 }
                    if (successful) SUCCESSFUL else STUCK_IN_LOOP
                } else {
                    SUCCESSFUL
                }
            } else {
                OUT_OF_GRID
            }
        }

        override fun toString(): String = currentLinearDirection.toString()
    }
}
