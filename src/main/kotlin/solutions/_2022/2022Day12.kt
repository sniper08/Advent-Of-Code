package solutions._2022

import java.util.*

typealias HillGrid = Array<Array<Hill>>
data class Hill(val y: Int, val x: Int, val value: Int) {
    fun getPossibleJumps(grid: HillGrid) = listOf(
        grid.getOrNull(y - 1)?.getOrNull(x),
        grid.getOrNull(y)?.getOrNull(x - 1),
        grid.getOrNull(y)?.getOrNull(x + 1),
        grid.getOrNull(y + 1)?.getOrNull(x)
    )
}

data class HillCoordinates(
    var startY: Int = 0,
    var startX: Int = 0,
    var endX: Int = 0,
    var endY: Int = 0
)

fun findShortestHillRoute(input: Sequence<String>) {
    val hillCoordinates = HillCoordinates()
    val hillGrid = createGrid(hillCoordinates, input)

    println("Min Steps = ${findShortestAmountOfSteps(hillCoordinates, hillGrid)}")
}
fun findShortestHillRouteScenic(input: Sequence<String>) {
    val hillCoordinates = HillCoordinates()
    val hillGrid = createGrid(hillCoordinates, input)

    val minAllStart = hillGrid.flatten().filter { it.value <= 1 }
        .minOfOrNull { hill ->
            hillCoordinates.startY = hill.y
            hillCoordinates.startX = hill.x
            findShortestAmountOfSteps(hillCoordinates, hillGrid).also {  println(if (it == Long.MAX_VALUE) "Blocked" else "$it") }
        }

    println("Min Steps = $minAllStart")
}

fun createGrid(hillCoordinates: HillCoordinates, input: Sequence<String>) = HillGrid(input.count()) { y ->
    val rowRaw = input.elementAt(y)

    Array(input.first().length) { x ->
        val text = rowRaw[x]
        val value = LOWERCASE.indexOf(text).let {
            when {
                it > -1 -> it + 1
                text == 'S' -> {
                    hillCoordinates.startY = y ; hillCoordinates.startX = x
                    0
                }
                else -> {
                    hillCoordinates.endY = y ; hillCoordinates.endX = x
                    LOWERCASE.count() + 1
                }
            }
        }

        Hill(y = y, x = x, value = value)
    }
}

fun findShortestAmountOfSteps(hillCoordinates: HillCoordinates, hillGrid: HillGrid): Long {
    val stepsArray = Array(hillGrid.size) { Array(hillGrid.first().size) { Long.MAX_VALUE } }
    stepsArray[hillCoordinates.startY][hillCoordinates.startX] = 0

    val pq = PriorityQueue<Hill> { a, b ->
        val stepsA = stepsArray[a.y][a.x]
        val stepsB = stepsArray[b.y][b.x]

        when {
            stepsA < stepsB -> -1
            stepsB > stepsA -> 1
            else -> 0
        }
    }

    pq.add(hillGrid[hillCoordinates.startY][hillCoordinates.startX])

    while (pq.isNotEmpty()) {
        val currentHill = pq.poll()

        for (possibleHill in currentHill.getPossibleJumps(hillGrid).filterNotNull()) {
            val currentSteps = stepsArray[currentHill.y][currentHill.x]
            val diff = possibleHill.value - currentHill.value

            if (possibleHill.y != hillCoordinates.startY && possibleHill.x != hillCoordinates.startX && diff < 0 || diff < 2) {
                val possibleSteps = stepsArray[possibleHill.y][possibleHill.x]

                if (possibleSteps > currentSteps + 1) {
                    if (possibleSteps != Long.MAX_VALUE) {
                        pq.remove(possibleHill)
                    }

                    stepsArray[possibleHill.y][possibleHill.x] = currentSteps + 1
                    pq.add(possibleHill)
                }
            }
        }
    }

    return stepsArray[hillCoordinates.endY][hillCoordinates.endX]
}

