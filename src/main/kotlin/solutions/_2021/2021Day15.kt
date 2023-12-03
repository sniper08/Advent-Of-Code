package solutions._2021

import Coordinate
import java.util.*

typealias ChitonGrid = Array<Array<Chiton>>

class Chiton(
    val y: Int,
    val x: Int,
    val risk: Long
) {
    val possibleCoordinates = setOf(
        Coordinate(y = y, x = x - 1),
        Coordinate(y = y + 1, x = x),
        Coordinate(y = y, x = x + 1),
        Coordinate(y = y - 1, x = x)
    )

    fun getCross(chitonGrid: ChitonGrid) : List<Chiton> {
        val tempList = mutableListOf<Chiton>()

        for (coordinate in possibleCoordinates) {
            try {
                tempList.add(chitonGrid[coordinate.y][coordinate.x])
            } catch (e: Exception) {
                // Do nothing
            }
        }

        return tempList.sortedBy { it.risk }
    }
}

fun calculateSmallChitonGrid(input: Sequence<String>) {
    val chitonList = input.toList()
    val row = chitonList.size
    val col = chitonList.first().length
    val chitonGrid = ChitonGrid(row) { y ->
        Array(col) { x ->
            Chiton(y = y, x = x, risk = chitonList[y][x].digitToInt().toLong())
        }
    }

    println(calculateShortestRoute(chitonGrid, row, col))
}

fun calculateLargeChitonGrid(input: Sequence<String>) {
    val chitonList = input.toList()
    val row = chitonList.size
    val col = chitonList.first().length
    val chitonGrid = ChitonGrid(row * 5) { y ->
        val addY = y / row
        Array(col * 5) { x ->
            val addX = x / row
            var risk = chitonList[y - (row * addY)][x - (col * addX)].digitToInt().toLong() + addX.toLong() + addY.toLong()

            if (risk > 9) {
                risk -= 9L
            }

            Chiton(y = y, x = x, risk = risk)
        }
    }

    println(calculateShortestRoute(chitonGrid, row * 5, col * 5))
}

fun calculateShortestRoute(chitonGrid: ChitonGrid, row: Int, col: Int): Long {
    // Initialized distance array by INT_MAX
    val distanceArray = Array(row) { Array(col) { Long.MAX_VALUE } }

    // Initialized source distance as initial grid position
    distanceArray[0][0] = chitonGrid[0][0].risk
    val pq = PriorityQueue<Chiton>(row * col) { a, b ->
        when {
            (a?.risk ?: 0L) < (b?.risk ?: 0L) -> -1
            (a?.risk ?: 0L) > (b?.risk ?: 0L) -> 1
            else -> 0
        }
    }

    // Insert source chiton in priority queue
    pq.add(chitonGrid[0][0])

    while (pq.isNotEmpty()) {
        val currentChiton = pq.poll()

        for (chiton in currentChiton.getCross(chitonGrid)) {
            val rows = chiton.y
            val cols = chiton.x
            val updated = distanceArray[currentChiton.y][currentChiton.x] + chitonGrid[rows][cols].risk
            if (distanceArray[rows][cols] > updated) {

                // If cell is already been reached once, remove from priority queue
                if (distanceArray[rows][cols] != Long.MAX_VALUE) {
                    pq.remove(chiton)
                }

                // Insert cell with updated distance
                distanceArray[rows][cols] = updated
                pq.add(Chiton(chiton.y, chiton.x, risk = updated))
            }
        }
    }

    return distanceArray[row - 1][col -1] - distanceArray[0][0]
}
