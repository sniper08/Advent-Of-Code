package solutions._2023

import ANSI_BLUE
import ANSI_CYAN
import ANSI_RED
import ANSI_RED_BACKGROUND
import ANSI_RESET
import ANSI_YELLOW
import Coordinate
import doNothing
import solutions._2021.pointsClosing
import solutions._2023.Direction.*
import solutions._2023.PipeType.*
import solutions._2023.ValidationCheck.*
import java.util.Stack

typealias PipeMaze = Array<Array<PipeTile>>

enum class PipeType(val value: Char) {
    VERTICAL('|'),
    HORIZONTAL('-'),
    NORTH_WEST('J'),
    NORTH_EAST('L'),
    SOUTH_WEST('7'),
    SOUTH_EAST('F'),
    GROUND('.')
}

enum class Direction { NORTH, SOUTH, WEST, EAST }

private const val BEGIN = 'S'
private var begin: PipeTile.Begin? = null

enum class ValidationCheck { NONE, NOT_ENCLOSED, NOT_ENCLOSED_FROM_OPENING, IN_LOOP_EDGE, IN_LOOP_AND_HAS_OPENING }

sealed class PipeTile {
    abstract val coordinate: Coordinate
    abstract fun next(maze: PipeMaze): List<PipeTile>

    protected val openFrom = mutableSetOf<Direction>()
    fun openFrom(): Set<Direction> = openFrom

    val inLoop get() = openFrom.isNotEmpty()

    var checked: Boolean = false
    var checkedInLoopAndHasOpening = false

    var validationCheck: ValidationCheck = NONE

    data class Begin(override val coordinate: Coordinate) : PipeTile() {
        private var pipeType: PipeType? = null

        fun determineType(maze: PipeMaze) {
            val north = findNorth(maze)?.pipeType
            val south = findSouth(maze)?.pipeType
            val west = findWest(maze)?.pipeType

            pipeType = when {
                north == VERTICAL || north == SOUTH_WEST || north == SOUTH_EAST -> {
                    when {
                        south == VERTICAL || south == NORTH_WEST || south == NORTH_EAST -> {
                            openFrom.add(WEST) ; openFrom.add(EAST)
                            VERTICAL
                        }
                        west == NORTH_EAST || west == SOUTH_EAST || west == HORIZONTAL -> {
                            openFrom.add(SOUTH) ; openFrom.add(EAST)
                            NORTH_WEST
                        }
                        else -> {
                            openFrom.add(SOUTH) ; openFrom.add(WEST)
                            NORTH_EAST
                        } // can only connect to the EAST
                    }
                }
                south == VERTICAL || south == NORTH_WEST || south == NORTH_EAST -> {
                    when {
                        west == NORTH_EAST || west == SOUTH_EAST || west == HORIZONTAL  -> {
                            openFrom.add(NORTH) ; openFrom.add(EAST)
                            SOUTH_WEST
                        }
                        else -> {
                            openFrom.add(NORTH) ; openFrom.add(WEST)
                            SOUTH_EAST
                        } // can only connect to the EAST
                    }
                }
                else -> {
                    openFrom.add(NORTH) ; openFrom.add(SOUTH)
                    HORIZONTAL
                }
            }
        }

        override fun next(maze: PipeMaze): List<PipeTile> {
           val pipeType = pipeType ?: throw Exception("PipeType not determined")

           return when (pipeType) {
               VERTICAL -> listOfNotNull(findNorth(maze), findSouth(maze))
               HORIZONTAL -> listOfNotNull(findWest(maze), findEast(maze))
               NORTH_WEST -> listOfNotNull(findNorth(maze), findWest(maze))
               NORTH_EAST -> listOfNotNull(findNorth(maze), findEast(maze))
               SOUTH_WEST -> listOfNotNull(findSouth(maze), findWest(maze))
               SOUTH_EAST -> listOfNotNull(findSouth(maze), findEast(maze))
               GROUND -> emptyList()
           }.onEach { it.addToLoop(this) }
        }

