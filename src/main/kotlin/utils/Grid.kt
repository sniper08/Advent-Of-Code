package utils

import Coordinate
import LinearDirection
import LinearDirection.*
import AllSidesDirection
import AllSidesDirection.*

interface GridElement {
    val coordinate: Coordinate
    val y get() = coordinate.y
    val x get() = coordinate.x
}

data class VerticalNeighbours<T : GridElement>(
    val northNeighbour: T?,
    val southNeighbour: T?
)

data class HorizontalNeighbours<T : GridElement>(
    val westNeighbour: T?,
    val eastNeighbour: T?
)

class Grid<T : GridElement>(
    val ySize: Int,
    val xSize: Int,
    init: (Coordinate) -> T
) {
    constructor(
        input: Sequence<String>,
        init: (Coordinate, Char) -> T
    ) : this(
        ySize = input.count(),
        xSize = input.first().length,
        init = { coordinate ->
            val rawChar = input.elementAt(coordinate.y)[coordinate.x]

            init(coordinate, rawChar)
        }
    )

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

    fun get(y: Int, x: Int): T? = grid[y]?.get(x)
    fun set(y: Int, x: Int, element: T) {
        grid[y]?.put(key = x, value = element)
    }
    operator fun get(coordinate: Coordinate): T? = grid[coordinate.y]?.get(coordinate.x)
    operator fun set(coordinate: Coordinate, element: T) {
        grid[coordinate.y]?.put(key = coordinate.x, value = element)
    }

    fun getElement(coordinate: Coordinate): T = get(coordinate) ?: error("Not found in grid!")
    fun getElement(y: Int, x: Int): T = get(y, x) ?: error("Not found in grid!")

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

    fun findVerticalNeighbours(coordinate: Coordinate) = VerticalNeighbours(
        northNeighbour = findLinearNeighbour(direction = NORTH, coordinate = coordinate),
        southNeighbour = findLinearNeighbour(direction = SOUTH, coordinate = coordinate)
    )

    fun findHorizontalNeighbours(coordinate: Coordinate) = HorizontalNeighbours(
        westNeighbour = findLinearNeighbour(direction = WEST, coordinate = coordinate),
        eastNeighbour = findLinearNeighbour(direction = EAST, coordinate = coordinate)
    )

    fun yLastIndex() = ySize - 1
    fun xLastIndex() = xSize - 1
}
