package solutions._2023

import Coordinate
import CDirection
import CDirection.*
import java.util.PriorityQueue

typealias City = Array<Array<CityBlock>>

fun City.print() {
    forEach {
        println(it.joinToString("") { it.toString() })
    }
}

data class CityBlock(
    val heatLoss: Int,
    val coordinate: Coordinate
) {
    fun north(city: City) = city.getOrNull(coordinate.y - 1)?.getOrNull(coordinate.x)
    fun south(city: City) = city.getOrNull(coordinate.y + 1)?.getOrNull(coordinate.x)
    fun west(city: City) = city.getOrNull(coordinate.y)?.getOrNull(coordinate.x - 1)
    fun east(city: City) = city.getOrNull(coordinate.y)?.getOrNull(coordinate.x + 1)
}

data class RouteSimple(
    val last: CityBlock,
    val heatLoss: Int,
    var inStraightLineSteps: Int,
    val direction: CDirection
)

fun initialisingControlMap(direction: CDirection, maxSteps: Int) =
    direction to buildMap {
        repeat(maxSteps) { put(it + 1, Int.MAX_VALUE) }
    }.toMutableMap()

fun calculateLeastHeatLoss(input: Sequence<String>) {
    val rows = input.count()
    val cols = input.first().length

    val city = createCity(rows, cols, input)

    val controlArray = createControlArray(rows, cols, 3)
    val pq = createPriorityQueue(RouteSimple(last = city[0][0], heatLoss = 0, inStraightLineSteps = 0, direction = EAST))

    processQueue(pq, controlArray) { next, toCheck ->
        val canMoveStraight = next.inStraightLineSteps < 3

        when (next.direction) {
            NORTH -> {
                next.last.west(city)?.let { toCheck.add(RouteSimple(last = it, heatLoss = next.heatLoss + it.heatLoss, inStraightLineSteps = 1, direction = WEST)) }
                if (canMoveStraight) next.last.north(city)?.let { toCheck.add(RouteSimple(last = it, heatLoss = next.heatLoss + it.heatLoss, inStraightLineSteps = next.inStraightLineSteps + 1, direction = NORTH)) }
                next.last.east(city)?.let { toCheck.add(RouteSimple(last = it, heatLoss = next.heatLoss + it.heatLoss, inStraightLineSteps = 1, direction = EAST)) }
            }
            SOUTH -> {
                next.last.west(city)?.let { toCheck.add(RouteSimple(last = it, heatLoss = next.heatLoss + it.heatLoss, inStraightLineSteps = 1, direction = WEST)) }
                if (canMoveStraight) next.last.south(city)?.let { toCheck.add(RouteSimple(last = it, heatLoss = next.heatLoss + it.heatLoss, inStraightLineSteps = next.inStraightLineSteps + 1, direction = SOUTH)) }
                next.last.east(city)?.let { toCheck.add(RouteSimple(last = it, heatLoss = next.heatLoss + it.heatLoss, inStraightLineSteps = 1, direction = EAST)) }
            }
            WEST -> {
                next.last.north(city)?.let { toCheck.add(RouteSimple(last = it, heatLoss = next.heatLoss + it.heatLoss, inStraightLineSteps = 1, direction = NORTH)) }
                if (canMoveStraight) next.last.west(city)?.let { toCheck.add(RouteSimple(last = it, heatLoss = next.heatLoss + it.heatLoss, inStraightLineSteps = next.inStraightLineSteps + 1, direction = WEST)) }
                next.last.south(city)?.let { toCheck.add(RouteSimple(last = it, heatLoss = next.heatLoss + it.heatLoss, inStraightLineSteps = 1, direction = SOUTH)) }
            }
            EAST -> {
                next.last.north(city)?.let { toCheck.add(RouteSimple(last = it, heatLoss = next.heatLoss + it.heatLoss, inStraightLineSteps = 1, direction = NORTH)) }
                if (canMoveStraight) next.last.east(city)?.let { toCheck.add(RouteSimple(last = it, heatLoss = next.heatLoss + it.heatLoss, inStraightLineSteps = next.inStraightLineSteps + 1, direction = EAST)) }
                next.last.south(city)?.let { toCheck.add(RouteSimple(last = it, heatLoss = next.heatLoss + it.heatLoss, inStraightLineSteps = 1, direction = SOUTH)) }
            }
        }
    }

    controlArray.forEach {
        println(
            it.joinToString(",") { gridControl ->
                val lowest = gridControl.entries.minOf { directionMap -> directionMap.value.values.minOf { heatLoss -> heatLoss } }
                "$lowest"
            }
        )
    }
}

