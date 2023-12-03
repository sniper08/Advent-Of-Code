package solutions._2021

import Coordinate
import kotlin.math.abs

typealias Grid = Array<Array<CheckPoint>>

private const val MIN_CHECKED_TIMES = 2
data class CheckPoint(var checked: Boolean = false, var timesChecked: Int = 0, var overlapCounted: Boolean = false) {
    fun overlapsAfterChecked(): Boolean {
        checked = true
        timesChecked++

        return if (timesChecked >= MIN_CHECKED_TIMES && !overlapCounted) {
            overlapCounted = true
            true
        } else {
            false
        }
    }
}

class HydrothermalCalculator(
    private val fullSweep: Boolean,
    private val input: Sequence<String>
) {
    private var overlapCount = 0

    fun calculateHydroThermalPoint(): Int {
        val coordinates = getCoordinates(input)
        val grid: Grid

        if (fullSweep) {
            val (flatLineCoordinates, diagonalCoordinates) = coordinates.partition { it.isFlatLine() }
            grid = createGrid(coordinates)
            calculateFlatLineOverlap(flatLineCoordinates, grid)
            calculateDiagonalOverlap(diagonalCoordinates, grid)
        } else {
            val flatLineCoordinates = coordinates.filter { it.isFlatLine() }
            grid = createGrid(flatLineCoordinates)
            calculateFlatLineOverlap(flatLineCoordinates, grid)
        }

        return overlapCount
    }

    private fun calculateDiagonalOverlap(coordinates: List<Pair<Coordinate, Coordinate>>, grid: Grid) {
        for (coordinate in coordinates) {
            checkDiagonalLine(
                startCoordinate = coordinate.first,
                grid = grid,
                ascendingY = coordinate.first.y < coordinate.second.y,
                ascendingX = coordinate.first.x < coordinate.second.x,
                numberOfSteps = coordinate.first.x - coordinate.second.x
            )
        }
    }

    private fun checkDiagonalLine(
        startCoordinate: Coordinate,
        grid: Grid,
        ascendingY: Boolean,
        ascendingX: Boolean,
        numberOfSteps: Int
    ) {
        for (i in 0..abs(numberOfSteps)) {
            val y = startCoordinate.y + (if (ascendingY) i else -i)
            val x = startCoordinate.x + (if (ascendingX) i else -i)

            if (grid[y][x].overlapsAfterChecked()) overlapCount++
        }
    }

    private fun calculateFlatLineOverlap(coordinates: List<Pair<Coordinate, Coordinate>>, grid: Grid) {
        for (coordinate in coordinates) {
            checkFlatLine(
                coordinate = coordinate,
                grid = grid,
                constantX = coordinate.first.x == coordinate.second.x
            )
        }
    }

    private fun checkFlatLine(coordinate: Pair<Coordinate, Coordinate>, grid: Grid, constantX: Boolean) {
        val range = getRange(coordinate, !constantX)

        for (i in range) {
            val y = if (constantX) i else coordinate.first.y
            val x = if (constantX) coordinate.first.x else i

            if (grid[y][x].overlapsAfterChecked()) overlapCount++
        }
    }

    private fun getRange(coordinate: Pair<Coordinate, Coordinate>, xRange: Boolean): IntRange = coordinate
        .let {
            val min = if (xRange) minOf(it.first.x, it.second.x) else minOf(it.first.y, it.second.y)
            val max = if (xRange) maxOf(it.first.x, it.second.x) else maxOf(it.first.y, it.second.y)

            min..max
        }

    private fun createGrid(coordinates: List<Pair<Coordinate, Coordinate>>): Grid = coordinates
        .fold(Coordinate(0, 0)) { gridSize, coordinate ->
            val maxX = maxOf(coordinate.first.x, coordinate.second.x)
            val maxY = maxOf(coordinate.first.y, coordinate.second.y)

            Coordinate(x = maxOf(gridSize.x, maxX), y = maxOf(gridSize.y, maxY))
        }.let { gridSize ->
            Array(gridSize.y + 1) { Array(gridSize.x + 1) { CheckPoint() } }
        }

    private fun getCoordinates(input: Sequence<String>) = input
        .map { coordinateString ->
            coordinateString.replace(" -> ",",")
                .split(",")
                .windowed(2, 2)
                .map { Coordinate(it[0].toInt(), it[1].toInt()) }
        }
        .map { Pair(it[0], it[1]) }
        .toList()

    private fun Pair<Coordinate, Coordinate>.isFlatLine() = first.x == second.x || first.y == second.y
}
