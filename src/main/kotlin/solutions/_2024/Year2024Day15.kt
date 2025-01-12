package solutions._2024

import ANSI_BLUE
import ANSI_GREEN
import ANSI_RED
import ANSI_RESET
import Coordinate
import LinearDirection
import LinearDirection.*
import day.Day
import doNothing
import parser.inputCleaner
import solutions._2024.Year2024Day15.WarehouseSection
import solutions._2024.Year2024Day15.WarehouseSection.*
import utils.Grid
import utils.GridElement

typealias Warehouse = Grid<WarehouseSection>

class Year2024Day15 : Day {

    override val lineJumpsInput: Int = 2

    /**
     * Find the sum of all boxes GPS after the robot finishes moving
     */
    override fun part1(input: Sequence<String>): String {
        val rawWarehouse = inputCleaner(input = input.first())
        val directions = createDirections(input = input)

        val robot = Robot()
        val warehouse = Warehouse(input = rawWarehouse) { coordinate, rawChar ->
            when (rawChar) {
                '#' -> Wall(coordinate = coordinate)
                'O' -> Box(coordinate = coordinate)
                '@' -> {
                    Empty(coordinate = coordinate)
                        .also {
                            it.containsRobot = true
                            robot.start(warehouseSection = it)
                        }
                }
                else -> Empty(coordinate = coordinate)
            }
        }

//        println("Initial State:")
//        warehouse.print()

        for (direction in directions) {
            robot.move(direction = direction, warehouse = warehouse)

//            println()
//            println("Move $direction:")
//            warehouse.print()
        }

        val sumOfGPSCoordinates = warehouse
            .flatten()
            .filterIsInstance<Box>()
            .sumOf { box ->
                (100L * box.coordinate.y) + box.coordinate.x
            }

        return "$sumOfGPSCoordinates"
    }

    /**
     * Find the sum of all boxes GPS after the robot finishes moving, but the map duplicates each
     * section horizontally before starting
     */
    override fun part2(input: Sequence<String>): String {
        val rawWarehouse = inputCleaner(input = input.first())
        val directions = createDirections(input = input)

        val robot = Robot()
        val warehouse = Warehouse(
            ySize = rawWarehouse.count(),
            xSize = rawWarehouse.first().length * 2
        ) { coordinate ->
            Empty(coordinate = coordinate)
        }


        for ((y, rawLine) in rawWarehouse.withIndex()) {
            var addToFirst = 0
            var addToSecond = 1

            for ((x, rawChar) in rawLine.withIndex()) {
                val firstCoordinate = Coordinate(y = y, x = x + addToFirst)
                val secondCoordinate = Coordinate(y = y, x = x + addToSecond)

                warehouse[firstCoordinate] = when (rawChar) {
                    '#' -> Wall(coordinate = firstCoordinate)
                    'O' -> BoxLeft(coordinate = firstCoordinate)
                    '@' -> Empty(coordinate = firstCoordinate)
                        .also {
                            it.containsRobot = true
                            robot.start(warehouseSection = it)
                        }
                    else -> Empty(coordinate = firstCoordinate)
                }

                warehouse[secondCoordinate] = when (rawChar) {
                    '#' -> Wall(coordinate = secondCoordinate)
                    'O' -> BoxRight(coordinate = secondCoordinate)
                    '@' -> Empty(coordinate = secondCoordinate)
                    else -> Empty(coordinate = secondCoordinate)
                }

                addToFirst++
                addToSecond++
            }
        }

//        println("Initial State:")
//        warehouse.print()

        for (direction in directions) {
            robot.move(direction = direction, warehouse = warehouse)

//            println()
//            println("Move $direction:")
//            warehouse.print()
        }

        val sumOfGPSCoordinates = warehouse
            .flatten()
            .filterIsInstance<BoxLeft>()
            .sumOf { box ->
                (100L * box.coordinate.y) + box.coordinate.x
            }

        return "$sumOfGPSCoordinates"
    }

    private fun createDirections(input: Sequence<String>) = inputCleaner(input = input.last())
        .joinToString("") { it }
        .map { rawChar ->
            LinearDirection.from(value = rawChar)
        }

    private fun Warehouse.print() {
        print { section ->
            val string = section.toString()
            when (section) {
                is Wall -> "$ANSI_BLUE$string$ANSI_RESET"
                is Box, is BoxLeft, is BoxRight -> "$ANSI_GREEN$string$ANSI_RESET"
                is Empty -> if (section.containsRobot) "$ANSI_RED$string$ANSI_RESET" else string
            }
        }
    }

    interface Pushable {
        fun canBePushed(direction: LinearDirection, warehouse: Warehouse): Boolean = true
        fun push(direction: LinearDirection, warehouse: Warehouse): Boolean
    }

    sealed class WarehouseSection : GridElement {

        data class Wall(
            override val coordinate: Coordinate
        ) : WarehouseSection() {
            override fun toString(): String = "#"
        }

