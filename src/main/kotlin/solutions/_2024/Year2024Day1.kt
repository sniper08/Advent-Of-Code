package solutions._2024

import day.Day
import kotlin.math.abs

class Year2024Day1 : Day {

    /**
     * Total Distance between lowest locationIDs
     */
    override fun part1(input: Sequence<String>): String {
        val significantLocations = createSignificantLocations(input = input)
        val sortedLeftSideLocations = significantLocations.leftSideLocations.sortedBy { it.locationID }
        val sortedRightSideLocations = significantLocations.rightSideLocations.sortedBy { it.locationID }

        val totalDistance = sortedLeftSideLocations
            .foldIndexed(initial = 0L) { index, acc, leftSideLocation ->
                val rightSideLocation = sortedRightSideLocations[index]
                acc + abs(leftSideLocation.locationID - rightSideLocation.locationID)
            }

        return "$totalDistance"
    }

    /**
     * Total Similarity Score per locationID
     *
     * Similarity score is found by multiplying left side locationID by how many times it appears on the right side
     */
    override fun part2(input: Sequence<String>): String {
        val significantLocations = createSignificantLocations(input = input)

        val totalSimilarityScore = significantLocations.leftSideLocations
            .fold(0L) { acc, leftSideLocation ->
                val similarityScore = leftSideLocation.locationID * significantLocations.rightSideLocations.count { it == leftSideLocation }
                acc + similarityScore
            }

        return "$totalSimilarityScore"
    }

    private data class Location(val locationID: Long)
    private data class SignificantLocations(
        val leftSideLocations: List<Location>,
        val rightSideLocations: List<Location>
    )

    private fun createSignificantLocations(input: Sequence<String>): SignificantLocations {
        val leftSideLocations = mutableListOf<Location>()
        val rightSideLocations = mutableListOf<Location>()

        input.forEach { rawLocations ->
            val split = rawLocations.split("   ")
            leftSideLocations.add(Location(locationID = split[0].toLong()))
            rightSideLocations.add(Location(locationID = split[1].toLong()))
        }

        return SignificantLocations(
            leftSideLocations = leftSideLocations,
            rightSideLocations = rightSideLocations
        )
    }
}
