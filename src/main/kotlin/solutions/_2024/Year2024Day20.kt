package solutions._2024

import Coordinate
import day.Day
import day.NOT_IMPLEMENTED_YET
import solutions._2024.Year2024Day20.RaceTrackSection
import solutions._2024.Year2024Day20.RaceTrackSection.TrackSegment
import solutions._2024.Year2024Day20.RaceTrackSection.Wall
import utils.Grid
import utils.GridElement

typealias RaceTrack = Grid<RaceTrackSection>

class Year2024Day20 : Day {

    override val year: Int = 2024
    override val day: Int = 20

    override fun part1(input: Sequence<String>): String {
        val raceTrack = RaceTrack(input = input) { coordinate, rawChar ->
            when (rawChar) {
                'S' -> TrackSegment(coordinate = coordinate, isStart = true)
                'E' -> TrackSegment(coordinate = coordinate, isEnd = true)
                '#' -> Wall(coordinate = coordinate)
                else -> TrackSegment(coordinate = coordinate)
            }
        }

        return ""
    }

    override fun part2(input: Sequence<String>): String {
        return NOT_IMPLEMENTED_YET
    }

    sealed class RaceTrackSection : GridElement {

        data class Wall(override val coordinate: Coordinate) : RaceTrackSection() {
            override fun toString(): String = "#"
        }

        data class TrackSegment(
            override val coordinate: Coordinate,
            private val isStart: Boolean = false,
            private val isEnd: Boolean = false
        ) : RaceTrackSection() {

            override fun toString(): String = when {
                isStart -> "S"
                isEnd -> "E"
                else -> "."
            }
        }
    }
}