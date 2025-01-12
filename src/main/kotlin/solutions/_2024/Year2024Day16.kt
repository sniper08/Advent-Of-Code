package solutions._2024

import ANSI_CYAN
import ANSI_GREEN
import ANSI_RED
import ANSI_RESET
import Coordinate
import LinearDirection
import LinearDirection.*
import createAnsi
import day.Day
import solutions._2024.Year2024Day16.MazeSection
import solutions._2024.Year2024Day16.MazeSection.PathTile
import solutions._2024.Year2024Day16.MazeSection.Wall
import utils.Grid
import utils.GridElement
import java.util.*

typealias Maze = Grid<MazeSection>

class Year2024Day16 : Day {

    /**
     * Find the cost the best path reindeer could take
     */
    override fun part1(input: Sequence<String>): String {
        val maze = createMaze(input = input)
       // maze.print()

        val bestPath = findBestPath(maze = maze)
        //plotAllPaths(maze = maze, bestPaths = bestPaths)

        return "${bestPath.points}"
    }

    /**
     * Find the count of maze section that are used in any best path a reindeer could take
     */
    override fun part2(input: Sequence<String>): String {
        val maze = createMaze(input = input)
       // maze.print()

        val bestPaths = findAllBestPaths(maze = maze)
        //maze.plotAllPaths(bestPaths = bestPaths)

        println("Best Paths: ${bestPaths.size}")

        val allPathTilesInABestPaths = bestPaths
            .flatMap { it.tiles }
            .map { it.coordinate }
            .toSet()

     //   maze.plotAllTilesInBestPaths(pathTilesCoordinates = allPathTilesInABestPaths)

        return "${allPathTilesInABestPaths.size}"
    }

    private fun createMaze(input: Sequence<String>) = Maze(input = input) { coordinate, rawChar ->
        when (rawChar) {
            '#' -> Wall(coordinate = coordinate)
            'S' -> PathTile(coordinate = coordinate, isStart = true, currentDirection = EAST)
            'E' -> PathTile(coordinate = coordinate, isEnd = true)
            else -> PathTile(coordinate = coordinate)
        }
    }

    private fun findBestPath(maze: Maze): Path {
        val pathTileCheckGrid = createPathTileCheckGrid(maze = maze)
        val pq = createPathPriorityQueue(maze = maze)

        while (pq.isNotEmpty()) {
            val currentPath = pq.poll()

            for (nextPathTile in currentPath.tiles.last().findNextPathTiles(maze = maze, route = currentPath)) {
                val possibleNextPath = currentPath.addNextTile(pathTile = nextPathTile)
                val controlPassed = pathTileCheckGrid.getElement(nextPathTile.coordinate)
                    .checkFor(nextPath = possibleNextPath, allowMultiple = false)

                if (controlPassed) {
                    if (nextPathTile.isEnd) return possibleNextPath

                    pq.add(possibleNextPath)
                }
            }
        }

        return Path(points = 0, tiles = emptySet())
    }

    private fun findAllBestPaths(maze: Maze): Set<Path> {
        val bestPaths = mutableSetOf<Path>()

        val pathTileCheckGrid = createPathTileCheckGrid(maze = maze)
        val pq = createPathPriorityQueue(maze = maze)

        while (pq.isNotEmpty()) {
            val currentPath = pq.poll()
            val completedPoints = bestPaths.lastOrNull()?.points ?: Long.MAX_VALUE

            if (currentPath.points < completedPoints) {
                for (nextPathTile in currentPath.tiles.last().findNextPathTiles(maze = maze, route = currentPath)) {
                    val possibleNextPath = currentPath.addNextTile(pathTile = nextPathTile)
                    val controlPassed = pathTileCheckGrid.getElement(nextPathTile.coordinate)
                        .checkFor(nextPath = possibleNextPath, allowMultiple = true)

                    if (controlPassed) {
                        if (nextPathTile.isEnd && possibleNextPath.points <= completedPoints) {
                            bestPaths.add(possibleNextPath)
                            continue
                        }

                        pq.add(possibleNextPath)
                    }
                }
            }
        }

        return bestPaths
    }

    private fun createPathTileCheckGrid(maze: Maze) = Grid<PathTileCheck>(
        ySize = maze.yLastIndex(),
        xSize = maze.xLastIndex()
    ) {
        PathTileCheck()
    }.apply {
        val initialControl = getElement(Coordinate(x = 1, y = maze.yLastIndex() - 1))
        initialControl.checkFrom(direction = NORTH, points = 0, allowMultiple = false)
        initialControl.checkFrom(direction = WEST, points = 0, allowMultiple = false)
        initialControl.checkFrom(direction = EAST, points = 0, allowMultiple = false)
        initialControl.checkFrom(direction = WEST, points = 0, allowMultiple = false)
    }

    private fun createPathPriorityQueue(maze: Maze) =
        PriorityQueue<Path> { a, b ->
            when {
                (a?.points ?: 0L) < (b?.points ?: 0L) -> -1
                (a?.points ?: 0L) > (b?.points ?: 0L) -> 1
                else -> 0
            }
        }.apply {
            add(
                Path(
                    points = 0,
                    tiles = setOf(
                        (maze.getElement(Coordinate(x = 1, y = maze.yLastIndex() - 1)) as PathTile)
                    )
                )
            )
        }