        data class Empty(
            override val coordinate: Coordinate
        ) : WarehouseSection() {
            var containsRobot = false

            override fun toString(): String = if (containsRobot) "@" else "."
        }

        data class Box(
            override val coordinate: Coordinate
        ) : WarehouseSection(), Pushable {

            override fun push(
                direction: LinearDirection,
                warehouse: Warehouse
            ): Boolean {
                val neighbour = warehouse.findLinearNeighbour(direction = direction, coordinate = coordinate)
                    ?: return false

                return when (neighbour) {
                    is Empty -> {
                        pushInto(destination = neighbour, warehouse = warehouse)
                        true
                    }
                    is Wall -> false
                    is Box -> {
                        val pushed = neighbour.push(direction = direction, warehouse = warehouse)

                        if (pushed) {
                            pushInto(destination = warehouse[neighbour.coordinate] as Empty, warehouse = warehouse)
                            true
                        } else {
                            false
                        }
                    }
                    else -> false
                }
            }

            private fun pushInto(
                destination: Empty,
                warehouse: Warehouse
            ) {
                // Push Box
                warehouse[destination.coordinate] = Box(coordinate = destination.coordinate)
                // Clear current space
                warehouse[this.coordinate] = Empty(coordinate = this.coordinate)
            }

            override fun toString(): String = "O"
        }

        data class BoxLeft(
            override val coordinate: Coordinate
        ) : WarehouseSection(), Pushable {

            override fun canBePushed(direction: LinearDirection, warehouse: Warehouse): Boolean {
                val extendedBox = ExtendedBox(
                    left = this,
                    right = warehouse.findLinearNeighbour(direction = EAST, coordinate = coordinate) as BoxRight
                )

                return extendedBox.canBePushed(direction = direction, warehouse = warehouse)
            }

            override fun push(
                direction: LinearDirection,
                warehouse: Warehouse
            ): Boolean {
                val extendedBox = ExtendedBox(
                    left = this,
                    right = warehouse.findLinearNeighbour(direction = EAST, coordinate = coordinate) as BoxRight
                )

                return extendedBox.push(direction = direction, warehouse = warehouse)
            }

            override fun toString(): String = "["
        }

        data class BoxRight(
            override val coordinate: Coordinate
        ) : WarehouseSection(), Pushable {

            override fun canBePushed(
                direction: LinearDirection,
                warehouse: Warehouse
            ): Boolean {
                val extendedBox = ExtendedBox(
                    left = warehouse.findLinearNeighbour(direction = WEST, coordinate = coordinate) as BoxLeft,
                    right = this
                )

                return extendedBox.canBePushed(direction = direction, warehouse = warehouse)
            }

            override fun push(
                direction: LinearDirection,
                warehouse: Warehouse
            ): Boolean {
                val extendedBox = ExtendedBox(
                    left = warehouse.findLinearNeighbour(direction = WEST, coordinate = coordinate) as BoxLeft,
                    right = this
                )

                return extendedBox.push(direction = direction, warehouse = warehouse)
            }

            override fun toString(): String = "]"
        }
    }

