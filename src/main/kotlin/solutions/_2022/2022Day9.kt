package solutions._2022

import solutions._2022.MoveDirection.*

typealias PlanckGrid = Array<Array<Planck>>
enum class MoveDirection { R, L, U, D }
data class Planck(var visited: Boolean = false)
data class Knot(var y: Int, var x: Int)

class Rope(size: Int, middle: Int) {
    val knots = List(size) { Knot(y = middle, x = middle) }
}

data class RopeMove(val direction: String, val value: Int)
fun findPositionsVisitedByTailBridge(input: Sequence<String>) {
    val moves = input.map {
        it.split(" ").let { move ->
            RopeMove(direction = move[0], value = move[1].toInt())
        }
    }
    val sideLength = (moves.maxOf { it.value } * 21)
    val middle = sideLength / 2

    val grid = PlanckGrid(sideLength) { Array(sideLength) { Planck() } }
    val rope = Rope(size = 10, middle)
    grid[middle][middle].visited = true

    //grid.print(rope)

    moves.forEach { move ->
        //println("== ${move.direction} ${move.value} ==")
        for (i in 1..move.value) {
            rope.move(move.direction, grid)
        }
        //grid.print(rope)
    }
    //grid.printVisitedByTail()

    println("Visited by tail: ${grid.flatten().count { it.visited }}")
}

fun Rope.move(moveDirection: String, grid: PlanckGrid) {
    val head = knots.first()
    when (moveDirection) {
        L.name -> head.x--
        R.name -> head.x++
        U.name -> head.y--
        D.name -> head.y++
    }

    for (i in 1..knots.lastIndex) {
        val previous = if (i == 1) head else knots[i - 1]
        val current = knots[i]

        val isPreviousAdjacent = previous.x in (current.x - 1)..(current.x + 1)
                && previous.y in (current.y - 1)..(current.y + 1)

        if (!isPreviousAdjacent) {
            current.updateX(previous.x)
            when {
                previous.y < current.y -> current.y--
                previous.y > current.y -> current.y++
            }
        }
    }
    val tail = knots.last()
    grid[tail.y][tail.x].visited = true
}

fun Knot.updateX(headX: Int) {
    when {
        headX < x -> x--
        headX > x -> x++
    }
}

fun PlanckGrid.print(rope: Rope) {
    indices.forEach { y ->
        get(y).indices.forEach { x ->
            val knotIndex = rope.knots.indexOfFirst { it.y == y && it.x == x }
            print(if (knotIndex > -1) "$knotIndex" else ".")
        }
        println()
    }
    println()
}

fun PlanckGrid.printVisitedByTail() {
    forEach {
        println(it.joinToString("") { planck -> if (planck.visited) "#" else "." })
    }
}