fun calculateLeastHeatLossUltra(input: Sequence<String>) {
    val rows = input.count()
    val cols = input.first().length

    val city = createCity(rows, cols, input)

    val controlArray = createControlArray(rows, cols, 10)
    val pq = createPriorityQueue(RouteSimple(last = city[0][0], heatLoss = 0, inStraightLineSteps = 0, direction = EAST))

    processQueue(pq, controlArray) { next, toCheck ->
        val canMoveStraight = next.inStraightLineSteps < 10
        val canTurn = next.inStraightLineSteps >= 4

        when (next.direction) {
            NORTH -> {
                when {
                    !canMoveStraight -> {
                        next.last.west(city)?.let { toCheck.add(RouteSimple(last = it, heatLoss = next.heatLoss + it.heatLoss, inStraightLineSteps = 1, direction = WEST)) }
                        next.last.east(city)?.let { toCheck.add(RouteSimple(last = it, heatLoss = next.heatLoss + it.heatLoss, inStraightLineSteps = 1, direction = EAST)) }
                    }
                    canTurn -> {
                        next.last.west(city)?.let { toCheck.add(RouteSimple(last = it, heatLoss = next.heatLoss + it.heatLoss, inStraightLineSteps = 1, direction = WEST)) }
                        next.last.north(city)?.let { toCheck.add(RouteSimple(last = it, heatLoss = next.heatLoss + it.heatLoss, inStraightLineSteps = next.inStraightLineSteps + 1, direction = NORTH)) }
                        next.last.east(city)?.let { toCheck.add(RouteSimple(last = it, heatLoss = next.heatLoss + it.heatLoss, inStraightLineSteps = 1, direction = EAST)) }
                    }
                    else -> {
                        next.last.north(city)?.let { toCheck.add(RouteSimple(last = it, heatLoss = next.heatLoss + it.heatLoss, inStraightLineSteps = next.inStraightLineSteps + 1, direction = NORTH)) }
                    }
                }
            }
            SOUTH -> {
                when {
                    !canMoveStraight -> {
                        next.last.west(city)?.let { toCheck.add(RouteSimple(last = it, heatLoss = next.heatLoss + it.heatLoss, inStraightLineSteps = 1, direction = WEST)) }
                        next.last.east(city)?.let { toCheck.add(RouteSimple(last = it, heatLoss = next.heatLoss + it.heatLoss, inStraightLineSteps = 1, direction = EAST)) }
                    }
                    canTurn -> {
                        next.last.west(city)?.let { toCheck.add(RouteSimple(last = it, heatLoss = next.heatLoss + it.heatLoss, inStraightLineSteps = 1, direction = WEST)) }
                        next.last.south(city)?.let { toCheck.add(RouteSimple(last = it, heatLoss = next.heatLoss + it.heatLoss, inStraightLineSteps = next.inStraightLineSteps + 1, direction = SOUTH)) }
                        next.last.east(city)?.let { toCheck.add(RouteSimple(last = it, heatLoss = next.heatLoss + it.heatLoss, inStraightLineSteps = 1, direction = EAST)) }
                    }
                    else -> {
                        next.last.south(city)?.let { toCheck.add(RouteSimple(last = it, heatLoss = next.heatLoss + it.heatLoss, inStraightLineSteps = next.inStraightLineSteps + 1, direction = SOUTH)) }
                    }
                }
            }
            WEST -> {
                when {
                    !canMoveStraight -> {
                        next.last.north(city)?.let { toCheck.add(RouteSimple(last = it, heatLoss = next.heatLoss + it.heatLoss, inStraightLineSteps = 1, direction = NORTH)) }
                        next.last.south(city)?.let { toCheck.add(RouteSimple(last = it, heatLoss = next.heatLoss + it.heatLoss, inStraightLineSteps = 1, direction = SOUTH)) }
                    }
                    canTurn -> {
                        next.last.north(city)?.let { toCheck.add(RouteSimple(last = it, heatLoss = next.heatLoss + it.heatLoss, inStraightLineSteps = 1, direction = NORTH)) }
                        next.last.west(city)?.let { toCheck.add(RouteSimple(last = it, heatLoss = next.heatLoss + it.heatLoss, inStraightLineSteps = next.inStraightLineSteps + 1, direction = WEST)) }
                        next.last.south(city)?.let { toCheck.add(RouteSimple(last = it, heatLoss = next.heatLoss + it.heatLoss, inStraightLineSteps = 1, direction = SOUTH)) }
                    }
                    else -> {
                        next.last.west(city)?.let { toCheck.add(RouteSimple(last = it, heatLoss = next.heatLoss + it.heatLoss, inStraightLineSteps = next.inStraightLineSteps + 1, direction = WEST)) }
                    }
                }
            }
            EAST -> {
                when {
                    !canMoveStraight -> {
                        next.last.north(city)?.let { toCheck.add(RouteSimple(last = it, heatLoss = next.heatLoss + it.heatLoss, inStraightLineSteps = 1, direction = NORTH)) }
                        next.last.south(city)?.let { toCheck.add(RouteSimple(last = it, heatLoss = next.heatLoss + it.heatLoss, inStraightLineSteps = 1, direction = SOUTH)) }
                    }
                    canTurn -> {
                        next.last.north(city)?.let { toCheck.add(RouteSimple(last = it, heatLoss = next.heatLoss + it.heatLoss, inStraightLineSteps = 1, direction = NORTH)) }
                        next.last.east(city)?.let { toCheck.add(RouteSimple(last = it, heatLoss = next.heatLoss + it.heatLoss, inStraightLineSteps = next.inStraightLineSteps + 1, direction = EAST)) }
                        next.last.south(city)?.let { toCheck.add(RouteSimple(last = it, heatLoss = next.heatLoss + it.heatLoss, inStraightLineSteps = 1, direction = SOUTH)) }
                    }
                    else -> {
                        next.last.east(city)?.let { toCheck.add(RouteSimple(last = it, heatLoss = next.heatLoss + it.heatLoss, inStraightLineSteps = next.inStraightLineSteps + 1, direction = EAST)) }
                    }
                }
            }
        }
    }

    controlArray.forEach {
        println(
            it.joinToString(",") { gridControl ->
                val lowest = gridControl.entries.minOf { directionMap ->
                    directionMap.value.entries
                        .filter { stepMap -> stepMap.key >= 4 }
                        .minOf { stepMap -> stepMap.value }
                }
                "$lowest"
            }
        )
    }
}

