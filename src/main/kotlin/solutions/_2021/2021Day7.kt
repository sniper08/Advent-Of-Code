package solutions._2021

import kotlin.math.abs

private fun getCrabInitialPositions(input: String) = input
    .split(',')
    .map { it.toInt() }
    .sorted()

fun calculateCrabSteps(input: String): Int {
    val crabInitialPositions = getCrabInitialPositions(input)
    val positionsCount = crabInitialPositions.groupBy { it }

    var lowestSteps = 0

    for (destination in 0..crabInitialPositions.last()) {
        var currentLowest = 0

        for (position in positionsCount) {
            if (position.key == destination) continue
            val distance = abs(destination - position.key)
            currentLowest += distance * position.value.size
        }

        if (destination == 0) {
            lowestSteps = currentLowest
        }

        if (currentLowest in 1 until lowestSteps) {
            lowestSteps = currentLowest
        }
    }

    return lowestSteps
}

fun calculateCrabStepsCorrected(input: String): Int {
    val crabInitialPositions = getCrabInitialPositions(input)
    val positionsCount = crabInitialPositions.groupBy { it }
    var lowestSteps = 0

    for (destination in 0..crabInitialPositions.last()) {
        var currentLowest = 0

        for (position in positionsCount) {
            if (position.key == destination) continue
            val distance = abs(destination - position.key)
            val distanceCost = (1..distance).reduce(Int::plus)
            currentLowest += distanceCost * position.value.size
        }

        if (destination == 0) {
            lowestSteps = currentLowest
        }

        if (currentLowest in 1 until lowestSteps) {
            lowestSteps = currentLowest
        }
    }

    return lowestSteps
}

private val Int.fuelCost
    get() = (this * (this + 1)) / 2
