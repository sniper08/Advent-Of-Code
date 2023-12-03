package solutions._2021

import Coordinate
import kotlin.math.abs
import kotlin.math.absoluteValue

fun calculateMaximumShootAltitude(input: String) {
    val ranges = input
        .split(" ")
        .map { it.replace(",", "").split("=","..") }

    val rangeX = ranges[2][1].toInt()..ranges[2][2].toInt()
    val rangeY = ranges[3][1].toInt()..ranges[3][2].toInt()

    val minX = calculateMinX(rangeX)
    val maxY = calculateMaxY(rangeY)

    println(rangeX)
    println(rangeY)
    println("($minX,$maxY)")
    println("Max Y height: ${maxY.sum()}")
    val count1Step = (abs(rangeX.last - rangeX.first) + 1) * (abs(rangeY.last - rangeY.first) + 1)

    val perfectSum = getPerfectsumX(rangeX, minX)
    val stepsInYPositive = getStepsInY(maxY, rangeY)
    val stepsInX = getStepsInX(rangeX, minX)

    println("Perfect Sum: $perfectSum")
    println("Steps In Y Positive: ")
    for (step in stepsInYPositive) {
        println(step)
    }
    println("Steps In X: ")
    for (step in stepsInX) {
        println(step)
    }

    val velocities = buildVelocities(stepsInX, stepsInYPositive, perfectSum)
    println("Velocities: ${velocities.size}")
    velocities.groupBy { it.x }.values.forEach { println(it.map { "(${it.x},${it.y})" }) }

    println("Total velocities count: ${count1Step + velocities.size}")
}

fun buildVelocities(
    stepsInX: MutableMap<Int, MutableSet<Int>>,
    stepsInY: MutableMap<Int, MutableSet<Int>>,
    perfectSum: Set<Int>
): Set<Coordinate> {
    val velocities = mutableSetOf<Coordinate>()
    var step = 2

    var stepX = stepsInX[step]
    var stepY = stepsInY[step]

    while (stepX != null || stepY != null) {
        if (stepX != null && stepY != null) {
            for (x in stepX) {
                for (y in stepY) {
                    velocities.add(Coordinate(x, y))
                }
            }
        }
        step++
        stepX = stepsInX[step]
        stepY = stepsInY[step]
    }

    for (x in perfectSum) {
        stepsInY.filter { it.key >= x }
            .flatMap { it.value }
            .forEach { y ->
                velocities.add(Coordinate(x, y))
            }
    }

    return velocities
}

fun getStepsInY(maxY: Int, rangeY: IntRange) : MutableMap<Int, MutableSet<Int>> {
    val map = mutableMapOf<Int, MutableSet<Int>>()
    for (y in maxY downTo rangeY.first) {
        var step = if (y > 0) (y * 2) + 1 else 1
        var currentPosition = if (y > 0) 0 else y

        while (currentPosition >= rangeY.first) {
            if(currentPosition in rangeY) {
                if (map[step] == null) {
                    map[step] = mutableSetOf()
                }
                map[step]?.add(y)
            }
            currentPosition += y - step
            step++
        }
    }

    return map
}

fun getStepsInX(rangeX: IntRange, minX: Int) : MutableMap<Int, MutableSet<Int>> {
    val map = mutableMapOf<Int, MutableSet<Int>>()
    for(x in minX..rangeX.last) {
        var step = 1
        var currentPosition = x

        while (currentPosition <= rangeX.last) {
            if(currentPosition in rangeX) {
                if (map[step] == null) {
                    map[step] = mutableSetOf()
                }
                map[step]?.add(x)
            }
            val nextMove = x - step
            if (nextMove == 0) break
            currentPosition += nextMove
            step++
        }
    }
    return map
}

fun getPerfectsumX(rangeX: IntRange, minX: Int): Set<Int> {
    val perfectSum = mutableSetOf<Int>().apply { add(minX) }
    var x = minX + 1
    var sum = x.sum()
    while (sum in rangeX) {
        perfectSum.add(x)
        sum = (++x).sum()
    }

    return perfectSum
}

fun calculateMinX(rangeX: IntRange): Int {
    var x = 0
    var sum = x.sum()
    while (sum !in rangeX) {
        x++
        sum = x.sum()
    }
    return x
}

fun calculateMaxY(rangeY: IntRange) = rangeY.first.absoluteValue - 1

fun Int.sum() = this * (this + 1) / 2
