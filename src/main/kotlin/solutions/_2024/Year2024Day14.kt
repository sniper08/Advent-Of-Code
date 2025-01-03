package solutions._2024

import ANSI_GREEN
import ANSI_RED
import ANSI_RESET
import Coordinate
import day.Day
import utils.Grid
import utils.GridElement

class Year2024Day14 : Day {

    override val year: Int = 2024
    override val day: Int = 14

    /**
     * Find the safety factor by multiplying values per quadrant
     */
    override fun part1(input: Sequence<String>): String {
        val xLimit = 101
        val yLimit = 103
        val ignoreX = xLimit / 2
        val ignoreY = yLimit / 2

        val quadrantAXRange = 0 until ignoreX
        val quadrantAYRange = 0 until ignoreY

        val quadrantBXRange = (ignoreX + 1) until xLimit
        val quadrantBYRange = 0 until ignoreY

        val quadrantCXRange = 0 until ignoreX
        val quadrantCYRange = (ignoreY + 1) until yLimit

        val quadrantDXRange = (ignoreX + 1) until xLimit
        val quadrantDYRange = (ignoreY + 1) until yLimit

        var quadrantACount = 0L
        var quadrantBCount = 0L
        var quadrantCCount = 0L
        var quadrantDCount = 0L

        for (rawRobotInfo in input) {
            val split = rawRobotInfo.split("=", " ", ",")
            var x = split[1].toInt()
            var y = split[2].toInt()
            val velX = split[4].toInt()
            val velY = split[5].toInt()

            repeat(100) {
                val uncheckedNextX = x + velX
                val uncheckedNextY = y + velY

                x = if (uncheckedNextX >= 0) {
                    uncheckedNextX % xLimit
                } else {
                    uncheckedNextX + xLimit
                }
                y = if (uncheckedNextY >= 0) {
                    uncheckedNextY % yLimit
                } else {
                    uncheckedNextY + yLimit
                }
            }

            if (x in quadrantAXRange && y in quadrantAYRange) {
                quadrantACount++
                continue
            }

            if (x in quadrantBXRange && y in quadrantBYRange) {
                quadrantBCount++
                continue
            }

            if (x in quadrantCXRange && y in quadrantCYRange) {
                quadrantCCount++
                continue
            }

            if (x in quadrantDXRange && y in quadrantDYRange) {
                quadrantDCount++
                continue
            }

           // println("In ignore section ($x, $y)")
        }

//        println("Quadrant A count = $quadrantACount")
//        println("Quadrant B count = $quadrantBCount")
//        println("Quadrant D count = $quadrantDCount")
//        println("Quadrant C count = $quadrantCCount")

        val safetyFactor = quadrantACount * quadrantBCount * quadrantCCount * quadrantDCount

        return "$safetyFactor"
    }

    /**
     * Find the xmas tree, apparently safety factor could help write an algorithm
     */
    override fun part2(input: Sequence<String>): String {
        val xLimit = 101
        val yLimit = 103

        val floor = Grid<Tile>(
            ySize = yLimit,
            xSize = xLimit
        ) {
            Tile(robots = mutableListOf())
        }

        for (rawRobotInfo in input) {
            val split = rawRobotInfo.split("=", " ", ",")
            val coordinate = Coordinate(y = split[2].toInt(),x = split[1].toInt())

            floor[coordinate]
                ?.robots
                ?.add(
                    Robot(
                        coordinate = coordinate,
                        velX = split[4].toInt(),
                        velY = split[5].toInt()
                    )
            )
        }

        //floor.print()

        val allTiles = floor.flatten()

        // Time when they start aligning vertically and then do after 101 iterations
        var printStep = 2

        for (step in 1..10000) {
            val robotsToMove = allTiles
                .flatMap { tile ->
                    tile.robots.filter { robot -> robot.step < step }
                }
            allTiles.forEach {
                it.robots.removeIf { robot -> robot.step < step }
            }

            for (robot in robotsToMove) {
                val uncheckedNextX = robot.coordinate.x + robot.velX
                val uncheckedNextY = robot.coordinate.y + robot.velY

                val checkedX = if (uncheckedNextX >= 0) {
                    uncheckedNextX % xLimit
                } else {
                    uncheckedNextX + xLimit
                }
                val checkedY = if (uncheckedNextY >= 0) {
                    uncheckedNextY % yLimit
                } else {
                    uncheckedNextY + yLimit
                }
                val newCoordinate = Coordinate(x = checkedX, y = checkedY)

                floor[newCoordinate]?.robots?.add(
                    Robot(
                        coordinate = newCoordinate,
                        velX = robot.velX,
                        velY = robot.velY
                    )
                )
            }

            if (step == printStep) {
//                println("$ANSI_RED----------------------------------- Step $step ---------------------------------$ANSI_RESET")
//                floor.print()
//                printStep += xLimit
            }
        }

        return ""
    }

    private fun Grid<Tile>.print() {
        print { tile ->
            if (tile.robots.isEmpty()) {
                tile.toString()
            } else {
                "$ANSI_GREEN$tile$ANSI_RESET"
            }
        }
    }

    data class Tile(
        val robots: MutableList<Robot>
    ) : GridElement {

        override val coordinate: Coordinate = Coordinate.dummy
        override fun toString(): String = if (robots.isEmpty()) "." else "${robots.size}"
    }

    data class Robot(
        val coordinate: Coordinate,
        val velX: Int,
        val velY: Int,
        val step: Int = 0
    )
}