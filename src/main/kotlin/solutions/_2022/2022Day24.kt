package solutions._2022

import Coordinate
import solutions._2022.BlizzardDirection.*
import solutions._2022.ValleyTile.*
import java.util.PriorityQueue

typealias Valley = List<List<ValleyTile>>

enum class BlizzardDirection(val char: Char) {
    R('>'), D('v'), L('<'), U('^')
}

data class Blizzard(val direction: BlizzardDirection, var moved: Boolean = false)

sealed class ValleyTile {
    object Wall : ValleyTile() { override fun toString() = "#" }
    object Start : ValleyTile() { override fun toString() = "."  }
    object End : ValleyTile() { override fun toString() = "." }
    data class Inner(val blizzards: MutableList<Blizzard> = mutableListOf(), val y: Int, val x: Int) : ValleyTile() {
        override fun toString() =
            when {
                blizzards.isEmpty() -> "."
                blizzards.size == 1 -> blizzards.first().direction.char.toString()
                else -> "${blizzards.size}"
            }

        fun clone() = Inner(blizzards = blizzards.toMutableList(), y = y, x = x)
    }
}

data class ValleyMove(val coordinate: Coordinate, val valleyIndex:Int = 0, val minutes: Int = 0) {

    fun findNextMoves(valleys: Set<Valley>): List<ValleyMove> {
        val nextValleyIndex = (valleyIndex + 1) % valleys.size
        val nextValley = valleys.elementAt(nextValleyIndex)
        val nextMinutes = minutes + 1

        return listOf(
            coordinate,
            Coordinate(y = coordinate.y - 1, x = coordinate.x),
            Coordinate(y = coordinate.y, x = coordinate.x - 1),
            Coordinate(y = coordinate.y, x = coordinate.x + 1),
            Coordinate(y = coordinate.y + 1, x = coordinate.x)
        ).mapNotNull {
            val next = nextValley.getOrNull(it.y)?.getOrNull(it.x)

            if (next != null) {
                val addNext = when (next) {
                    Start, End -> true
                    is Inner -> next.blizzards.isEmpty()
                    else -> false
                }

                if (addNext) ValleyMove(coordinate = it, valleyIndex = nextValleyIndex, minutes = nextMinutes) else null
            } else {
                null
            }
        }
    }
}

fun findShortestToCrossValley(input: Sequence<String>) {
    val height = input.count()
    val valleys = input.createValleys(height)

    val endCoordinate = Coordinate(y = height - 1, x = valleys.first().first().lastIndex - 1)
    val start = ValleyMove(Coordinate(y = 0, x = 1), valleyIndex = 0, minutes = 0)

    val shortest = findShortestTimeThroughValley(start, endCoordinate, valleys)
    println(shortest)
}

fun findShortestToCrossValleyPickingUpSnack(input: Sequence<String>) {
    val height = input.count()
    val valleys = input.createValleys(height)

    val endCoordinate = Coordinate(y = height - 1, x = valleys.first().first().lastIndex - 1)
    val start = ValleyMove(Coordinate(y = 0, x = 1), valleyIndex = 0, minutes = 0)

    val shortestA = findShortestTimeThroughValley(start, endCoordinate, valleys)
    println(shortestA)
    val shortestB = findShortestTimeThroughValley(shortestA, start.coordinate, valleys)
    println(shortestB)
    val shortestC = findShortestTimeThroughValley(shortestB, endCoordinate, valleys)
    println(shortestC)
}

private fun Sequence<String>.createValleys(height: Int): Set<Valley> {
    val valley = List(height) { y ->
        List(first().length) { x ->
            when (val found = elementAt(y)[x]) {
                '#' -> Wall
                '.' -> {
                    when (y) {
                        0 -> Start
                        height - 1 -> End
                        else -> Inner(y = y, x = x)
                    }
                }
                else -> Inner(
                    blizzards = mutableListOf(Blizzard(values().first { it.char == found })),
                    y = y,
                    x = x
                )
            }
        }
    }

    val valleys = mutableSetOf(valley)
    var couldAdd = true

    while (couldAdd) {
        val nextValley = valleys.last().new()
        nextValley.moveBlizzards()
        couldAdd = valleys.add(nextValley)
    }

    return valleys
}

private fun findShortestTimeThroughValley(start: ValleyMove, endCoordinate: Coordinate, valleys: Set<Valley>): ValleyMove {
    val counterGrid = Array(valleys.first().size) { Array(valleys.first().first().size) { mutableListOf<ValleyMove>() } }
    counterGrid[start.coordinate.y][start.coordinate.x].add(start)

    val pq = PriorityQueue<ValleyMove> { a, b ->
        when {
            a.minutes < b.minutes -> -1
            b.minutes > a.minutes -> 1
            else -> 0
        }
    }

    pq.add(counterGrid[start.coordinate.y][start.coordinate.x].first())

    while (pq.isNotEmpty()) {
        val current = pq.poll()

        for (nextMove in current.findNextMoves(valleys)) {
            val foundInNext = counterGrid[nextMove.coordinate.y][nextMove.coordinate.x]

            if (nextMove.coordinate == endCoordinate) {
                if (foundInNext.isNotEmpty()) {
                    if (foundInNext.first().minutes > nextMove.minutes) {
                        foundInNext.clear()
                        foundInNext.add(nextMove)
                    }
                } else {
                    foundInNext.add(nextMove)
                }
            } else {
                val previousMove = foundInNext.firstOrNull { it.valleyIndex == nextMove.valleyIndex }
                val endMinutes = counterGrid[endCoordinate.y][endCoordinate.x].firstOrNull()?.minutes ?: Int.MAX_VALUE

                if (previousMove != null) {
                    if (previousMove.minutes > nextMove.minutes) {
                        foundInNext.remove(previousMove)
                        foundInNext.add(nextMove)

                        if (endMinutes > nextMove.minutes) {
                            pq.add(nextMove)
                        }
                    }
                } else {
                    foundInNext.add(nextMove)

                    if (endMinutes > nextMove.minutes) {
                        pq.add(nextMove)
                    }
                }
            }
        }
    }

    return counterGrid[endCoordinate.y][endCoordinate.x].first()
}

fun Valley.moveBlizzards() {
    val allInner = flatten().filterIsInstance<Inner>()

    allInner.forEach { inner ->
        if (inner.blizzards.isNotEmpty()) {
            val blizzardsToMove = inner.blizzards.filter { !it.moved }

            for (blizzard in blizzardsToMove ) {
                var nextX = inner.x
                var nextY = inner.y

                when (blizzard.direction) {
                    R -> {
                        nextX++
                        if (nextX == first().lastIndex) nextX = 1
                    }
                    D -> {
                        nextY++
                        if (nextY == lastIndex) nextY = 1
                    }
                    L -> {
                        nextX--
                        if (nextX == 0) nextX = first().lastIndex - 1
                    }
                    U -> {
                        nextY--
                        if (nextY == 0) nextY = lastIndex - 1
                    }
                }

                blizzard.moved = true
                (get(nextY)[nextX] as Inner).blizzards.add(blizzard)
                inner.blizzards.remove(blizzard)
            }
        }
    }

    allInner.forEach {
        it.blizzards.forEach { blizzard -> blizzard.moved = false }
    }
}

fun Valley.new() = List(size) { y ->
    get(y).map { if (it is Inner) it.clone() else it }
}

