package solutions._2024

import ANSI_CYAN
import ANSI_RED
import ANSI_RESET
import Coordinate
import createAnsi
import day.Day
import solutions._2024.Year2024Day18.MemorySpace
import utils.Cost
import utils.Grid
import utils.GridElement
import java.util.*

typealias Computer = Grid<MemorySpace>

class Year2024Day18 : Day {

    override fun part1(input: Sequence<String>): String {
        val amountOfBytes = 1024
        val computer = createComputer()

        input
            .take(amountOfBytes)
            .forEach { rawCoordinate ->
                computer.corruptAt(rawCoordinate)
            }

        return "${computer.findLeastAmountOfSteps(printing = false)}"
    }

    override fun part2(input: Sequence<String>): String {
        var amountOfBytes = 1
        var coordinateFound: String? = null

        while (coordinateFound == null && amountOfBytes < input.count()) {
            val computer = createComputer()
            val lastRawCoordinate = input.elementAt(amountOfBytes - 1)

            input
                .take(amountOfBytes)
                .forEach { rawCoordinate ->
                    computer.corruptAt(rawCoordinate)
                }

            //println()
            //println("($lastRawCoordinate)")
            val leastAmountOfSteps = computer.findLeastAmountOfSteps(printing = false)

            if (leastAmountOfSteps == Long.MAX_VALUE) {
                coordinateFound = input.elementAt(amountOfBytes - 1)
            }
            amountOfBytes++

            if (coordinateFound != null) {
               // computer.print(blockingCoordinate = Coordinate.fromRaw(rawCoordinate = lastRawCoordinate))
            }
        }

        return "$coordinateFound"
    }

    private fun createComputer() = Computer(
        ySize = 71,
        xSize = 71
    ) { coordinate ->
        MemorySpace(coordinate = coordinate)
    }

    private fun Computer.corruptAt(rawCoordinate: String) {
        val split = rawCoordinate.split(",")
        val coordinate = Coordinate(
            x = split.first().toInt(),
            y = split.last().toInt()
        )
        this[coordinate]?.corrupted = true
    }

    private fun Computer.findLeastAmountOfSteps(printing: Boolean): Long {
        val costControl = Grid(ySize = ySize, xSize = xSize) { Cost(cost = Long.MAX_VALUE) }
        costControl.getElement(y = 0, x = 0).cost = 0

        var route = 0
        val allSpaces = flatten()

        val pq = PriorityQueue<MemorySpace> { a, b ->
            val aCost = costControl.getElement(coordinate = a.coordinate).cost
            val bCost = costControl.getElement(coordinate = b.coordinate).cost

            when {
                aCost < bCost -> -1
                aCost > bCost -> 1
                else -> 0
            }
        }.apply {
            add(
                getElement(y = 0, x = 0)
                    .apply {
                        visited = true
                        if (printing) {
                            routes.add(route)
                        }
                    }
            )
        }

        while (pq.isNotEmpty()) {
            val currentMemorySpace = pq.poll()
            val currentCost = costControl.getElement(currentMemorySpace.coordinate).cost
            val currentRoute = currentMemorySpace.routes.lastOrNull() ?: 0

            for (memorySpace in currentMemorySpace.getNextMemorySpaces(computer = this)) {
                val nextCost = currentCost + 1
                if (costControl.getElement(coordinate = memorySpace.coordinate).cost > nextCost) {
                    val newRoute = ++route
                    if (printing) {
                        memorySpace.routes.add(newRoute)
                    }
                    memorySpace.visited = true
                    costControl.getElement(coordinate = memorySpace.coordinate).cost = nextCost

                    if (printing) {
                        allSpaces
                            .filter { it.routes.contains(currentRoute) }
                            .forEach { it.routes.add(newRoute) }
                    }

                    if (memorySpace.coordinate == Coordinate(y = ySize - 1, x = xSize - 1)) {
                        break
                    }

                    pq.add(memorySpace)
                }
            }

            if (printing) {
                allSpaces.forEach { it.routes.remove(currentRoute) }
            }
        }

        val leastAmountOfSteps = costControl.getElement(coordinate = Coordinate(y = ySize - 1, x = xSize - 1)).cost

        if (leastAmountOfSteps < Long.MAX_VALUE && printing) {
            val correctRoute = getElement(coordinate = Coordinate(y = ySize - 1, x = xSize - 1)).routes.first()

            allSpaces
                .filter { it.routes.contains(correctRoute) }
                .forEach { it.inPath = true }

            print()
        }

        return leastAmountOfSteps
    }

    private fun Computer.print(blockingCoordinate: Coordinate? = null) {
        print { memorySpace ->
            val string = memorySpace.toString()
            when {
                memorySpace.coordinate == blockingCoordinate -> "$ANSI_CYAN$string$ANSI_RESET"
                memorySpace.inPath -> createAnsi(11) + "0" + ANSI_RESET
                memorySpace.corrupted -> "$ANSI_RED$string$ANSI_RESET"
                else -> string
            }
        }
    }

    data class MemorySpace(
        override val coordinate: Coordinate,
        var corrupted: Boolean = false,
        var visited: Boolean = false,
        var inPath: Boolean = false
    ) : GridElement {
        val routes = mutableSetOf<Int>()

        fun getNextMemorySpaces(computer: Computer): Set<MemorySpace> =
            computer.findLinearNeighbours(coordinate = coordinate)
                .values
                .filterNotNull()
                .filter { !it.visited && !it.corrupted }
                .toSet()

        override fun toString(): String = if (corrupted) "#" else "."
    }
}