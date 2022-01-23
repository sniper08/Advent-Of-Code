package solutions._2021

typealias SeaCucumberGrid = Array<Array<SeaCucumber>>

const val SOUTH = 'v'
const val EAST = '>'
const val EMPTY = '.'

data class SeaCucumber(val x: Int, val y: Int, var direction: Char) {

    var nextMove: SeaCucumber? = null

    private fun isEmpty() = direction == EMPTY

    fun canMove(gridSeaCucumber: SeaCucumberGrid): Boolean =
        when (direction) {
            EAST -> {
                val nextX = if (x == gridSeaCucumber.first().size - 1) 0 else x + 1
                gridSeaCucumber[y][nextX].let {
                    if (it.isEmpty()) {
                        nextMove = it
                        true
                    } else {
                        false
                    }
                }
            }
            SOUTH -> {
                val nextY = if (y == gridSeaCucumber.size - 1) 0 else y + 1
                gridSeaCucumber[nextY][x].let {
                    if (it.isEmpty()) {
                        nextMove = it
                        true
                    } else {
                        false
                    }
                }
            }
            else -> false
        }

    fun move() {
        nextMove?.direction = direction
        direction = EMPTY
        nextMove = null
    }

    override fun toString() = direction.toString()
}

fun calculateSeaCucumberStop(input: Sequence<String>) {
    val grid = createSeaCucumberGrid(input.toList())

//    println("---- Initial State -----")
//    grid.forEach { println(it.joinToString("") ) }

    val allSeaCucumber = grid.flatten()
    var steps = 1

    var eastMove = allSeaCucumber.filter { it.direction == EAST && it.canMove(grid) }
    var southMove = allSeaCucumber.filter { it.direction == SOUTH && it.canMove(grid) }

    while (eastMove.isNotEmpty() || southMove.isNotEmpty()) {
        if (eastMove.isNotEmpty()) {
            eastMove.forEach { it.move() }
            southMove = allSeaCucumber.filter { it.direction == SOUTH && it.canMove(grid) }
        }

        southMove.forEach { it.move() }

//        println("\n---- Step $steps -----")
//        grid.forEach { println(it.joinToString("") ) }
        eastMove = allSeaCucumber.filter { it.direction == EAST && it.canMove(grid) }
        southMove = allSeaCucumber.filter { it.direction == SOUTH && it.canMove(grid) }
        steps++
    }

    println("---- Total Steps to Stop: $steps ----- ")
}

fun createSeaCucumberGrid(input: List<String>) = SeaCucumberGrid(input.size) { y ->
    Array(input.first().length) { x -> SeaCucumber(x, y, input[y][x]) }
}
