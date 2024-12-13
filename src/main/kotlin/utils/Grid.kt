package utils

import Coordinate

class Grid<T>(
    private val ySize: Int,
    private val xSize: Int,
    init: (Coordinate) -> T
) {

    private val grid = buildMap<Int, Map<Int,T>> {
        (0 until ySize).map { y ->
            put(
                key = y,
                value = buildMap {
                    (0 until xSize).map { x ->
                        val coordinate = Coordinate(y = y, x = x)
                        put(key = x, value = init(coordinate))
                    }
                }
            )
        }
    }

    operator fun get(coordinate: Coordinate): T? = grid[coordinate.y]?.get(coordinate.x)

    fun flatten(): List<T> =
        grid.flatMap { row ->
            row.value.values
        }

    fun print(
        toString: (T) -> String
    ) {
        grid.values.forEach { row ->
            println(
                row.values.joinToString("") { element ->
                    toString(element)
                }
            )
        }
    }

    fun yLastIndex() = ySize - 1
    fun xLastIndex() = xSize - 1
}
