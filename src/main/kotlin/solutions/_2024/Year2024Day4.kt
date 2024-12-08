package solutions._2024

import ANSI_GREEN
import ANSI_RED
import ANSI_RESET
import Coordinate
import day.Day
import solutions._2024.XmasDirection.*
import solutions._2024.XmasDirection.S as SO
import solutions._2024.Year2024Day4.XmasLetter.*
import utils.Grid

enum class XmasDirection { NW, N, NE, W, E, SW, S, SE }

val dummyCoordinate = Coordinate(y = -1, x = -1)

class Year2024Day4 : Day {

    override val year: Int = 2024
    override val day: Int = 4

    /**
     * Find all XMAS in all directions
     */
    override fun part1(input: Sequence<String>): String {
        val grid = createXmasGrid(input)

        val foundXmasCombinations = mutableSetOf<Xmas>()

        grid
            .flatten()
            .filterIsInstance<X>()
            .forEach { letterX ->
                foundXmasCombinations.addAll(letterX.findXmasCombinations(grid = grid))
            }

        grid.print { letter ->
            val string = letter.toString()
            when (letter) {
                is X -> if (letter.inXmasCombination) "$ANSI_GREEN$string$ANSI_RESET" else string
                is M -> if (letter.inXmasCombination) "$ANSI_RED$string$ANSI_RESET" else string
                is A -> if (letter.inXmasCombination) "$ANSI_RED$string$ANSI_RESET" else string
                is S -> if (letter.inXmasCombination) "$ANSI_GREEN$string$ANSI_RESET" else string
                else -> string
            }
        }

        return "${foundXmasCombinations.count()}"
    }

    /**
     * Find X-mas crosses in all directions
     *
     * For example
     * M M
     *  A
     * S S
     */
    override fun part2(input: Sequence<String>): String {
        val grid = createXmasGrid(input)

        val xmasCrossCount = grid
            .flatten()
            .filterIsInstance<A>()
            .sumOf { letterA ->
                val isInCenterOfXmasCross = letterA.isInCenterOfXmasCross(grid = grid)

                if (isInCenterOfXmasCross) 1 else 0L
            }

        println()
        grid.print { letter ->
            val string = letter.toString()
            when (letter) {
                is M -> if (letter.inXmasCross) "$ANSI_GREEN$string$ANSI_RESET" else string
                is A -> if (letter.inXmasCross) "$ANSI_RED$string$ANSI_RESET" else string
                is S -> if (letter.inXmasCross) "$ANSI_GREEN$string$ANSI_RESET" else string
                else -> string
            }
        }

        return "$xmasCrossCount"
    }

    private fun createXmasGrid(input: Sequence<String>) = Grid<XmasLetter>(
        ySize = input.count(),
        xSize = input.first().length
    ) { coordinate ->
        val rawLetter = input.elementAt(coordinate.y)[coordinate.x].toString()

        when (rawLetter) {
            "X" -> X(coordinate = coordinate)
            "M" -> M(coordinate = coordinate)
            "A" -> A(coordinate = coordinate)
            "S" -> S(coordinate = coordinate)
            else -> Noise(coordinate = coordinate)
        }
    }

    sealed class XmasLetter {
        abstract val coordinate: Coordinate

        var inXmasCombination = false
            protected set
        var inXmasCross = false
            protected set

        data class X(override val coordinate: Coordinate) : XmasLetter() {
            fun findXmasCombinations(grid: Grid<XmasLetter>): Set<Xmas> {
                val foundXmasCombinations = mutableSetOf<Xmas>()

                for (direction in XmasDirection.entries) {
                    val foundM = findXmasLetter<M>(fromLetter = this, direction = direction, grid = grid)

                    val xmas = foundM?.traceXmasCombination(
                        grid = grid, direction = direction, xmas = Xmas(x = this, m = foundM)
                    )

                    if (xmas != null) {
                        inXmasCombination = true
                        xmas.m.inXmasCombination = true
                        xmas.a.inXmasCombination = true
                        xmas.s.inXmasCombination = true
                        foundXmasCombinations.add(xmas)
                    }
                }

                return foundXmasCombinations
            }

            override fun toString(): String = "X"
        }

        data class M(override val coordinate: Coordinate) : XmasLetter() {
            fun traceXmasCombination(grid: Grid<XmasLetter>, direction: XmasDirection, xmas: Xmas): Xmas? {
                val foundA = findXmasLetter<A>(fromLetter = this, direction = direction, grid = grid)

                return foundA?.traceXmasCombination(
                    grid = grid, direction = direction, xmas = xmas.copy(a = foundA)
                )
            }

