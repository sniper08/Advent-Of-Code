package solutions._2021

import Coordinate

typealias SquareGrid = Array<Array<Square>>

data class Square(
    val x: Int,
    val y: Int,
    val value: Int
) {
    companion object{
        const val BASINS_LIMIT = 9
    }
    private val squareCrossCoordinates = setOf(
        Coordinate(y = y - 1, x = x),
        Coordinate(y = y, x = x - 1),
        Coordinate(y = y, x = x + 1),
        Coordinate(y = y + 1, x = x)
    )

    var checked = false

    fun getCrossAround(squareGrid: SquareGrid): List<Square?> {
        val tempSquareGrid = mutableListOf<Square?>()
        for (coordinate in squareCrossCoordinates) {
            try {
                tempSquareGrid.add(squareGrid[coordinate.y][coordinate.x])
            } catch (e: Exception) {
                tempSquareGrid.add(null)
            }
        }
        return tempSquareGrid
    }

    fun getCrossAroundNoLimits(squareGrid: SquareGrid): List<Square> {
        val tempSquareGrid = mutableListOf<Square>()
        for (coordinate in squareCrossCoordinates) {
            try {
                val square = squareGrid[coordinate.y][coordinate.x]
                if (square.value != BASINS_LIMIT && !square.checked) {
                    square.checked = true
                    tempSquareGrid.add(square)
                }
            } catch (e: Exception) {
            }
        }
        return tempSquareGrid
    }
}

fun calculateRiskLevelsSum2(input: Sequence<String>) : Int {
    val lavaTubeList = input.toList()
    val lowest = mutableListOf<Int>()

    val squareGrid: SquareGrid = Array(lavaTubeList.size) { y ->
        Array(lavaTubeList[y].length) { x ->
             Square(x = x, y = y, value = lavaTubeList[y][x].digitToInt())
        }
    }

    for (lavaTube in squareGrid) {
        for (square in lavaTube) {
            var isLowest: Boolean? = null
            square.getCrossAround(squareGrid).forEach {
                it?.let { nonNullSquare ->
                    if (isLowest != false){
                        isLowest = square.value < nonNullSquare.value
                    }
                }
            }
            if (isLowest == true) {
                lowest.add(square.value + 1)
            }
        }
    }

    return lowest.sum()
}


fun calculateRiskLevelsSumBasins(input: Sequence<String>): Int {
    val lavaTubeList = input.toList()
    val lowest = mutableListOf<Square>()
    val basins = mutableListOf<Int>()

    val squareGrid: SquareGrid = Array(lavaTubeList.size) { y ->
        Array(lavaTubeList[y].length) { x ->
            Square(x = x, y = y, value = lavaTubeList[y][x].digitToInt())
        }
    }

    for (lavaTube in squareGrid) {
        for (square in lavaTube) {
            var isLowest: Boolean? = null
            square.getCrossAround(squareGrid).forEach {
                it?.let { nonNullSquare ->
                    if (isLowest != false){
                        isLowest = square.value < nonNullSquare.value
                    }
                }
            }
            if (isLowest == true) {
                square.checked = true
                lowest.add(square)
            }
        }
    }

    for (square in lowest) {
        basins.add(getBasinCount(square, squareGrid) + 1)
    }

    println(basins)
    return basins.sorted().subList(basins.size - 3, basins.size).reduce(Int::times)
}

fun getBasinCount(square: Square, squareGrid: SquareGrid): Int {
    var count = 0
    val tempSquareGrid = square.getCrossAroundNoLimits(squareGrid)

    if (tempSquareGrid.isNotEmpty()) {
        for (squareCross in tempSquareGrid) {
            count++
            count += getBasinCount(squareCross, squareGrid)
        }
    }

    return count
}