        override fun toString() = pipeType?.value?.toString() ?: BEGIN.toString()
    }

    data class Normal(
        val pipeType: PipeType,
        override val coordinate: Coordinate
    ) : PipeTile() {
        private var addedToLoopFrom: Direction? = null

        fun addToLoop(previous: PipeTile) {
            addedToLoopFrom = when {
                previous.coordinate.y < coordinate.y -> NORTH
                previous.coordinate.y > coordinate.y -> SOUTH
                previous.coordinate.x < coordinate.x -> WEST
                else -> EAST
            }
            determineOpenSide()
        }

        private fun determineOpenSide() {
            when (pipeType) {
                VERTICAL -> { openFrom.add(WEST) ; openFrom.add(EAST) }
                HORIZONTAL -> { openFrom.add(NORTH) ; openFrom.add(SOUTH) }
                NORTH_WEST -> { openFrom.add(SOUTH) ; openFrom.add(EAST) }
                NORTH_EAST -> { openFrom.add(SOUTH) ; openFrom.add(WEST) }
                SOUTH_WEST -> { openFrom.add(NORTH) ; openFrom.add(EAST) }
                SOUTH_EAST -> { openFrom.add(NORTH) ; openFrom.add(WEST) }
                GROUND -> doNothing
            }
        }

        override fun next(maze: PipeMaze): List<PipeTile> {
            val visitedFrom = addedToLoopFrom ?: throw Exception("NOT VISITED YET")

            return when (pipeType) {
                VERTICAL -> listOfNotNull(if (visitedFrom == NORTH) findSouth(maze) else findNorth(maze))
                HORIZONTAL -> listOfNotNull(if (visitedFrom == WEST) findEast(maze) else findWest(maze))
                NORTH_WEST -> listOfNotNull(if (visitedFrom == NORTH) findWest(maze) else findNorth(maze))
                NORTH_EAST -> listOfNotNull(if (visitedFrom == NORTH) findEast(maze) else findNorth(maze))
                SOUTH_WEST -> listOfNotNull(if (visitedFrom == SOUTH) findWest(maze) else findSouth(maze))
                SOUTH_EAST -> listOfNotNull(if (visitedFrom == SOUTH) findEast(maze) else findSouth(maze))
                GROUND -> emptyList()
            }.onEach { it.addToLoop(this) }
        }

        override fun toString() = pipeType.value.toString()
    }

    fun findSubGrid(maze: PipeMaze) = listOf(
        maze.getOrNull(coordinate.y - 1)?.getOrNull(coordinate.x - 1),
        maze.getOrNull(coordinate.y - 1)?.getOrNull(coordinate.x),
        maze.getOrNull(coordinate.y - 1)?.getOrNull(coordinate.x + 1),
        maze.getOrNull(coordinate.y)?.getOrNull(coordinate.x - 1),
        maze.getOrNull(coordinate.y)?.getOrNull(coordinate.x + 1),
        maze.getOrNull(coordinate.y + 1)?.getOrNull(coordinate.x - 1),
        maze.getOrNull(coordinate.y + 1)?.getOrNull(coordinate.x),
        maze.getOrNull(coordinate.y + 1)?.getOrNull(coordinate.x + 1)
    )

    fun findCross(maze: PipeMaze) = listOf(
        maze.getOrNull(coordinate.y - 1)?.getOrNull(coordinate.x),
        maze.getOrNull(coordinate.y)?.getOrNull(coordinate.x - 1),
        maze.getOrNull(coordinate.y)?.getOrNull(coordinate.x + 1),
        maze.getOrNull(coordinate.y + 1)?.getOrNull(coordinate.x),
    )

