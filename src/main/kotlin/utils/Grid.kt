package utils

import Coordinate
import LinearDirection
import LinearDirection.*
import AllSidesDirection
import AllSidesDirection.*

class Grid<T>(
    private val ySize: Int,
    private val xSize: Int,
    init: (Coordinate) -> T
) {

    private val grid = buildMap<Int, MutableMap<Int,T>> {
        (0 until ySize).map { y ->
            put(
                key = y,
                value = buildMap {
                    (0 until xSize).map { x ->
                        val coordinate = Coordinate(y = y, x = x)
                        put(key = x, value = init(coordinate))
                    }
                }.toMutableMap()
            )
        }
    }

    operator fun get(coordinate: Coordinate): T? = grid[coordinate.y]?.get(coordinate.x)
    operator fun set(coordinate: Coordinate, element: T) {
        grid[coordinate.y]?.put(key = coordinate.x, value = element)
    }

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

    fun findLinearNeighbour(
        direction: LinearDirection,
        coordinate: Coordinate
    ) : T? =
        when (direction) {
            NORTH -> this[Coordinate(y = coordinate.y - 1, x = coordinate.x)]
            WEST -> this[Coordinate(y = coordinate.y, x = coordinate.x - 1)]
            EAST -> this[Coordinate(y = coordinate.y, x = coordinate.x + 1)]
            SOUTH -> this[Coordinate(y = coordinate.y + 1, x = coordinate.x)]
        }

    fun findLinearNeighbours(coordinate: Coordinate): Map<LinearDirection, T?> = mapOf(
        NORTH to this[Coordinate(y = coordinate.y - 1, x = coordinate.x)],
        WEST to this[Coordinate(y = coordinate.y, x = coordinate.x - 1)],
        EAST to this[Coordinate(y = coordinate.y, x = coordinate.x + 1)],
        SOUTH to this[Coordinate(y = coordinate.y + 1, x = coordinate.x)]
    )

    fun findAllNeighbours(coordinate: Coordinate): Map<AllSidesDirection, T?> = mapOf(
        NW to this[Coordinate(y = coordinate.y - 1, x = coordinate.x - 1)],
        N to this[Coordinate(y = coordinate.y - 1, x = coordinate.x)],
        NE to this[Coordinate(y = coordinate.y - 1, x = coordinate.x + 1)],
        W to this[Coordinate(y = coordinate.y, x = coordinate.x - 1)],
        E to this[Coordinate(y = coordinate.y, x = coordinate.x + 1)],
        SW to this[Coordinate(y = coordinate.y + 1, x = coordinate.x - 1)],
        S to this[Coordinate(y = coordinate.y + 1, x = coordinate.x)],
        SE to this[Coordinate(y = coordinate.y + 1, x = coordinate.x + 1)],
    )

    fun yLastIndex() = ySize - 1
    fun xLastIndex() = xSize - 1
}
