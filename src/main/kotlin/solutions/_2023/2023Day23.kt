package solutions._2023

import Coordinate
import LinearDirection
import kotlin.math.max

typealias HikingTrail = Array<Array<HikingTile>>

sealed class HikingTile(
    open val coordinate: Coordinate,
    open val symbol: Char
) {
    data class Forest(override val coordinate: Coordinate) : HikingTile(coordinate, '#')

    sealed class Walkable(
        override val coordinate: Coordinate,
        override val symbol: Char
    ) : HikingTile(coordinate, symbol) {
        abstract fun cross(hikingTrail: HikingTrail): List<Walkable>

        protected val cross = mutableListOf<Walkable>()

        data class Path(override val coordinate: Coordinate) : Walkable(coordinate, '.') {
            override fun cross(hikingTrail: HikingTrail): List<Walkable> {
                if (cross.isEmpty()) {
                    cross.addAll(
                        listOfNotNull(
                            hikingTrail.getOrNull(coordinate.y - 1)?.getOrNull(coordinate.x),
                            hikingTrail.getOrNull(coordinate.y)?.getOrNull(coordinate.x - 1),
                            hikingTrail.getOrNull(coordinate.y)?.getOrNull(coordinate.x + 1),
                            hikingTrail.getOrNull(coordinate.y + 1)?.getOrNull(coordinate.x)
                        ).filterIsInstance<Walkable>()
                    )
                }
                return cross
            }
        }

        data class Slope(
            override val coordinate: Coordinate,
            val linearDirection: LinearDirection
        ) : Walkable(coordinate, linearDirection.arrow) {
            override fun cross(hikingTrail: HikingTrail): List<Walkable> {
                if (cross.isEmpty()) {
                    cross.addAll(
                        listOfNotNull(
                            when (linearDirection) {
                                LinearDirection.NORTH -> hikingTrail.getOrNull(coordinate.y - 1)?.getOrNull(coordinate.x)
                                LinearDirection.WEST -> hikingTrail.getOrNull(coordinate.y)?.getOrNull(coordinate.x - 1)
                                LinearDirection.EAST -> hikingTrail.getOrNull(coordinate.y)?.getOrNull(coordinate.x + 1)
                                LinearDirection.SOUTH -> hikingTrail.getOrNull(coordinate.y + 1)?.getOrNull(coordinate.x)
                            }
                        ).filterIsInstance<Walkable>()
                    )
                }
                return cross
            }
        }
    }
}

data class Counter(var longest: Int)
data class HikingPath(val path: MutableSet<HikingTile.Walkable>)

data class Section(
    val startCoordinate: Coordinate,
    val nextToStartCoordinate: Coordinate,
    val nextToEndCoordinate: Coordinate,
    val endCoordinate: Coordinate,
    val size: Int
) {
    override fun equals(other: Any?): Boolean = other is Section
            && (
            (startCoordinate == other.startCoordinate && endCoordinate == other.endCoordinate)
                    ||  (startCoordinate == other.endCoordinate && endCoordinate == other.startCoordinate)
            )
}

fun calculateLongestPathInTrailNotIcy(input: Sequence<String>) {
    val hikingTrail = mapHikingTrail(input, icy = false)

    val startCoordinate = Coordinate(y = 0, x = 1)
    val endCoordinate = Coordinate(y = hikingTrail.lastIndex, x = hikingTrail.first().lastIndex - 1)

    val controlArray = Array(hikingTrail.size) { Array(hikingTrail.first().size) { Int.MIN_VALUE } }
    val pathsToEvaluate = mutableSetOf<HikingPath>()

    println("The max number of steps is ${controlArray[endCoordinate.y][endCoordinate.x]}")
}

fun calculateLongestPathInTrail(input: Sequence<String>) {
    val hikingTrail = mapHikingTrail(input, icy = false)
    val endCoordinate = Coordinate(y = hikingTrail.lastIndex, x = hikingTrail.first().lastIndex - 1)

    val sections = mutableSetOf<Section>()
    val firstPath = HikingPath(path = mutableSetOf(hikingTrail[0][1] as HikingTile.Walkable))
    val firstSection = buildSection(firstPath, hikingTrail)

    val lastPath = HikingPath(path = mutableSetOf(hikingTrail[endCoordinate.y][endCoordinate.x] as HikingTile.Walkable))
    val lastSection = buildSection(lastPath, hikingTrail)

    if (sections.none { it == firstSection }) {
        sections.add(firstSection)
    }

    if (sections.none { it == lastSection }) {
        sections.add(lastSection)
    }

    hikingTrail
        .flatten()
        .filterIsInstance<HikingTile.Walkable>()
        .mapNotNull {
            val cross = it.cross(hikingTrail)
            if (cross.size > 2) {
                it to cross
            } else {
                null
            }
        }
        .toMap()
        .entries
        .forEach {
            for (walkable in it.value) {
                val path = HikingPath(
                    path = mutableSetOf(
                        hikingTrail[it.key.coordinate.y][it.key.coordinate.x] as HikingTile.Walkable,
                        walkable
                    )
                )
                val section = buildSection(path, hikingTrail)
                if (sections.none { existingSection -> existingSection == section }) {
                    sections.add(section)
                }
            }
        }

    val flippedSections = sections.map {
        Section(it.endCoordinate, it.nextToEndCoordinate, it.nextToStartCoordinate, it.startCoordinate, it.size)
    }

    val groupedSections = (sections + flippedSections)
        .groupBy { it.startCoordinate }
        .onEach {
            println()
            println(it.key)
            it.value.forEach {
                println(it)
            }
        }

    findLongest(
        startCoordinate = Coordinate(y = 0, x = 1),
        endCoordinate = endCoordinate,
        allSections = groupedSections,
        route = mutableSetOf(),
        counter = Counter(Int.MIN_VALUE)
    )
}