private fun createControlArray(
    rows: Int,
    cols: Int,
    maxSteps: Int
) = Array(rows) {
    Array(cols) {
        mutableMapOf(
            initialisingControlMap(NORTH, maxSteps),
            initialisingControlMap(SOUTH, maxSteps),
            initialisingControlMap(WEST, maxSteps),
            initialisingControlMap(EAST, maxSteps)
        )
    }
}

private fun createPriorityQueue(first: RouteSimple) = PriorityQueue<RouteSimple> { a, b ->
    when {
        a.heatLoss > b.heatLoss -> 1
        b.heatLoss > a.heatLoss -> -1
        else -> 0
    }
}.apply {
    add(first)
}

private fun createCity(
    rows: Int,
    cols: Int,
    input: Sequence<String>
) = City(rows) { y ->
    Array(cols) { x ->
        CityBlock(
            heatLoss = input.elementAt(y)[x].digitToInt(),
            coordinate = Coordinate(y = y, x = x)
        )
    }
}

private fun processQueue(
    pq: PriorityQueue<RouteSimple>,
    controlArray: Array<Array<MutableMap<CDirection, MutableMap<Int, Int>>>>,
    onNext: (RouteSimple, MutableList<RouteSimple>) -> Unit
) {
    while (pq.isNotEmpty()) {
        val next = pq.poll()
        val toCheck = mutableListOf<RouteSimple>()

        onNext(next, toCheck)

        for (possible in toCheck) {
            val directionControl = controlArray[possible.last.coordinate.y][possible.last.coordinate.x].getValue(possible.direction)
            val control = directionControl.getValue(possible.inStraightLineSteps)

            if (possible.heatLoss < control) {
                directionControl[possible.inStraightLineSteps] = possible.heatLoss

                if (!pq.contains(possible)) {
                    pq.add(possible)
                }
            }
        }
    }
}