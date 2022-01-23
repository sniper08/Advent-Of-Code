package solutions._2021

import parser.inputCleaner

const val Y = 'y'
const val X = 'x'

typealias DotGrid = Array<Array<Dot>>

data class OrigamiInstruction(
    val axis: Char,
    val index: Int
)

data class Dot(
    val y: Int = 0,
    val x: Int = 0,
) {
    var checked: Boolean = false
}

fun createGrid(input: String): DotGrid {
    val coordinates = inputCleaner(input).map { it.split(',') }
    var maxX = 0
    var maxY = 0

    coordinates.forEach {
        val x = it[0].toInt()
        val y = it[1].toInt()

        if (x > maxX) maxX = x
        if (y > maxY) maxY = y
    }

    return DotGrid(maxY + 1) { y ->
        Array(maxX + 1) { x ->
            Dot(y = y, x = x)
        }
    }.apply {
       coordinates.forEach { this[it[1].toInt()][it[0].toInt()].checked = true }
    }
}

fun getFoldingInstructions(input: String): List<OrigamiInstruction> =
    inputCleaner(input.trim())
        .toList()
        .map { it.split('=') }
        .map { OrigamiInstruction(it[0].last(), it[1].toInt()) }

fun calculateDotsOrigami(input: Sequence<String>) : Int {
    val origamiInstructions = getFoldingInstructions(input.last())

    return applyFold(origamiInstructions.first(), createGrid(input.first())).sumOf { it.count { dot -> dot.checked } }
}

fun calculateOrigamiMessage(input: Sequence<String>) {
    val origamiInstructions = getFoldingInstructions(input.last())
    var gridSlice = createGrid(input.first())

    for (instruction in origamiInstructions) {
        gridSlice = applyFold(instruction, gridSlice)
    }

    for (row in gridSlice) {
        println(row.map { if(it.checked) '*' else ' ' }.joinToString(""))
    }
}

fun applyFold(instruction: OrigamiInstruction, gridSlice: DotGrid): DotGrid {
    return if (instruction.axis == Y) {
        applyYFold(instruction.index, gridSlice)
    } else {
        applyXFold(instruction.index, gridSlice)
    }
}

fun applyYFold(foldIndex: Int, gridSlice: DotGrid): DotGrid{
    var up = foldIndex - 1
    var down = foldIndex + 1

    while (up >= 0 && down <= gridSlice.size - 1) {
        val upRow = gridSlice[up]
        val downRow = gridSlice[down]

        for ((x, upDot) in upRow.withIndex()) {
            val downDot = downRow[x]

            if (!upDot.checked) upDot.checked = downDot.checked
        }

        up--
        down++
    }

    return gridSlice.take(foldIndex).toTypedArray()
}

fun applyXFold(foldIndex: Int, gridSlice: DotGrid): DotGrid {
    var left = foldIndex - 1
    var right = foldIndex + 1

    while (left >= 0 && right <= gridSlice.first().size - 1) {
        for (row in gridSlice) {
            val leftDot = row[left]
            val rightDot = row[right]

            if (!leftDot.checked) leftDot.checked = rightDot.checked
        }

        left--
        right++
    }

    return DotGrid(gridSlice.size) { y ->
        gridSlice[y].take(foldIndex).toTypedArray()
    }
}
