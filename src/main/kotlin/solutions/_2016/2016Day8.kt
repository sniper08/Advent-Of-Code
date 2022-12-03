package solutions._2016

import java.lang.Math.abs

const val WIDTH = 50
const val HEIGHT = 6

typealias PixelGrid = Array<Array<Pixel>>
data class Pixel(val x: Int, val y: Int) {
    var on: Boolean = false
    var afterRotate: Boolean? = null

    fun normalize() {
        afterRotate?.let {
            on = it
            afterRotate = null
        }
    }
}

enum class Move { RECT, ROTATE }
enum class RotateDirection { COLUMN, ROW }

fun calculateLitOnPixelsLittleScreen(input: Sequence<String>) {
    val grid = PixelGrid(HEIGHT) { y -> Array(WIDTH) { x -> Pixel(x, y) } }

    input.forEach {
        val move = it.split(" ")

        when (move.first().uppercase()) {
            Move.RECT.name -> processRect(move.last().split("x"), grid)
            Move.ROTATE.name -> processRotate(move, grid)
        }

        grid.forEach { row ->
            println(row.joinToString("") { pixel -> if (pixel.on) "#" else "." })
        }
        println()
    }

    println("Total lit pixels: ${grid.flatten().count { it.on }}")
}

fun processRect(miniGrid: List<String>, grid: PixelGrid) {
    val width = miniGrid.first().toInt()
    val height = miniGrid.last().toInt()

    repeat(height) { i ->
        grid[i].take(width).forEach { pixel -> pixel.on = true }
    }
}
fun processRotate(move: List<String>, grid: PixelGrid) {
    val index = move[2].split("=").last().toInt()
    val by = move.last().toInt()

    when (move[1].uppercase()) {
        RotateDirection.COLUMN.name -> {
            rotateOnValidBy(by, grid, grid) { normalizedBy ->
                for ((i, row) in grid.withIndex()) {
                    row[index].afterRotate = grid[calculatePreviousIndex(i, normalizedBy, grid)][index].on
                }
            }
        }
        RotateDirection.ROW.name -> {
            val row = grid[index]

            rotateOnValidBy(by, row, grid) { normalizedBy ->
                for ((i, pixel) in row.withIndex()) {
                    pixel.afterRotate = row[calculatePreviousIndex(i, normalizedBy, row)].on
                }
            }
        }
    }
}

fun <T> rotateOnValidBy(by: Int, array: Array<T>, grid: PixelGrid, onRotate: (Int) -> Unit) {
    if (by != array.size) {
        onRotate(if (by > array.size) by % array.size else by)
        grid.flatten().forEach { it.normalize() }
    }
}

fun <T> calculatePreviousIndex(i: Int, normalizedBy: Int, array: Array<T>): Int {
    val calculatedIndex = i - normalizedBy
    return if (calculatedIndex < 0) array.size - abs(calculatedIndex) else calculatedIndex
}