    protected fun findNorth(maze: PipeMaze) = maze.getOrNull(coordinate.y - 1)?.getOrNull(coordinate.x) as? Normal
    protected fun findSouth(maze: PipeMaze) = maze.getOrNull(coordinate.y + 1)?.getOrNull(coordinate.x) as? Normal
    protected fun findWest(maze: PipeMaze) = maze.getOrNull(coordinate.y)?.getOrNull(coordinate.x - 1) as? Normal
    protected fun findEast(maze: PipeMaze) = maze.getOrNull(coordinate.y)?.getOrNull(coordinate.x + 1) as? Normal
}

data class InLoopOpening(
    val firstPipeTile: PipeTile,
    val secondPipeTile: PipeTile,
    val horizontal: Boolean = firstPipeTile.coordinate.y == secondPipeTile.coordinate.y
) {
    init {
        firstPipeTile.validationCheck = IN_LOOP_AND_HAS_OPENING
        secondPipeTile.validationCheck = IN_LOOP_AND_HAS_OPENING
    }

    val checked get() = firstPipeTile.checkedInLoopAndHasOpening && secondPipeTile.checkedInLoopAndHasOpening

    fun findSubgrid(maze: PipeMaze): List<PipeTile?> =
        if (horizontal) {
            listOf(
                firstPipeTile,
                maze.getOrNull(firstPipeTile.coordinate.y - 1)?.getOrNull(firstPipeTile.coordinate.x),
                maze.getOrNull(secondPipeTile.coordinate.y - 1)?.getOrNull(secondPipeTile.coordinate.x),
                secondPipeTile,
                maze.getOrNull(secondPipeTile.coordinate.y + 1)?.getOrNull(secondPipeTile.coordinate.x),
                maze.getOrNull(firstPipeTile.coordinate.y + 1)?.getOrNull(firstPipeTile.coordinate.x),
            )
        } else {
            listOf(
                firstPipeTile,
                maze.getOrNull(firstPipeTile.coordinate.y)?.getOrNull(firstPipeTile.coordinate.x + 1),
                maze.getOrNull(secondPipeTile.coordinate.y)?.getOrNull(secondPipeTile.coordinate.x + 1),
                secondPipeTile,
                maze.getOrNull(secondPipeTile.coordinate.y)?.getOrNull(secondPipeTile.coordinate.x - 1),
                maze.getOrNull(firstPipeTile.coordinate.y)?.getOrNull(firstPipeTile.coordinate.x - 1),
            )
        }
}

fun createMaze(input: Sequence<String>) = PipeMaze(input.count()) { y ->
    Array(input.first().length) { x ->
        val coordinate = Coordinate(y = y, x = x)
        val char = input.elementAt(y)[x]

        if (char == BEGIN) {
            PipeTile.Begin(coordinate)
                .also { begin = it }
        } else {
            val pipeType = when (char) {
                VERTICAL.value -> VERTICAL
                HORIZONTAL.value -> HORIZONTAL
                NORTH_WEST.value -> NORTH_WEST
                NORTH_EAST.value -> NORTH_EAST
                SOUTH_WEST.value -> SOUTH_WEST
                SOUTH_EAST.value -> SOUTH_EAST
                GROUND.value -> GROUND
                else -> throw Exception("INVALID CHAR")
            }
            PipeTile.Normal(pipeType, coordinate)
        }
    }
}

fun PipeMaze.print() {
    forEach {
        println(
            it.joinToString("") { tile ->
                when (tile) {
                    is PipeTile.Begin -> ANSI_CYAN + tile.toString() + ANSI_RESET
                    is PipeTile.Normal ->  {
                        when (tile.validationCheck) {
                            IN_LOOP_AND_HAS_OPENING -> ANSI_RED_BACKGROUND + tile.toString() + ANSI_RESET
                            IN_LOOP_EDGE -> ANSI_RED + tile.toString() + ANSI_RESET
                            NOT_ENCLOSED, NOT_ENCLOSED_FROM_OPENING -> ANSI_BLUE + "O" + ANSI_RESET
                            NONE -> {
                                if (tile.inLoop) {
                                    ANSI_YELLOW + tile.toString() + ANSI_RESET
                                } else {
                                    tile.toString()
                                }
                            }
                        }
                    }
                }
            }
        )
    }
}