    private fun Maze.plotAllPaths(bestPaths: Set<Path>) {
        val allPathTiles = flatten()
            .filterIsInstance<PathTile>()

        for (path in bestPaths) {
            reset(alreadyFlattened = allPathTiles)

            for (pathTile in path.tiles) {
                if (!pathTile.isStart && !pathTile.isEnd) {
                    this[pathTile.coordinate] = pathTile
                }
            }

            println()
            print()
        }
    }

    private fun Maze.plotAllTilesInBestPaths(pathTilesCoordinates: Set<Coordinate>) {
        reset()

        for (coordinate in pathTilesCoordinates) {
            val pathTile = this[coordinate] as? PathTile ?: continue
            if (!pathTile.isStart && !pathTile.isEnd) {
                pathTile.inBestMap = true
            }
        }

        println()
        print()
    }

    private fun Maze.reset(alreadyFlattened: List<MazeSection>? = null) {
        val allPathTiles = (alreadyFlattened ?: flatten())
            .filterIsInstance<PathTile>()

        for (pathTile in allPathTiles) {
            if (!pathTile.isStart && !pathTile.isEnd) {
                this[pathTile.coordinate] = PathTile(coordinate = pathTile.coordinate)
            }
        }
    }

    private fun Maze.print() {
        print { mazeSection ->
            val string = mazeSection.toString()

            when {
                mazeSection is PathTile && mazeSection.isStart -> "$ANSI_RED$string$ANSI_RESET"
                mazeSection is PathTile && mazeSection.isEnd -> "$ANSI_GREEN$string$ANSI_RESET"
                mazeSection is PathTile && (mazeSection.currentDirection != null || mazeSection.inBestMap) -> "${createAnsi(11)}$string$ANSI_RESET"
                mazeSection is Wall -> "$ANSI_CYAN$string$ANSI_RESET"
                else -> string
            }
        }
    }

    data class Path(
        val points: Long,
        val tiles: Set<PathTile>
    ) {
        fun addNextTile(pathTile: PathTile) = Path(
            points = points + pathTile.points,
            tiles = tiles + pathTile
        )
    }

    data class PathTileCheck(
        var pointsNorth: Long = Long.MAX_VALUE,
        var pointsWest: Long = Long.MAX_VALUE,
        var pointsEast: Long = Long.MAX_VALUE,
        var pointsSouth: Long = Long.MAX_VALUE
    ) : GridElement {

        fun checkFor(nextPath: Path, allowMultiple: Boolean): Boolean {
            return checkFrom(
                direction = nextPath.tiles.last().currentDirection ?: return false,
                points = nextPath.points,
                allowMultiple = allowMultiple
            )
        }

        fun checkFrom(direction: LinearDirection, points: Long, allowMultiple: Boolean): Boolean {
            when (direction) {
                NORTH -> if ((allowMultiple && points <= pointsNorth) || points < pointsNorth) pointsNorth = points else return false
                WEST -> if ((allowMultiple && points <= pointsWest) || points < pointsWest) pointsWest = points else return false
                SOUTH -> if ((allowMultiple && points <= pointsEast) || points < pointsEast) pointsEast = points else return false
                EAST -> if ((allowMultiple && points <= pointsSouth) || points < pointsSouth) pointsSouth = points else return false
            }
            return true
        }
    }

    sealed class MazeSection : GridElement {

        data class Wall(
            override val coordinate: Coordinate
        ) : MazeSection() {
            override fun toString(): String = "#"
        }

        data class PathTile(
            override val coordinate: Coordinate,
            val isStart: Boolean = false,
            val isEnd: Boolean = false,
            val currentDirection: LinearDirection? = null,
            val points: Long = 0,
        ) : MazeSection() {

            var inBestMap: Boolean = false

            fun findNextPathTiles(maze: Maze, route: Path): Set<PathTile> {
                currentDirection ?: return emptySet()

                val pathTiles = mutableSetOf<PathTile>()

                val sanitizedNeighbours = maze
                    .findLinearNeighbours(coordinate = coordinate)
                    .toMutableMap()
                    .apply { remove(currentDirection.opposite()) }

                val clockwiseDirection = currentDirection.rotateClockwise()
                val antiClockwiseDirection = currentDirection.rotateAntiClockwise()

                val straight = (sanitizedNeighbours[currentDirection] as? PathTile)
                    ?.copy(currentDirection = currentDirection, points = 1)
                val clockwise = (sanitizedNeighbours[clockwiseDirection] as? PathTile)
                    ?.copy(currentDirection = clockwiseDirection, points = 1001)
                val antiClockwise = (sanitizedNeighbours[antiClockwiseDirection] as? PathTile)
                    ?.copy(currentDirection = antiClockwiseDirection, points = 1001)

                listOfNotNull(straight, clockwise, antiClockwise).forEach { nextPath ->
                    if (route.tiles.none { it.coordinate == nextPath.coordinate }) {
                        pathTiles.add(nextPath)
                    }
                }

                return pathTiles
            }

            override fun toString(): String = when {
                isStart -> "S"
                isEnd -> "E"
                currentDirection != null -> currentDirection.toString()
                inBestMap -> "O"
                else -> "."
            }
        }
    }
}
