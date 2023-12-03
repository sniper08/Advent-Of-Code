package solutions._2023

import Coordinate

typealias SchematicsGrid = Array<Array<SchematicsPart>>

const val EMPTY = '.'
const val GEAR = '*'

data class NumberWrapper(
    var number: String,
    val coordinates: MutableSet<Coordinate> = mutableSetOf()
) {
    fun toLong() = try { number.toLong() } catch (e: Exception) { 0 }

    val possibleCoordinates get() = buildSet<Coordinate> {
        val first = coordinates.first()
        val last = coordinates.last()

        for (i in (first.x - 1)..(last.x + 1)) {
            add(Coordinate(x = i, y = first.y - 1))
            add(Coordinate(x = i, y = first.y + 1))
        }
        add(Coordinate(x = first.x - 1, y = first.y))
        add(Coordinate(x = last.x + 1, y = last.y))
    }
}

fun findSubGrid(grid: SchematicsGrid, possibleCoordinates: Set<Coordinate>) = possibleCoordinates.mapNotNull {
    try {
        grid[it.y][it.x]
    } catch (e: Exception) {
        null
    }
}

sealed class SchematicsPart {
    abstract val char: Char
    abstract val coordinate: Coordinate

    sealed class Symbol(override val char: Char, override val coordinate: Coordinate) : SchematicsPart() {
        data class Gear(override val coordinate: Coordinate) : Symbol(GEAR, coordinate) {
            val possibleCoordinates = buildSet<Coordinate> {
                for (i in (coordinate.x - 1)..(coordinate.x + 1)) {
                    add(Coordinate(x = i, y = coordinate.y - 1))
                    add(Coordinate(x = i, y = coordinate.y + 1))
                }
                add(Coordinate(x = coordinate.x - 1, y = coordinate.y))
                add(Coordinate(x = coordinate.x + 1, y = coordinate.y))
            }
        }
        data class Other(override val char: Char, override val coordinate: Coordinate) : Symbol(char, coordinate)
    }

    data class Number(override val char: Char, val number: NumberWrapper, override val coordinate: Coordinate) : SchematicsPart()
    data class Empty(override val coordinate: Coordinate) : SchematicsPart() {
        override val char: Char = EMPTY
    }
}

fun createSchematics(input: Sequence<String>): SchematicsGrid {
    var previous: SchematicsPart = SchematicsPart.Empty(Coordinate(x = -1, y = -1))

    return SchematicsGrid(input.count()) { y ->
        Array(input.first().length) { x ->
            val c = input.elementAt(y)[x]
            val coordinate = Coordinate(x = x, y = y)
            val innerPrevious = previous

            when  {
                c.isDigit() -> {
                    when (innerPrevious) {
                        is SchematicsPart.Empty,
                        is SchematicsPart.Symbol -> {
                            SchematicsPart.Number(char = c, number = NumberWrapper(number = "$c"), coordinate = coordinate).apply {
                                number.coordinates.add(coordinate)
                            }
                        }
                        is SchematicsPart.Number -> {
                            innerPrevious.number.coordinates.add(coordinate)
                            innerPrevious.number.number = "${innerPrevious.number.number}$c"
                            SchematicsPart.Number(char = c, number = innerPrevious.number, coordinate = coordinate)
                        }
                    }
                }
                c == EMPTY -> SchematicsPart.Empty(coordinate = coordinate)
                c == GEAR -> SchematicsPart.Symbol.Gear(coordinate = coordinate)
                else -> SchematicsPart.Symbol.Other(char = c, coordinate = coordinate)
            }.also {
                previous = it
            }
        }
    }
}

fun calculateSumOfNumberPartsInEngineSchematics(input: Sequence<String>) {
    val schematics = createSchematics(input)
    val numberParts = mutableListOf<NumberWrapper>()

    schematics
        .flatten()
        .filterIsInstance<SchematicsPart.Number>()
        .map { it.number }
        .toSet()
        .forEach { number ->
            findSubGrid(schematics, number.possibleCoordinates)
                .filterIsInstance<SchematicsPart.Symbol>()
                .forEach {
                    numberParts.add(number)
                }
        }

    println("The sum of number parts is ${numberParts.sumOf { it.toLong() }}")
}

fun calculateSumOfGearRatiosInEngineSchematics(input: Sequence<String>) {
    val schematics = createSchematics(input)
    val ratios = mutableListOf<Long>()

    schematics
        .flatten()
        .filterIsInstance<SchematicsPart.Symbol.Gear>()
        .forEach { gear ->
            val numberParts = findSubGrid(schematics, gear.possibleCoordinates)
                .filterIsInstance<SchematicsPart.Number>()
                .map { it.number }
                .toSet()

            if (numberParts.size == 2) {
                ratios.add(numberParts.first().toLong() * numberParts.last().toLong())
            }
        }

    println("The sum of gear ratios is ${ratios.sum()}")
}