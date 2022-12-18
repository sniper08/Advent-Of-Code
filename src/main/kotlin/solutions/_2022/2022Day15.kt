package solutions._2022

import solutions._2021.Coordinate
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

data class Pairing(
    val sensor: Coordinate,
    val beacon: Coordinate,
) {
    val manhattanDistance = abs(sensor.x - beacon.x) + abs(sensor.y - beacon.y)

    val minX = sensor.x - manhattanDistance
    val maxX = sensor.x + manhattanDistance
    val minY = sensor.y - manhattanDistance
    val maxY = sensor.y + manhattanDistance

    fun findRangeInY(requestedY: Int, gridMinX: Int? = null, gridMaxX: Int? = null): IntRange? =
        if (requestedY in minY .. maxY) {
            var rangeMinX = gridMinX?.let { max(it, minX) } ?: minX
            var rangeMaxX = gridMaxX?.let { min(it, maxX) } ?: maxX

            if (requestedY != sensor.y) {
                val maxManhattanInRequestedY = manhattanDistance - abs(sensor.y - requestedY)

                rangeMinX = gridMinX?.let { max(it, sensor.x - maxManhattanInRequestedY) } ?: (sensor.x - maxManhattanInRequestedY)
                rangeMaxX = gridMaxX?.let { min(it, sensor.x + maxManhattanInRequestedY) } ?: (sensor.x + maxManhattanInRequestedY)
            }

            rangeMinX..rangeMaxX
        } else {
            null
        }
}

fun findNotBeaconHoldersInY(input: Sequence<String>) {
    val Y = 10
    val beaconsInY = mutableSetOf<Coordinate>()
    val sensorsInY = mutableSetOf<Coordinate>()

    val rangesAllSensors = input
        .getPairings()
        .mapNotNull {
            if (it.beacon.y == Y) beaconsInY.add(it.beacon)
            if (it.sensor.y == Y) sensorsInY.add(it.sensor)

            it.findRangeInY(requestedY = Y)
        }.sortedBy { it.first }

    val rangesSanitized = rangesAllSensors.sanitize()

    var reduce = 0

    for (coordinate in (beaconsInY + sensorsInY)) {
        for (range in rangesSanitized) {
            if (coordinate.x in range) reduce++; break
        }
    }

    rangesSanitized.forEach { println(it.toString()) }
    println("Total in Y $Y = ${rangesSanitized.sumOf { it.count() } - reduce}")
}

fun findDistressSignalTuningFrequency(input: Sequence<String>) {
    val max = 4000000
    val pairings = input.getPairings()

    var unmappedBeaconY = 0
    var unmappedBeaconX = 0

    for (y in 0..max) {
        val rangesSanitized = pairings
            .mapNotNull { it.findRangeInY(requestedY = y, gridMinX = 0, gridMaxX = max) }
            .sortedBy { it.first }
            .sanitize()

        println("Y: $y ---- $rangesSanitized")

        if (rangesSanitized.size == 2) {
            unmappedBeaconY = y
            unmappedBeaconX = (rangesSanitized.first().last..rangesSanitized.last().first).elementAt(1)
            break
        }
    }

    println("X is $unmappedBeaconX")
    println("Y is $unmappedBeaconY")
    println("Tuning frequency is = ${(4000000L * unmappedBeaconX.toLong()) + unmappedBeaconY.toLong()}")
}

fun Sequence<String>.getPairings() = map {
    val raw = it.split(" ", "=", ": ", ", ")

    Pairing(
        sensor = Coordinate(x = raw[3].toInt(), y = raw[5].toInt()),
        beacon = Coordinate(x = raw[11].toInt(), y = raw.last().toInt())
    )
}

fun Sequence<IntRange>.sanitize(): Set<IntRange> {
    val rangesSanitized = mutableSetOf(first())

    drop(1).forEach { range ->
        val lastRange = rangesSanitized.last()

        when {
            (range.first <= lastRange.last || range.first - lastRange.last == 1) && range.last >= lastRange.last -> {
                rangesSanitized.remove(lastRange)
                rangesSanitized.add(lastRange.first..range.last)
            }
            range.first > lastRange.last -> rangesSanitized.add(range)
        }
    }

    return rangesSanitized
}
