package solutions._2022

import parser.inputCleaner
import solutions._2022.Facing.*

enum class Facing { R, D, L, U  }

data class ForceFieldPassword(var y: Int, var x: Int, var facing: Facing) {

    fun move(amountToMove: Int, map: List<String>) {
        for (i in 1..amountToMove) {
            when (facing) {
                R, L -> if (!updateX(map)) break
                D, U -> if (!updateY(map)) break
            }
        }
    }

    private fun updateX(map: List<String>): Boolean {
        var nextX = x + (if (facing == R) 1 else - 1)
        val next = map[y].getOrNull(nextX)

        if (next != null && next != ' ') {
            if (next.isEmpty()) x = nextX else return false
        } else {
            nextX = map[y].let { row -> if (facing == R) row.indexOfFirst { it.isValid() } else row.indexOfLast { it.isValid() } }
            if (map[y][nextX].isEmpty()) x = nextX else return false
        }
        return true
    }

    private fun updateY(map: List<String>): Boolean {
        var nextY = y + (if (facing == D) 1 else - 1)
        val next = map.getOrNull(nextY)?.getOrNull(x)

        if (next != null && next != ' ') {
            if (next.isEmpty()) y = nextY else return false
        } else {
            nextY = if (facing == D) map.indexOfFirst { it.getOrNull(x).isValid() } else map.indexOfLast { it.getOrNull(x).isValid() }
            if (map[nextY][x].isEmpty()) y = nextY else return false
        }
        return true
    }

    fun moveInCube(amountToMove: Int, map: List<String>) {
        for (i in 1..amountToMove) {
            when {
                x in 50..99 && y in 0..49 -> {
                    // 1
                    when (facing) {
                        R, D -> if (!niceMove(map)) break
                        L -> if (!uglyMove(map, limit = 50, sanitizeX = 0, sanitizeY = 149 - y, newFacing = R)) break
                        U -> if (!uglyMove(map, limit = 0, sanitizeX = 0, sanitizeY = x + 100, newFacing = R)) break
                    }
                }
                x in 100..149 && y in 0..49 -> {
                    // 2
                    when (facing) {
                        R -> if (!uglyMove(map, limit = 149, sanitizeX = 99, sanitizeY = 149 - y, newFacing = L)) break
                        D -> if (!uglyMove(map, limit = 49, sanitizeX = 99, sanitizeY = x - 50, newFacing = L)) break
                        L -> if (!niceMove(map)) break
                        U -> if (!uglyMove(map, limit = 0, sanitizeX = x - 100, sanitizeY = 199)) break
                    }
                }
                x in 50..99 && y in 50..99 -> {
                    // 3
                    when (facing) {
                        R -> if (!uglyMove(map, limit = 99, sanitizeX = y + 50, sanitizeY = 49, newFacing = U)) break
                        D, U -> if (!niceMove(map)) break
                        L -> if (!uglyMove(map, limit = 50, sanitizeX = y - 50, sanitizeY = 100, newFacing = D)) break
                    }
                }
                x in 0..49 && y in 100..149 -> {
                    // 4
                    when (facing) {
                        R, D -> if (!niceMove(map)) break
                        L -> if (!uglyMove(map, limit = 0, sanitizeX = 50, sanitizeY = 149 - y, newFacing = R)) break
                        U -> if (!uglyMove(map, limit = 100, sanitizeX = 50, sanitizeY = x + 50, newFacing = R)) break
                    }
                }
                x in 50..99 && y in 100..149 -> {
                    // 5
                    when (facing) {
                        R -> if (!uglyMove(map, limit = 99, sanitizeX = 149, sanitizeY = 149 - y, newFacing = L)) break
                        D -> if (!uglyMove(map, limit = 149, sanitizeX = 49, sanitizeY = x + 100, newFacing = L)) break
                        L, U -> if (!niceMove(map)) break
                    }
                }
                x in 0..49 && y in 150..199 -> {
                    // 6
                    when (facing) {
                        R -> if (!uglyMove(map, limit = 49, sanitizeX = y - 100, sanitizeY = 149, newFacing = U)) break
                        D -> if (!uglyMove(map, limit = 199, sanitizeX = x + 100, sanitizeY = 0)) break
                        L -> if (!uglyMove(map, limit = 0, sanitizeX = y - 100, sanitizeY = 0, newFacing = D)) break
                        U -> if (!niceMove(map)) break
                    }
                }
            }
        }
    }

    private fun niceMove(map: List<String>): Boolean {
        var nextX = x
        var nextY = y

        when (facing) {
            R -> nextX = x + 1
            D -> nextY = y + 1
            L -> nextX = x - 1
            U -> nextY = y - 1
        }

        return if (map[nextY][nextX].isEmpty()) {
            x = nextX ; y = nextY
            true
        } else {
            false
        }
    }

    private fun uglyMove(map: List<String>, limit: Int, sanitizeX: Int, sanitizeY: Int, newFacing: Facing = facing): Boolean {
        var nextX = x
        var nextY = y

        when (facing) {
            R -> nextX = x + 1 ; D -> nextY = y + 1 ; L -> nextX = x - 1 ; U -> nextY = y - 1
        }

        val withinLimit = when (facing) {
            R -> nextX <= limit ; D -> nextY <= limit ; L -> nextX >= limit ; U -> nextY >= limit
        }

        return if (withinLimit) {
            if (map[nextY][nextX].isEmpty()) {
                x = nextX ; y = nextY ; true
            } else {
                false
            }
        } else {
            nextX = sanitizeX
            nextY = sanitizeY

            if (map[nextY][nextX].isEmpty()) {
                facing = newFacing ; x = nextX ; y = nextY ; true
            } else {
                false
            }
        }
    }

    fun calculate() = (1000 * (y + 1)) + (4 * (x + 1)) + facing.ordinal
}

fun findForceFieldPassword(input: Sequence<String>, inCube: Boolean = false) {
    val map = inputCleaner(input.first()).toList()

    val password = ForceFieldPassword(y = 0, x = map.first().indexOfFirst { it == '.' }, facing = R)

    var instructionsLeft = input.last().substring(0)
    var indexOfNextFacingChange = instructionsLeft.indexOfFirst { it == 'R' || it == 'L'}

    while (indexOfNextFacingChange >= 1) {
        val amountToMove = instructionsLeft.substring(0, indexOfNextFacingChange).toInt()
        if (inCube) password.moveInCube(amountToMove, map) else password.move(amountToMove, map)
        password.facing = password.facing.nextFacing(instructionsLeft[indexOfNextFacingChange])
        instructionsLeft = instructionsLeft.substring(indexOfNextFacingChange + 1)
        indexOfNextFacingChange = instructionsLeft.indexOfFirst { it == 'R' || it == 'L'}
    }

    if (inCube) password.moveInCube(instructionsLeft.toInt(), map) else password.move(instructionsLeft.toInt(), map)
    println(password.calculate())
}

fun Facing.nextFacing(instruction: Char): Facing =
    when {
        this == R -> if (instruction == 'L') U else D
        this == D -> if (instruction == 'L') R else L
        this == L -> if (instruction == 'L') D else U
        this == U -> if (instruction == 'L') L else R
        else -> R
    }

fun Char?.isValid() = this != null && this == '.' || this == '#'
fun Char?.isEmpty() = this != null && this == '.'