    data class ExtendedBox(
        val left: BoxLeft,
        val right: BoxRight
    ) : Pushable {

        override fun canBePushed(direction: LinearDirection, warehouse: Warehouse): Boolean {
            return when (direction) {
                NORTH, SOUTH -> {
                    val verticalLeftNeighbour = warehouse.findLinearNeighbour(direction = direction, coordinate = left.coordinate)
                        ?: return false
                    val verticalRightNeighbour = warehouse.findLinearNeighbour(direction = direction, coordinate = right.coordinate)
                        ?: return false

                    when {
                        verticalLeftNeighbour is Empty && verticalRightNeighbour is Empty -> true
                        verticalLeftNeighbour is BoxLeft && verticalRightNeighbour is BoxRight -> {
                            verticalLeftNeighbour.canBePushed(direction = direction, warehouse = warehouse)
                        }
                        verticalLeftNeighbour is BoxRight && verticalRightNeighbour is Empty -> {
                            verticalLeftNeighbour.canBePushed(direction = direction, warehouse = warehouse)
                        }
                        verticalLeftNeighbour is Empty && verticalRightNeighbour is BoxLeft -> {
                            verticalRightNeighbour.canBePushed(direction = direction, warehouse = warehouse)
                        }
                        verticalLeftNeighbour is BoxRight && verticalRightNeighbour is BoxLeft -> {
                            val canLeftBePushed = verticalLeftNeighbour.canBePushed(direction = direction, warehouse = warehouse)
                            val canRightBePushed = verticalRightNeighbour.canBePushed(direction = direction, warehouse = warehouse)

                            canLeftBePushed && canRightBePushed
                        }
                        else -> false
                    }
                }
                WEST -> {
                    val leftNeighbour = warehouse.findLinearNeighbour(direction = direction, coordinate = left.coordinate)
                        ?: return false

                    when (leftNeighbour) {
                        is Empty -> true
                        is BoxRight -> leftNeighbour.canBePushed(direction = direction, warehouse = warehouse)
                        else -> false
                    }
                }
                EAST -> {
                    val rightNeighbour = warehouse.findLinearNeighbour(direction = direction, coordinate = right.coordinate)
                        ?: return false

                    when (rightNeighbour) {
                        is Empty -> true
                        is BoxLeft -> rightNeighbour.canBePushed(direction = direction, warehouse = warehouse)
                        else -> false
                    }
                }
            }
        }

        override fun push(direction: LinearDirection, warehouse: Warehouse): Boolean {
            return when (direction) {
                NORTH, SOUTH -> {
                    val verticalLeftNeighbour = warehouse.findLinearNeighbour(direction = direction, coordinate = left.coordinate)
                        ?: return false
                    val verticalRightNeighbour = warehouse.findLinearNeighbour(direction = direction, coordinate = right.coordinate)
                        ?: return false

                    when {
                        verticalLeftNeighbour is BoxLeft && verticalRightNeighbour is BoxRight -> {
                            verticalLeftNeighbour.push(direction = direction, warehouse = warehouse)
                        }
                        verticalLeftNeighbour is BoxRight && verticalRightNeighbour is Empty -> {
                            verticalLeftNeighbour.push(direction = direction, warehouse = warehouse)
                        }
                        verticalLeftNeighbour is Empty && verticalRightNeighbour is BoxLeft -> {
                            verticalRightNeighbour.push(direction = direction, warehouse = warehouse)
                        }
                        verticalLeftNeighbour is BoxRight && verticalRightNeighbour is BoxLeft -> {
                            verticalLeftNeighbour.push(direction = direction, warehouse = warehouse)
                            verticalRightNeighbour.push(direction = direction, warehouse = warehouse)
                        }
                    }

                    // Push Box
                    warehouse[verticalLeftNeighbour.coordinate] = BoxLeft(coordinate = verticalLeftNeighbour.coordinate)
                    warehouse[verticalRightNeighbour.coordinate] = BoxRight(coordinate = verticalRightNeighbour.coordinate)
                    // Clear current space
                    warehouse[this.left.coordinate] = Empty(coordinate = this.left.coordinate)
                    warehouse[this.right.coordinate] = Empty(coordinate = this.right.coordinate)
                    true
                }
                WEST -> {
                    val destination = warehouse.findLinearNeighbour(
                        direction = direction,
                        coordinate = left.coordinate
                    ) ?: return false

                    (destination as? BoxRight)?.push(direction = direction, warehouse = warehouse)

                    // Push Box
                    warehouse[destination.coordinate] = BoxLeft(coordinate = destination.coordinate)
                    warehouse[left.coordinate] = BoxRight(coordinate = left.coordinate)
                    // Clear Space Left
                    warehouse[right.coordinate] = Empty(coordinate = right.coordinate)
                    true
                }
                EAST -> {
                    val destination = warehouse.findLinearNeighbour(
                        direction = direction,
                        coordinate = right.coordinate
                    ) ?: return false

                    (destination as? BoxLeft)?.push(direction = direction, warehouse = warehouse)

                    // Push Box
                    warehouse[destination.coordinate] = BoxRight(coordinate = destination.coordinate)
                    warehouse[right.coordinate] = BoxLeft(coordinate = right.coordinate)
                    // Clear Space Left
                    warehouse[left.coordinate] = Empty(coordinate = left.coordinate)
                    true
                }
            }
        }
    }

    class Robot {
        private var currentWarehouseSection: Empty = Empty(coordinate = Coordinate(y = -1, x = -1))

        fun start(warehouseSection: Empty) {
            if (currentWarehouseSection.coordinate.y < 0) {
                currentWarehouseSection = warehouseSection
            }
        }

        fun move(
            direction: LinearDirection,
            warehouse: Warehouse
        ) {
            val destination = warehouse.findLinearNeighbour(
                direction = direction,
                coordinate = currentWarehouseSection.coordinate
            )
                ?: return

            when (destination) {
                is Empty -> moveInto(destination = destination)
                is Wall -> doNothing
                is Pushable -> {
                    val canBePushed = destination.canBePushed(direction = direction, warehouse = warehouse)

                    if (canBePushed) {
                        val pushed = destination.push(direction = direction, warehouse = warehouse)

                        if (pushed) {
                            moveInto(destination = warehouse[destination.coordinate] as? Empty)
                        }
                    }
                }
                else -> doNothing
            }
        }

        private fun moveInto(destination: Empty?) {
            destination ?: return

            currentWarehouseSection.containsRobot = false
            currentWarehouseSection = destination
            currentWarehouseSection.containsRobot = true
        }
    }
}
