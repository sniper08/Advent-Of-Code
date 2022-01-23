package solutions._2021

import java.util.*
import kotlin.math.abs

const val OPEN = '.'

val costs = mapOf(
    'A' to 1,
    'B' to 10,
    'C' to 100,
    'D' to 1000
)

data class LocationGrid(val grid: List<List<Char>>) {
    private val housesLength = grid.size - 3
    val houses = mapOf(
        'A' to House('A', 3, MutableList(housesLength) { y -> grid[y + 2][3] }),
        'B' to House('B', 5, MutableList(housesLength) { y -> grid[y + 2][5] }),
        'C' to House('C', 7, MutableList(housesLength) { y -> grid[y + 2][7] }),
        'D' to House('D', 9, MutableList(housesLength) { y -> grid[y + 2][9] })
    )

    fun generateNextGrids() : Set<LocationGridCost> {
        val nextGrids = mutableSetOf<LocationGridCost>()
        val hallway = grid[1]

        moveFromHallway(nextGrids, hallway)
        moveFromHouse(nextGrids, hallway)

        return nextGrids
    }

    private fun moveFromHouse(nextGrids: MutableSet<LocationGridCost>, hallway: List<Char>) {
        val openInHallway = listOf(1, 2, 4, 6, 8, 10, 11).map { IndexedValue(it, hallway[it]) }.filter { !it.value.isLetter() }

        for (house in houses.values) {
            if (house.incomplete()) {
                val content = house.content.withIndex().filter { it.value.isLetter() && house.isFree(it.index) }
                for (inHouse in content) {
                    val distanceOut = inHouse.index + 1

                    for (open in openInHallway) {
                        if (isHallwayFree(house.index, open.index, hallway)) {
                            val distanceToOpen = calculateDistanceInHallway(house.index, open.index) + distanceOut
                            val newCost = distanceToOpen * costs[inHouse.value]!!

                            nextGrids.add(
                                LocationGridCost(
                                    LocationGrid(
                                        List(grid.size) { y ->
                                            when (y) {
                                                1 -> grid[1].toMutableList()
                                                    .apply { removeAt(open.index); add(open.index, inHouse.value) }
                                                distanceOut + 1 -> grid[distanceOut + 1].toMutableList()
                                                    .apply { removeAt(house.index); add(house.index, OPEN) }
                                                else -> grid[y]
                                            }
                                        }
                                    ),
                                    cost = newCost
                                )
                            )
                        }
                    }
                }
            }
        }
    }

    private fun moveFromHallway(nextGrids: MutableSet<LocationGridCost>, hallway: List<Char>) {
        val couldMoveToHouse = hallway.withIndex().filter { it.value.isLetter() }
        for (inHallway in couldMoveToHouse) {
            val house = houses[inHallway.value]!!
            val openInHouse = house.open()
            if (isHallwayFree(inHallway.index, house.index, hallway) && openInHouse > 0){
                val distanceToHouse = calculateDistanceInHallway(inHallway.index, house.index) + openInHouse
                val newCost = distanceToHouse * costs[house.type]!!

                nextGrids.add(
                    LocationGridCost(
                        LocationGrid(
                            List(grid.size) { y ->
                                when (y) {
                                    1 -> grid[1].toMutableList().apply { removeAt(inHallway.index) ; add(inHallway.index, OPEN) }
                                    openInHouse + 1 -> grid[openInHouse + 1].toMutableList().apply { removeAt(house.index) ; add(house.index, inHallway.value) }
                                    else -> grid[y]
                                }
                            }
                        ),
                        cost = newCost
                    )
                )
            }
        }
    }

    private fun House.isFree(indexLeave: Int) = content.subList(0, indexLeave).all { it == OPEN }
    private fun calculateDistanceInHallway(current: Int, to: Int) = abs(current - to)
    private fun isHallwayFree(from: Int, to: Int, hallway: List<Char>): Boolean {
        val range = if (from < to){
            from + 1..to
        } else {
            to until from
        }

        return hallway.slice(range).all { it == OPEN }
    }

    fun print() {
        println()
        grid.forEach { println(it.joinToString("") { it.toString() }) }
    }
}

data class LocationGridCost(val locationGrid: LocationGrid, val cost: Int)

data class House(
    val type: Char,
    val index: Int,
    val content: MutableList<Char>
) {
    fun isComplete() = content.all { it == type }
    fun open() = if (content.all { it == OPEN || it == type }) {
        content.count { it == OPEN }
    } else {
        0
    }
    fun incomplete() = !content.all { it == OPEN || it == type }
}

fun calculateAmphipodsMoves(input: Sequence<String>) {
    val locationGrid = createLocationGrid(input.toList())

    val pq = PriorityQueue<LocationGridCost> { a,b ->
        when {
            (a?.cost ?: 0) < (b?.cost ?: 0) -> -1
            (a?.cost ?: 0) > (b?.cost ?: 0) -> 1
            else -> 0
        }
    }.apply { add(LocationGridCost(locationGrid, 0)) }
    val visited = mutableSetOf<LocationGridCost>()
    val costs = mutableMapOf<LocationGrid, Int>().withDefault { Int.MAX_VALUE }

    while (pq.isNotEmpty()) {
        val current = pq.poll()
        visited.add(current)
        current.locationGrid.generateNextGrids().forEach { next ->
            if (!visited.contains(next)) {
                val newCost = current.cost + next.cost
                if (costs.getValue(next.locationGrid) > newCost) {
                    costs[next.locationGrid] = newCost
                    pq.add(next.copy(cost = newCost))
                }
            }
        }
    }

    val totalEnergy = costs.filter { it.key.houses.values.all { it.isComplete() } }.values.first()

    println("---- Total Energy: $totalEnergy -----")
}

fun createLocationGrid(input: List<String>) : LocationGrid {
    val grid = List(input.size) { y ->
        List(input.first().length) { x ->
            val char = input[y].withIndex().find { it.index == x }?.value
            char ?: ' '
        }
    }
    return LocationGrid(grid)
}







