package solutions._2015

import solutions._2021.Coordinate

const val NORTH = '^'
const val SOUTH = 'v'
const val EAST = '>'
const val WEST = '<'

fun calculateHousesVisited(instruction: String) {
    val visited = mutableMapOf<Coordinate, Int>().apply { put(Coordinate(0, 0), 1) }

    var x = 0
    var y = 0

    for (direction in instruction) {
        visited.moveToNextHouse(direction, x, y).also {
            x = it.x
            y = it.y
        }
    }

    println("Total visited at least once: ${visited.values.count { it >= 1 }}")
}

fun calculateHousesVisitedWithRoboSanta(instruction: String) {
    val visited = mutableMapOf<Coordinate, Int>().apply { put(Coordinate(0, 0), 1) }

    var santaX = 0
    var santaY = 0
    var roboX = 0
    var roboY = 0
    var santaTurn = true

    for (direction in instruction) {
        visited.moveToNextHouse(
            direction,
            if (santaTurn) santaX else roboX,
            if (santaTurn) santaY else roboY
        ).also {
            if (santaTurn) {
                santaX = it.x
                santaY = it.y
            } else {
                roboX = it.x
                roboY = it.y
            }
        }
        santaTurn = !santaTurn
    }

    println("Total visited at least once: ${visited.values.count { it >= 1 }}")
}

fun MutableMap<Coordinate, Int>.moveToNextHouse(direction: Char, currentX: Int, currentY: Int): Coordinate {
    var x = currentX
    var y = currentY

    when (direction) {
        NORTH -> y--
        SOUTH -> y++
        EAST -> x++
        WEST -> x--
    }

    val nextHouse = Coordinate(x, y)
    val current = getOrDefault(nextHouse, 0)

    put(nextHouse, current + 1)
    return nextHouse
}