private fun buildSection(
    firstPath: HikingPath,
    hikingTrail: HikingTrail
): Section {
    var currentTile = firstPath.path.last()
    var walkables = currentTile.cross(hikingTrail).filter { !firstPath.path.contains(it) }

    while (walkables.size == 1) {
        firstPath.path.add(walkables.first())
        currentTile = firstPath.path.last()
        walkables = currentTile.cross(hikingTrail).filter { !firstPath.path.contains(it) }
    }

    val startCoordinate = firstPath.path.first().coordinate
    val nextToStartCoordinate = firstPath.path.elementAt(1).coordinate
    val sectionEndCoordinate = currentTile.coordinate
    val nextToEndCoordinate = firstPath.path.elementAt(firstPath.path.size - 2).coordinate
    val size = firstPath.path.size - 1

    val section = Section(startCoordinate, nextToStartCoordinate, nextToEndCoordinate, sectionEndCoordinate, size)
    return section
}

private fun findLongest(
    startCoordinate: Coordinate,
    endCoordinate: Coordinate,
    allSections: Map<Coordinate, List<Section>>,
    route: MutableSet<Section>,
    counter: Counter
) {
    if (route.any { it.startCoordinate == startCoordinate }) {
        return
    }

    if (startCoordinate == endCoordinate) {
        val routeLength = route.routeLength()
        counter.longest = max(counter.longest, routeLength)
        println("${counter.longest} --> $routeLength")
        return
    }

    val possibleNextSections = allSections.getValue(startCoordinate)

    for (possible in possibleNextSections) {
        if (route.none { it == possible }) {
            route.add(possible)
            findLongest(
                startCoordinate = possible.endCoordinate,
                endCoordinate = endCoordinate,
                allSections = allSections,
                route = route,
                counter = counter
            )
            route.remove(route.last())
        }
    }
}

private fun findLongestByTile(
    startCoordinate: Coordinate,
    endCoordinate: Coordinate,
    hikingTrail: HikingTrail,
    route: MutableSet<HikingTile.Walkable>,
    counter: Counter
) {
//    if (startCoordinate == endCoordinate) {
//        val routeLength = route.routeLength()
//        counter.longest = max(counter.longest, routeLength)
//        println("${counter.longest} --> $routeLength")
//        return
//    }
//
//    val possibleNextWalkables = (hikingTrail[startCoordinate.y][startCoordinate.x] as HikingTile.Walkable).cross(hikingTrail)
//
//    for (possible in possibleNextWalkables) {
//        if (!route.contains(possible)) {
//            route.add(possible)
//            findLongestByTile(
//                startCoordinate = possible.coordinate,
//                endCoordinate = endCoordinate,
//                hikingTrail = hikingTrail,
//                route = route,
//                counter = counter
//            )
//            route.remove(route.last())
//        }
//    }
}

//private fun MutableSet<HikingTile.Walkable>.routeLength() = size - 1
private fun MutableSet<Section>.routeLength() = sumOf { it.size }

private fun mapHikingTrail(input: Sequence<String>, icy: Boolean) = HikingTrail(input.count()) { y ->
    Array(input.first().length) { x ->
        val char = input.elementAt(y)[x]
        val coordinate = Coordinate(y = y, x = x)

        when (char) {
            '.' -> HikingTile.Walkable.Path(coordinate)
            '#' -> HikingTile.Forest(coordinate)
            else -> {
                if (icy) {
                    HikingTile.Walkable.Slope(coordinate, LinearDirection.from(char))
                } else {
                    HikingTile.Walkable.Path(coordinate)
                }
            }
        }
    }
}

private fun findsAllCompleted(
    hikingTrail: HikingTrail,
    startCoordinate: Coordinate,
    endCoordinate: Coordinate,
    controlArray: Array<Array<Int>>
) {
//    val pq = PriorityQueue<HikingPath> { a, b ->
//        when {
//            a.path.size < b.path.size -> 1// Highest first
//            b.path.size < a.path.size -> -1
//            else -> 0
//        }
//    }
//    pq.add(HikingPath(path = setOf(hikingTrail[startCoordinate.y][startCoordinate.x] as HikingTile.Walkable)))
//
//    while (pq.isNotEmpty()) {
//        val currentPath = pq.poll()
//        val steps = currentPath.path.size - 1
//        val nextStep = steps + 1
//        val lastTile = currentPath.path.last()
//        val cross = lastTile.cross(hikingTrail)
//
//        for (walkable in cross) {
//            if (!currentPath.path.contains(walkable)) {
//                val control = controlArray[walkable.coordinate.y][walkable.coordinate.x]
//
//                if (control < nextStep) {
//                    controlArray[walkable.coordinate.y][walkable.coordinate.x] = nextStep
//
//                    val newPath = HikingPath(path = currentPath.path + walkable)
//
//                    if (walkable.coordinate != endCoordinate && !pq.contains(newPath)) {
//                        pq.add(newPath)
//                    }
//                }
//            }
//        }
//    }
}