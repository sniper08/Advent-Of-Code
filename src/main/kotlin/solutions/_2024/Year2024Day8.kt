package solutions._2024

import ANSI_GREEN
import ANSI_RED
import ANSI_RESET
import Coordinate
import day.Day
import solutions._2024.Year2024Day8.FrequencySection
import solutions._2024.Year2024Day8.FrequencySection.Antenna
import solutions._2024.Year2024Day8.FrequencySection.Empty
import utils.Grid
import utils.GridElement

typealias FrequencyGrid = Grid<FrequencySection>

class Year2024Day8 : Day {

    override val year: Int = 2024
    override val day: Int = 8

    /**
     * Find antinodes for a pair of antennae, antinodes must be at exactly doubled the manhattan distance of the pair
     */
    override fun part1(input: Sequence<String>): String {
        val frequencyGrid = createFrequencyGrid(input)
        val antinodesCount = frequencyGrid.findAntinodes(onlyAtDoubleManhattanDistance = true)

     //   frequencyGrid.print()

        return "$antinodesCount"
    }

    /**
     * Find antinodes for antennae, antinodes must be in line and inside the grid, meaning other antennae are
     * immediately considered antinodes
     */
    override fun part2(input: Sequence<String>): String {
        val frequencyGrid = createFrequencyGrid(input)
        val antinodesCount = frequencyGrid.findAntinodes(onlyAtDoubleManhattanDistance = false)

      //  frequencyGrid.print()

        return "$antinodesCount"
    }

    private fun createFrequencyGrid(input: Sequence<String>) = FrequencyGrid(input = input) { coordinate, rawChar ->
        when (rawChar) {
            '.', '#' -> Empty(coordinate = coordinate)
            else -> Antenna(coordinate = coordinate, symbol = rawChar)
        }
    }

    private fun FrequencyGrid.findAntinodes(onlyAtDoubleManhattanDistance: Boolean): Int {
        val flattened = flatten()

        flattened
            .filterIsInstance<Antenna>()
            .groupBy { it.symbol }
            .values
            .forEach { antennaeGroup ->
                if (antennaeGroup.size > 1) {
                    for (antenna in antennaeGroup) {
                        if (!onlyAtDoubleManhattanDistance) {
                            antenna.isAntinode = true
                        }

                        for (nextAntenna in (antennaeGroup - antenna)) {
                            val yDiff = nextAntenna.coordinate.y - antenna.coordinate.y
                            val xDiff = nextAntenna.coordinate.x - antenna.coordinate.x

                            var currentMultiplier = 2

                            do {
                                val yAntinodeDiff = yDiff * currentMultiplier
                                val xAntinodeDiff = xDiff * currentMultiplier

                                val possibleAntinodeY = antenna.coordinate.y + yAntinodeDiff
                                val possibleAntinodeX = antenna.coordinate.x + xAntinodeDiff

                                val foundInGrid = this[Coordinate(y = possibleAntinodeY, x = possibleAntinodeX)]

                                if (foundInGrid != null) {
                                    foundInGrid.isAntinode = true
                                    currentMultiplier++
                                } else {
                                    break
                                }
                            } while (!onlyAtDoubleManhattanDistance)
                        }
                    }
                }
            }

        return flattened.count { it.isAntinode }
    }

    private fun FrequencyGrid.print() {
        print { section ->
            val string = section.toString()

            when  {
                section.isAntinode -> "$ANSI_GREEN$string$ANSI_RESET"
                section is Antenna -> "$ANSI_RED$string$ANSI_RESET"
                else -> string
            }
        }
    }

    sealed class FrequencySection : GridElement {
        var isAntinode = false

        data class Empty(override val coordinate: Coordinate) : FrequencySection() {
            override fun toString(): String = if (isAntinode) "#" else "."
        }

        data class Antenna(
            override val coordinate: Coordinate,
            val symbol: Char
        ) : FrequencySection() {
            override fun toString(): String = "$symbol"
        }
    }
}
