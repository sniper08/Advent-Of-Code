package solutions._2015

import Coordinate
import TURN_OFF
import TURN_ON
import day.Day
import solutions._2015.Year2015Day6.LedLightInstruction.*

class Year2015Day6 : Day {

    override val year: Int = 2015
    override val day: Int = 6

    /**
     * Count how many lights are on in the Christmas display (grid)
     */
    override fun part1(input: Sequence<String>): String {
        val grid = Array(1000) { Array(1000) { false } }

        for (rawInstruction in input) {
            val instruction = LedLightInstruction.create(instruction = rawInstruction)

            val yRange = instruction.startCoordinate.y..instruction.endCoordinate.y
            val xRange = instruction.startCoordinate.x..instruction.endCoordinate.x

            grid
                .slice(yRange)
                .forEach { row ->
                    xRange.forEach { positionInRow ->
                        row[positionInRow] = when (instruction) {
                            is Toggle -> !row[positionInRow]
                            is TurnOn -> true
                            is TurnOff -> false
                        }
                    }
            }
        }

        return "${grid.flatten().count { it }}"
    }

    /**
     * Calculate the sum of the intensity of the lights in the Christmas display (grid)
     */
    override fun part2(input: Sequence<String>): String {
        class Led {
            var brightness: Long = 0L

            fun increaseBrightness() {
                brightness++
            }

            fun decreaseBrightness() {
                if (brightness > 0L) {
                    brightness--
                }
            }

            fun doubleIncreaseBrightness() {
                brightness += 2L
            }
        }

        val grid = Array(1000) { Array(1000) { Led() } }

        for (rawInstruction in input) {
            val instruction = LedLightInstruction.create(instruction = rawInstruction)

            val yRange = instruction.startCoordinate.y..instruction.endCoordinate.y
            val xRange = instruction.startCoordinate.x..instruction.endCoordinate.x

            grid
                .slice(yRange)
                .forEach { row ->
                    xRange.forEach { positionInRow ->
                        val led = row[positionInRow]
                        when (instruction) {
                            is Toggle -> led.doubleIncreaseBrightness()
                            is TurnOn -> led.increaseBrightness()
                            is TurnOff -> led.decreaseBrightness()
                        }
                    }
                }
        }

        return "${grid.flatten().sumOf { it.brightness }}"
    }

    private sealed class LedLightInstruction {
        abstract val startCoordinate: Coordinate
        abstract val endCoordinate: Coordinate

        data class Toggle(
            override val startCoordinate: Coordinate,
            override val endCoordinate: Coordinate
        ) : LedLightInstruction()

        data class TurnOn(
            override val startCoordinate: Coordinate,
            override val endCoordinate: Coordinate
        ) : LedLightInstruction()

        data class TurnOff(
            override val startCoordinate: Coordinate,
            override val endCoordinate: Coordinate
        ) : LedLightInstruction()

        companion object {
            fun create(instruction: String): LedLightInstruction {
                val split = instruction.split(" ", ",")
                val endCoordinate = Coordinate(
                    x = split[split.lastIndex - 1].toInt(),
                    y = split.last().toInt()
                )

                return when (split[1]) {
                    TURN_ON -> TurnOn(
                        startCoordinate = Coordinate(
                            x = split[2].toInt(),
                            y = split[3].toInt()
                        ),
                        endCoordinate = endCoordinate
                    )
                    TURN_OFF -> TurnOff(
                        Coordinate(
                            x = split[2].toInt(),
                            y = split[3].toInt()
                        ),
                        endCoordinate = endCoordinate
                    )
                    else -> Toggle(
                        startCoordinate = Coordinate(
                            x = split[1].toInt(),
                            y = split[2].toInt()
                        ),
                        endCoordinate = endCoordinate
                    )
                }
            }
        }
    }
}