            override fun toString(): String = "M"
        }

        data class A(override val coordinate: Coordinate) : XmasLetter() {
            fun isInCenterOfXmasCross(grid: Grid<XmasLetter>): Boolean {
                val northWestM = findXmasLetter<M>(fromLetter = this, direction = NW, grid)
                val northWestS = findXmasLetter<S>(fromLetter = this, direction = NW, grid)
                val northEastM = findXmasLetter<M>(fromLetter = this, direction = NE, grid)
                val northEastS = findXmasLetter<S>(fromLetter = this, direction = NE, grid)
                val southWestM = findXmasLetter<M>(fromLetter = this, direction = SW, grid)
                val southWestS = findXmasLetter<S>(fromLetter = this, direction = SW, grid)
                val southEastM = findXmasLetter<M>(fromLetter = this, direction = SE, grid)
                val southEastS = findXmasLetter<S>(fromLetter = this, direction = SE, grid)

                return when {
                    northWestM != null && southEastS != null -> {
                        when {
                            northEastM != null && southWestS != null ->{
                                /**
                                 * M M
                                 *  A
                                 * S S
                                 */
                                this.inXmasCross = true
                                northWestM.inXmasCross = true
                                northEastM.inXmasCross = true
                                southWestS.inXmasCross = true
                                southEastS.inXmasCross = true
                                true
                            }
                            northEastS != null && southWestM != null -> {
                                /**
                                 * M S
                                 *  A
                                 * M S
                                 */
                                this.inXmasCross = true
                                northWestM.inXmasCross = true
                                northEastS.inXmasCross = true
                                southWestM.inXmasCross = true
                                southEastS.inXmasCross = true
                                true
                            }
                            else -> false
                        }
                    }
                    northWestS != null && southEastM != null -> {
                        when {
                            northEastS != null && southWestM != null -> {
                                /**
                                 * S S
                                 *  A
                                 * M M
                                 */
                                this.inXmasCross = true
                                northWestS.inXmasCross = true
                                northEastS.inXmasCross = true
                                southWestM.inXmasCross = true
                                southEastM.inXmasCross = true
                                true
                            }
                            northEastM != null && southWestS != null -> {
                                /**
                                 * S M
                                 *  A
                                 * S M
                                 */
                                this.inXmasCross = true
                                northWestS.inXmasCross = true
                                northEastM.inXmasCross = true
                                southWestS.inXmasCross = true
                                southEastM.inXmasCross = true
                                true
                            }
                            else -> false
                        }
                    }
                    else -> false
                }
            }

            fun traceXmasCombination(grid: Grid<XmasLetter>, direction: XmasDirection, xmas: Xmas): Xmas? {
                val foundS = findXmasLetter<S>(fromLetter = this, direction = direction, grid = grid)

                return foundS
                    ?.let {
                        xmas.copy(s = foundS)
                    }
            }

            override fun toString(): String = "A"
        }

        data class S(override val coordinate: Coordinate) : XmasLetter() {
            override fun toString(): String = "S"
        }

        data class Noise(override val coordinate: Coordinate) : XmasLetter() {
            override fun toString(): String = "."
        }

        inline fun <reified T : XmasLetter> findXmasLetter(fromLetter: XmasLetter, direction: XmasDirection, grid: Grid<XmasLetter>): T? =
            when (direction) {
                NW -> grid[Coordinate(y = fromLetter.coordinate.y - 1, x = fromLetter.coordinate.x - 1)]
                N -> grid[Coordinate(y = fromLetter.coordinate.y - 1, x = fromLetter.coordinate.x)]
                NE -> grid[Coordinate(y = fromLetter.coordinate.y - 1, x = fromLetter.coordinate.x + 1)]
                W -> grid[Coordinate(y = fromLetter.coordinate.y, x = fromLetter.coordinate.x - 1)]
                E -> grid[Coordinate(y = fromLetter.coordinate.y, x = fromLetter.coordinate.x + 1)]
                SW -> grid[Coordinate(y = fromLetter.coordinate.y + 1, x = fromLetter.coordinate.x - 1)]
                SO -> grid[Coordinate(y = fromLetter.coordinate.y + 1, x = fromLetter.coordinate.x)]
                SE -> grid[Coordinate(y = fromLetter.coordinate.y + 1, x = fromLetter.coordinate.x + 1)]
            } as? T
    }

    data class Xmas(
        val x: X,
        val m: M,
        val a: A = A(coordinate = dummyCoordinate),
        val s: S = S(coordinate = dummyCoordinate)
    )
}