fun calculateStepInLoopPipeMaze(input: Sequence<String>) {
    val maze = createMaze(input)
    var step = 0
    var found = false

    begin?.determineType(maze)

    val nextMoves: MutableList<PipeTile> = begin?.next(maze)?.toMutableList() ?: mutableListOf()

    while (!found) {
        step++
        val firstNext = nextMoves.first().next(maze).first()
        val secondNext = nextMoves.last().next(maze).first()

        if (firstNext == secondNext) {
            found = true
        } else {
            nextMoves[0] = firstNext
            nextMoves[1] = secondNext
        }
    }
    println("Step $step")
    maze.print()
}

fun calculateEnclosedInLoopTilesCountPipeMaze(input: Sequence<String>) {
    val maze = createMaze(input)
    var found = false

    begin?.determineType(maze)

    val nextMoves: MutableList<PipeTile> = begin?.next(maze)?.toMutableList() ?: mutableListOf()

    while (!found) {
        val firstNext = nextMoves.first().next(maze).first()
        val secondNext = nextMoves.last().next(maze).first()

        if (firstNext == secondNext) {
            found = true
        } else {
            nextMoves[0] = firstNext
            nextMoves[1] = secondNext
        }
    }

    val loopEdges = findEdges(maze)
    val notEnclosedByOpening = traverseOpeningsAndFindNotEnclosed(loopEdges, maze)

    val notEnclosedFromOpeningStack = Stack<PipeTile>()
    notEnclosedByOpening.forEach { notEnclosedFromOpeningStack.push(it) }

    var nextNeighbourToInspect: PipeTile? = try { notEnclosedFromOpeningStack.pop() } catch (e: Exception) { null }

    while (nextNeighbourToInspect != null) {
        nextNeighbourToInspect.checked = true
        val subGrid = nextNeighbourToInspect.findSubGrid(maze).filterNotNull()
        val innerLoopEdges = mutableSetOf<PipeTile>()

        for (pipeInSubGrid in subGrid) {
            if (pipeInSubGrid.inLoop) {
                innerLoopEdges.add(pipeInSubGrid)
            } else {
                pipeInSubGrid.validationCheck = NOT_ENCLOSED
                if (!pipeInSubGrid.checked && !notEnclosedFromOpeningStack.contains(pipeInSubGrid)) {
                    notEnclosedFromOpeningStack.push(pipeInSubGrid)
                }
            }
        }

        val innerNotEnclosedByOpening = traverseOpeningsAndFindNotEnclosed(innerLoopEdges, maze)
        innerNotEnclosedByOpening.forEach { notEnclosedFromOpeningStack.push(it) }

        nextNeighbourToInspect = try { notEnclosedFromOpeningStack.pop() } catch (e: Exception) { null }
    }

    maze.print()

    println("The total enclosed is ${maze.flatten().count { !it.inLoop && it.validationCheck == NONE }}")
}

