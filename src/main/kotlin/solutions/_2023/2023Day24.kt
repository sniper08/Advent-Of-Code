package solutions._2023

import DoubleCoordinate
import io.ksmt.KContext
import io.ksmt.solver.z3.KZ3Solver
import io.ksmt.utils.getValue
import kotlin.time.Duration.Companion.seconds

data class Hailstone(
    val x1: Double,
    val y1: Double,
    val z1: Double,
    val xDiff: Double,
    val yDiff: Double,
    val zDiff: Double,
    val m: Double = yDiff / xDiff,
    val b: Double = y1 - (m * x1),
    val xIncreasing: Boolean = xDiff > 0,
    val yIncreasing: Boolean = yDiff > 0
)

data class HailstoneLong(
    val x: Long,
    val y: Long,
    val z: Long,
    val vx: Long,
    val vy: Long,
    val vz: Long,
)

fun calculateHailstoneIntersections(input: Sequence<String>) {
    val testArea = 200000000000000.0..400000000000000.00
    var intersecting = 0
    val hailstones = input
        .toList()
        .map {
            val split = it.split(", ", " @ ")

            Hailstone(
                x1 = split[0].trim().toDouble(),
                y1 = split[1].trim().toDouble(),
                z1 = split[2].trim().toDouble(),
                xDiff = split[3].trim().toDouble(),
                yDiff = split[4].trim().toDouble(),
                zDiff = split[5].trim().toDouble()
            )
        }

    for ((i, hailstone1) in hailstones.withIndex()) {
        val toEvaluate = hailstones.drop(i + 1)

        for (hailstone2 in toEvaluate) {
            val mutualCoordinate = findMutualCoordinate(hailstone1, hailstone2)

            println()
            println(hailstone1)
            println(hailstone2)
            if (mutualCoordinate != null) {
                val inFutureFor1 = hailstone1.isMutualCoordinateInTheFuture(mutualCoordinate)
                val inFutureFor2 = hailstone2.isMutualCoordinateInTheFuture(mutualCoordinate)
                val inTestArea = mutualCoordinate.isInTestArea(testArea)

                val toCount = inFutureFor1 && inFutureFor2 && inTestArea
                println("$mutualCoordinate -> In Future For 1: $inFutureFor1 --> In Future For 2: $inFutureFor2 --> In Test Area: $inTestArea")
                if (toCount) {
                    intersecting++
                    println("Must be counted")
                }
            } else {
                println(mutualCoordinate)
            }
        }
    }

    println()
    println("Total intersecting: $intersecting")
}

fun findStartingRockPosition(input: Sequence<String>) {
    val hailstones = input
        .toList()
        .map {
            val split = it.split(", ", " @ ")

            HailstoneLong(
                x = split[0].trim().toLong(),
                y = split[1].trim().toLong(),
                z = split[2].trim().toLong(),
                vx = split[3].trim().toLong(),
                vy = split[4].trim().toLong(),
                vz = split[5].trim().toLong()
            )
        }

    val ctx = KContext()

    with(ctx) {
        val x by intSort
        val y by intSort
        val z by intSort
        val vx by intSort
        val vy by intSort
        val vz by intSort
        val t1 by intSort
        val t2 by intSort
        val t3 by intSort

        val ts = listOf(t1, t2, t3)

        KZ3Solver(this).use { solver ->
            hailstones.take(3).forEachIndexed { i, h ->
                solver.assert(x + (vx * ts[i]) eq h.x.expr + (h.vx.expr * ts[i]))
                solver.assert(y + (vy * ts[i]) eq h.y.expr + (h.vy.expr * ts[i]))
                solver.assert(z + (vz * ts[i]) eq h.z.expr + (h.vz.expr * ts[i]))
            }

            val satisfiability = solver.check(timeout = 10.seconds)
            println(satisfiability)

            val model = solver.model()

            val answerX = model.eval(x)
            val answerY = model.eval(y)
            val answerZ = model.eval(z)
            val answerVx = model.eval(vx)
            val answerVy = model.eval(vy)
            val answerVz = model.eval(vz)

            println("$x = $answerX")
            println("$y = $answerY")
            println("$z = $answerZ")
            println("$vx = $answerVx")
            println("$vy = $answerVy")
            println("$vz = $answerVz")

            println("Sum = ${answerX + answerY + answerZ}")
        }
    }
}

fun DoubleCoordinate.isInTestArea(area: ClosedRange<Double>) = x in area && y in area

fun findMutualCoordinate(hailstone1: Hailstone, hailstone2: Hailstone): DoubleCoordinate? {
    return try {
        val mutualX = (hailstone2.b - hailstone1.b) / (hailstone1.m - hailstone2.m)
        val mutualCoordinate = DoubleCoordinate(x = mutualX, y = hailstone1.findYForXIn(mutualX))

        if (mutualCoordinate.x.isInvalid() || mutualCoordinate.y.isInvalid()) {
            null
        } else {
            mutualCoordinate
        }
    } catch (e: Exception) {
        null
    }
}

fun Hailstone.isMutualCoordinateInTheFuture(mutualCoordinate: DoubleCoordinate): Boolean {
    if (xIncreasing) {
        if (mutualCoordinate.x < x1) return false
        return isMutualCoordinateYInTheFuture(mutualCoordinateY = mutualCoordinate.y)
    } else {
        // xDecreasing
        if (mutualCoordinate.x > x1) return false
        return isMutualCoordinateYInTheFuture(mutualCoordinateY = mutualCoordinate.y)
    }
}

fun Hailstone.isMutualCoordinateYInTheFuture(mutualCoordinateY: Double): Boolean {
    if (yIncreasing) {
        if (mutualCoordinateY < y1) return false
        return true
    } else {
        // yDecreasing
        if (mutualCoordinateY > y1) return false
        return true
    }
}

fun Double.isInvalid() = this == Double.NEGATIVE_INFINITY || this == Double.POSITIVE_INFINITY || this.isNaN()
fun Hailstone.findYForXIn(x: Double): Double = (m * x) + b

