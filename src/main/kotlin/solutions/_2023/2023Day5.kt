package solutions._2023

import doNothing
import parser.inputCleaner

data class Range(val start: Long, val end: Long) {
    fun findPositionInRange(toFind: Long): Long {
        return if (toFind in start..end) {
            toFind - start
        } else {
            -1
        }
    }
}

data class Mapping(
    val source: Range,
    val destination: Range,
) {
    fun findDestinationValue(toFind: Long): Long {
        val position = source.findPositionInRange(toFind)

        return if (position > -1) {
            destination.start + position
        } else {
            toFind
        }
    }

    fun findDestinationRanges(toFind: MutableSet<Range>, found: MutableSet<Range>) {
        toFind.toSet().forEachIndexed { i, range ->
            when {
                range.end < source.start || range.start > source.end -> doNothing
                range.start >= source.start -> {
                    if (range.end <= source.end) {
                        val startAdd = range.start - source.start
                        val endReduce = source.end - range.end

                        found.add(Range(destination.start + startAdd, destination.end - endReduce))
                        toFind.remove(range)
                    } else {
                        val startAdd = range.start - source.start
                        found.add(Range(destination.start + startAdd, destination.end))
                        toFind.remove(range)

                        toFind.add(Range(source.end + 1, range.end))
                    }
                }
                range.end <= source.end -> {
                    val endReduce = source.end - range.end
                    found.add(Range(destination.start, destination.end - endReduce))
                    toFind.remove(range)

                    toFind.add(Range(range.start, source.start - 1))
                }
            }
        }
    }
}

data class Destination(
    val name: String,
    val value: Long
)

data class RangeDestination(
    val name: String,
    val ranges: Set<Range>
) {
    fun print() {
        println("----------")
        println(name)
        ranges.sortedBy { it.start }.forEach(::println)
        println("----------")
    }
}

data class ElementMap(
    val source: String,
    val destination: String,
    val mappings: List<Mapping>
) {
    fun findDestination(toFind: Long): Destination {
        var valueFound = toFind

        for (mapping in mappings) {
            valueFound = mapping.findDestinationValue(toFind)
            if (valueFound != toFind) {
                break
            }
        }

        return Destination(destination, valueFound)
    }

    fun findRangeDestination(toFind: Set<Range>): RangeDestination {
        val mutableToFind = toFind.toMutableSet()
        val found = mutableSetOf<Range>()

        for (mapping in mappings) {
            mapping.findDestinationRanges(mutableToFind, found)

            if (mutableToFind.isEmpty()) {
                break
            }
        }

        return RangeDestination(destination, found + mutableToFind)
    }
}

fun calculateLowestLocationForSeedRanges(input: Sequence<String>) {
    val seedRanges = input
        .first()
        .split(": ", " ").drop(1)
        .windowed(2, 2)
        .map {
            val start = it[0].toLong()
            val elements = it[1].toLong() - 1

            Range(start, start + elements)
        }.toSet()

    val elementMaps = createElementMaps(input)

    var rangeDestination = elementMaps.values.first().findRangeDestination(seedRanges)
    var currentElementMap = elementMaps[rangeDestination.name]

    while (currentElementMap != null) {
        rangeDestination = currentElementMap.findRangeDestination(rangeDestination.ranges)
        currentElementMap = elementMaps[rangeDestination.name]
    }

    println("Lowest location number in ranges ${rangeDestination.ranges.minOf { it.start }}")
}

fun calculateLowestLocationForSeed(input: Sequence<String>) {
    val seeds = input
        .first()
        .split(": ", " ").drop(1)
        .map { it.toLong() }
        .toSet()

    val elementMaps = createElementMaps(input)
    val lowest = seeds.minOf {
        println("------------------")
        var currentDestination: Destination = elementMaps.values.first().findDestination(it)
        println("$it -> $currentDestination")

        var nextElementMap = elementMaps[currentDestination.name]

        while (nextElementMap != null) {
            val value = currentDestination.value
            currentDestination = nextElementMap.findDestination(currentDestination.value)
            nextElementMap = elementMaps[currentDestination.name]
            println("$value -> $currentDestination")
        }

        println("------------------")
        currentDestination.value
    }

    println("Lowest location number $lowest")
}

private fun createElementMaps(input: Sequence<String>): Map<String, ElementMap> =
    input
        .drop(1)
        .map {
            val lines = inputCleaner(it)
            val firstLine = lines.first().split("-", " ")
            val source = firstLine[0]
            val destination = firstLine[2]

            val mappings = lines
                .drop(1)
                .map { line ->
                    val split = line.split(" ")
                    val destinationStart = split[0].toLong()
                    val sourceStart = split[1].toLong()
                    val elements = split[2].toLong() - 1L

                    Mapping(
                        source = Range(sourceStart, sourceStart + elements),
                        destination = Range(destinationStart, destinationStart + elements)
                    )
                }.toList()

            source to ElementMap(source, destination, mappings)
        }.toMap()