private fun traverseOpeningsAndFindNotEnclosed(
    loopEdges: Set<PipeTile>,
    maze: PipeMaze
): MutableSet<PipeTile> {
    val openingsInspectionStack = Stack<InLoopOpening>()

    for (edge in loopEdges) {
        val cross = edge.findCross(maze)
        val top = cross.getOrNull(0)
        val left = cross.getOrNull(1)
        val right = cross.getOrNull(2)
        val bottom = cross.getOrNull(3)

        when {
            top != null && top.validationCheck == NOT_ENCLOSED -> {
                when {
                    left != null && left.validationCheck == NOT_ENCLOSED -> {
                        bottom.findInLoopOpeningWith(edge)?.let { openingsInspectionStack.push(it) }
                        right.findInLoopOpeningWith(edge)?.let { openingsInspectionStack.push(it) }
                    }

                    right != null && right.validationCheck == NOT_ENCLOSED -> {
                        bottom.findInLoopOpeningWith(edge)?.let { openingsInspectionStack.push(it) }
                        left.findInLoopOpeningWith(edge)?.let { openingsInspectionStack.push(it) }
                    }

                    else -> {
                        val leftInLoopOpening =
                            left.findInLoopOpeningWith(edge)?.let { openingsInspectionStack.push(it) }
                        val rightInLoopOpening =
                            right.findInLoopOpeningWith(edge)?.let { openingsInspectionStack.push(it) }

                        if (leftInLoopOpening != null || rightInLoopOpening != null) {
                            bottom.findInLoopOpeningWith(edge)?.let { openingsInspectionStack.push(it) }
                        }
                    }
                }
            }

            left != null && left.validationCheck == NOT_ENCLOSED -> {
                when {
                    bottom != null && bottom.validationCheck == NOT_ENCLOSED -> {
                        top.findInLoopOpeningWith(edge)?.let { openingsInspectionStack.push(it) }
                        right.findInLoopOpeningWith(edge)?.let { openingsInspectionStack.push(it) }
                    }

                    else -> {
                        val topInLoopOpening = top.findInLoopOpeningWith(edge)?.let { openingsInspectionStack.push(it) }
                        val bottomInLoopOpening =
                            bottom.findInLoopOpeningWith(edge)?.let { openingsInspectionStack.push(it) }

                        if (topInLoopOpening != null || bottomInLoopOpening != null) {
                            right.findInLoopOpeningWith(edge)?.let { openingsInspectionStack.push(it) }
                        }
                    }
                }
            }

            right != null && right.validationCheck == NOT_ENCLOSED -> {
                when {
                    bottom != null && bottom.validationCheck == NOT_ENCLOSED -> {
                        top.findInLoopOpeningWith(edge)?.let { openingsInspectionStack.push(it) }
                        left.findInLoopOpeningWith(edge)?.let { openingsInspectionStack.push(it) }
                    }

                    else -> {
                        val topInLoopOpening = top.findInLoopOpeningWith(edge)?.let { openingsInspectionStack.push(it) }
                        val bottomInLoopOpening =
                            bottom.findInLoopOpeningWith(edge)?.let { openingsInspectionStack.push(it) }

                        if (topInLoopOpening != null || bottomInLoopOpening != null) {
                            left.findInLoopOpeningWith(edge)?.let { openingsInspectionStack.push(it) }
                        }
                    }
                }
            }

            bottom != null && bottom.validationCheck == NOT_ENCLOSED -> {
                val leftInLoopOpening = left.findInLoopOpeningWith(edge)?.let { openingsInspectionStack.push(it) }
                val rightInLoopOpening = right.findInLoopOpeningWith(edge)?.let { openingsInspectionStack.push(it) }

                if (leftInLoopOpening != null || rightInLoopOpening != null) {
                    top.findInLoopOpeningWith(edge)?.let { openingsInspectionStack.push(it) }
                }
            }
        }
    }

    var nextInLoopToInspect = try { openingsInspectionStack.pop() } catch (e: Exception) { null }
    val notEnclosedByOpening = mutableSetOf<PipeTile>()

    while (nextInLoopToInspect != null) {
        nextInLoopToInspect.firstPipeTile.checkedInLoopAndHasOpening = true
        nextInLoopToInspect.secondPipeTile.checkedInLoopAndHasOpening = true

        val subGrid = nextInLoopToInspect.findSubgrid(maze).filterNotNull()

        subGrid
            .windowed(2, 1)
            .toMutableList()
            .apply {
                add(listOf(subGrid.last(), subGrid.first()))
            }
            .mapNotNull {
                it.filter { windowedTile -> !windowedTile.inLoop && windowedTile.validationCheck == NONE }
                    .forEach { innerTile ->
                        innerTile.validationCheck = NOT_ENCLOSED
                        notEnclosedByOpening.add(innerTile)
                    }

                findInLoopOpening(it)
            }
            .forEach {
                if (!it.checked && !openingsInspectionStack.contains(it)) {
                    openingsInspectionStack.push(it)
                }
            }

        nextInLoopToInspect = try { openingsInspectionStack.pop() } catch (e: Exception) { null }
    }
    return notEnclosedByOpening
}

