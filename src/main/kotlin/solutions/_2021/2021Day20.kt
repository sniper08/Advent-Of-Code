package solutions._2021

import parser.inputCleaner
import java.lang.Exception

typealias PixelGrid = MutableList<MutableList<Pixel>>

const val ADDED_MARGIN = 2

data class Pixel(
    val x: Int,
    val y: Int,
    var value: Int,
    var valueAfter: Int
) {
    companion object {
        const val LIT_CHAR = '#'
    }

    fun isLit() = value == 1

    private fun getPixelSubGrid(pixelGrid: PixelGrid): List<Pixel> {
        val tempGrid = mutableListOf<Pixel>()
        for (yb in (y - 1)..(y + 1)) {
            for (xb in (x - 1)..(x + 1)) {
                try {
                    tempGrid.add(pixelGrid[yb][xb])
                } catch (e: Exception) {
                    // Do nothing
                }
            }
        }
        return tempGrid
    }

    private fun getBinaryIndex(pixelGrid: PixelGrid) : Int {
        val tempGrid = getPixelSubGrid(pixelGrid)

        return tempGrid.joinToString("") { it.value.toString() }.toInt(2)
    }

    fun calculateValueAfter(pixelGrid: PixelGrid, algorithm: String) {
        val binaryIndex = getBinaryIndex(pixelGrid)
        val pixel = algorithm[binaryIndex]
        valueAfter = if (pixel == LIT_CHAR) 1 else 0
    }

    fun updateValue() {
        value = valueAfter
    }
}

fun calculateLitPixels(input: Sequence<String>) {
    val algorithm = input.first()
    val imageLines = inputCleaner(input.last()).toList()
    val pixelGrid = createPixelGrid(imageLines, 200)

//    for ((i, line) in pixelGrid.withIndex()) {
//        println("$i ${line.map { if (it.value == 1) Pixel.LIT_CHAR else Pixel.UNLIT_CHAR }.joinToString("")}")
//    }

    applyAlgorithm(200, pixelGrid, algorithm)
}

fun countLitPixels(pixelGrid: PixelGrid, scrap: Int): Int {
    val countable = pixelGrid.subList(scrap, pixelGrid.size - scrap)
    var total = 0

    for (line in countable) {
        total += line.subList(scrap, line.size - scrap).count { it.isLit() }
    }

    return total
}

fun applyAlgorithm(times: Int, pixelGrid: PixelGrid, algorithm: String) {
    repeat(times) {
        pixelGrid.flatten()
            .onEach { it.calculateValueAfter(pixelGrid, algorithm) }
            .forEach { it.updateValue() }

//        println("\n")
//        for ((i, line) in pixelGrid.withIndex()) {
//            println("$i ${line.map { if (it.value == 1) Pixel.LIT_CHAR else Pixel.UNLIT_CHAR }.joinToString("")}")
//        }
        val total = countLitPixels(pixelGrid, it + 1)
        println("------ Total Lit After ${it + 1}: $total ----------")
    }
}

fun createPixelGrid(imageLines: List<String>, timesRun: Int) : PixelGrid {
    val lineLength = imageLines.first().length
    val height = imageLines.size

    val startReal = ADDED_MARGIN * timesRun

    return MutableList(height + startReal * 2 ) { y ->
        MutableList(lineLength + startReal * 2) { x ->
            val shouldBeUnlit = (y < startReal || y >= startReal + height)
                    || (x < startReal || x >= startReal + lineLength)

            if (shouldBeUnlit) {
                Pixel(x = x, y = y, value = 0, valueAfter = 0)
            } else {
                val pixel = imageLines[y - startReal][x - startReal]
                val value = if (pixel == Pixel.LIT_CHAR) 1 else 0

                Pixel(x = x, y = y, value = value, valueAfter = value)
            }
        }
    }
}