private fun findEdges(maze: PipeMaze): Set<PipeTile> {
    val inspectionsStack = Stack<PipeTile>()
    val starting = (maze[0] + maze[maze.lastIndex]).toMutableList()
    maze.drop(1).dropLast(1).forEach {
        starting.add(it[0])
        starting.add(it[maze.first().lastIndex])
    }
    starting.forEach { inspectionsStack.push(it) }

    var nextToInspect: PipeTile? = maze[0][0]
    val loopEdges = mutableSetOf<PipeTile>()

    while (nextToInspect != null) {
        nextToInspect.checked = true
        nextToInspect.validationCheck = NOT_ENCLOSED
        val subGrid = nextToInspect.findSubGrid(maze).filterNotNull()

        if (nextToInspect.coordinate.x == 18 && nextToInspect.coordinate.y == 19) {
            nextToInspect.checked
        }

        for (pipeInSubGrid in subGrid) {
            if (pipeInSubGrid.inLoop) {
                pipeInSubGrid.validationCheck = IN_LOOP_EDGE
                loopEdges.add(pipeInSubGrid)
            } else {
                if (!pipeInSubGrid.checked && !inspectionsStack.contains(pipeInSubGrid)) {
                    inspectionsStack.push(pipeInSubGrid)
                }
            }
        }

        nextToInspect = try { inspectionsStack.pop() } catch (e: Exception) { null }
    }
    return loopEdges
}

private fun PipeTile?.findInLoopOpeningWith(edge: PipeTile) = this?.let { if (it.inLoop) findInLoopOpening(listOf(edge, it)) else null }

private fun findInLoopOpening(it: List<PipeTile>): InLoopOpening? {
    val firstPipeTile = it[0]
    val secondPipeTile = it[1]
    val horizontal = firstPipeTile.coordinate.y == secondPipeTile.coordinate.y
    val firstTop = (secondPipeTile.coordinate.y - firstPipeTile.coordinate.y) > 0
    val firstLeft = (secondPipeTile.coordinate.x - firstPipeTile.coordinate.x) > 0

    return when {
        horizontal && firstLeft && firstPipeTile.openEast() && secondPipeTile.openWest() -> {
            InLoopOpening(firstPipeTile = firstPipeTile, secondPipeTile = secondPipeTile)
        }
        horizontal && !firstLeft && secondPipeTile.openEast() && firstPipeTile.openWest() -> {
            InLoopOpening(firstPipeTile = secondPipeTile, secondPipeTile = firstPipeTile)
        }
        !horizontal && firstTop && firstPipeTile.openSouth() && secondPipeTile.openNorth() -> {
            InLoopOpening(firstPipeTile = firstPipeTile, secondPipeTile = secondPipeTile)
        }
        !horizontal && !firstTop && secondPipeTile.openSouth() && firstPipeTile.openNorth() -> {
            InLoopOpening(firstPipeTile = secondPipeTile, secondPipeTile = firstPipeTile)
        }
        else -> null
    }
}

fun PipeTile.openNorth() = openFrom().contains(NORTH)
fun PipeTile.openSouth() = openFrom().contains(SOUTH)
fun PipeTile.openWest() = openFrom().contains(WEST)
fun PipeTile.openEast() = openFrom().contains(EAST)
