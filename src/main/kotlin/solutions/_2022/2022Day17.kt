package solutions._2022

import solutions._2022.CaveSpace.EMPTY
import solutions._2022.CaveSpace.ROCK
import kotlin.math.abs
import kotlin.math.pow

typealias Cave = MutableList<Array<CaveSpace>>
enum class CaveSpace { EMPTY, ROCK }

const val LEFT_EDGE = 0
const val RIGHT_EDGE = 6

data class RockEdgeCoordinate(var x: Int = 0, var y: Int =0)

abstract class Rock {
    val top = RockEdgeCoordinate()
    val left = RockEdgeCoordinate(x = 2)
    val right = RockEdgeCoordinate()
    val bottom = RockEdgeCoordinate()

    abstract val height: Int

    protected fun onLeft() { left.x-- ; right.x-- ; top.x-- ; bottom.x-- }
    protected fun onRight() { right.x++ ; left.x++ ; top.x++ ; bottom.x++ }
    protected fun onDown() { left.y++ ; right.y++ ; top.y++ ; bottom.y++ }

    abstract fun initiate(latestTop: Int)
    abstract fun moveLeft(cave: Cave)
    abstract fun moveRight(cave: Cave)
    abstract fun moveDown(cave: Cave): Boolean
    abstract fun land(cave: Cave)
}

class FlatHorizontal : Rock() {

    override val height: Int = 1

    override fun initiate(latestTop: Int) {
        left.y = latestTop - 4
        right.x = left.x + 3 ; right.y = left.y
        top.x = left.x ; top.y = left.y
        bottom.x = left.x ; bottom.y = left.y
    }

    override fun moveLeft(cave: Cave) {
        val nextLeftPosition = left.x - 1
        if (nextLeftPosition >= LEFT_EDGE && cave[left.y][nextLeftPosition] == EMPTY) {
            onLeft()
        }
    }

    override fun moveRight(cave: Cave) {
        val nextRightPosition = right.x + 1
        if (nextRightPosition <= RIGHT_EDGE && cave[right.y][nextRightPosition] == EMPTY) {
            onRight()
        }
    }

    override fun moveDown(cave: Cave): Boolean {
        val nextBottomPosition = bottom.y + 1
        return if (nextBottomPosition < cave.lastIndex
            && cave[nextBottomPosition].sliceArray(left.x..right.x).all { it == EMPTY }) {
            onDown()
            true
        } else {
            false
        }
    }

    override fun land(cave: Cave) {
        for (i in left.x..right.x) {
            cave[top.y][i] = ROCK
        }
    }
}

class Cross : Rock() {

    override val height: Int = 3

    override fun initiate(latestTop: Int) {
        left.y = latestTop - 5
        right.x = left.x + 2 ; right.y = left.y
        top.x = left.x + 1 ; top.y = left.y - 1
        bottom.x = top.x ; bottom.y = left.y + 1
    }

    override fun moveLeft(cave: Cave) {
        val nextLeftPosition = left.x - 1
        if (
            nextLeftPosition >= LEFT_EDGE
            && cave[left.y][nextLeftPosition] == EMPTY
            && cave[top.y][top.x - 1] == EMPTY
            && cave[bottom.y][bottom.x - 1] == EMPTY
        ) {
            onLeft()
        }
    }

    override fun moveRight(cave: Cave) {
        val nextRightPosition = right.x + 1
        if (
            nextRightPosition <= RIGHT_EDGE
            && cave[right.y][nextRightPosition] == EMPTY
            && cave[top.y][top.x + 1] == EMPTY
            && cave[bottom.y][bottom.x + 1] == EMPTY
        ) {
            onRight()
        }
    }

    override fun moveDown(cave: Cave): Boolean {
        val nextBottomPosition = bottom.y + 1
        return if (
            nextBottomPosition < cave.lastIndex
            && cave[nextBottomPosition][bottom.x] == EMPTY
            && cave[left.y + 1][left.x] == EMPTY
            && cave[right.y + 1][right.x] == EMPTY
        ) {
            onDown()
            true
        } else {
            false
        }
    }

    override fun land(cave: Cave) {
        cave[top.y][top.x] = ROCK
        for (i in left.x..right.x) {
            cave[left.y][i] = ROCK
        }
        cave[bottom.y][bottom.x] = ROCK
    }
}

class InvertedL : Rock() {

    override val height: Int = 3

    override fun initiate(latestTop: Int) {
        left.y = latestTop - 4
        right.x = left.x + 2 ; right.y = left.y
        top.x = left.x + 2 ; top.y = left.y - 2
        bottom.x = top.x ; bottom.y = left.y
    }

    override fun moveLeft(cave: Cave) {
        val nextLeftPosition = left.x - 1
        if (
            nextLeftPosition >= LEFT_EDGE
            && cave[left.y][nextLeftPosition] == EMPTY
            && cave[top.y][top.x - 1] == EMPTY
            && cave[top.y + 1][top.x - 1] == EMPTY
        ) {
            onLeft()
        }
    }

    override fun moveRight(cave: Cave) {
        val nextRightPosition = right.x + 1
        if (
            nextRightPosition <= RIGHT_EDGE
            && cave[right.y][nextRightPosition] == EMPTY
            && cave[right.y - 1][nextRightPosition] == EMPTY
            && cave[right.y - 2][nextRightPosition] == EMPTY
        ) {
            onRight()
        }
    }

    override fun moveDown(cave: Cave): Boolean {
        val nextBottomPosition = bottom.y + 1
        return if (
            nextBottomPosition < cave.lastIndex
            && cave[nextBottomPosition].sliceArray(left.x..right.x).all { it == EMPTY }
        ) {
            onDown()
            true
        } else {
            false
        }
    }

    override fun land(cave: Cave) {
        cave[top.y][top.x] = ROCK
        cave[top.y + 1][top.x] = ROCK
        for (i in left.x..right.x) {
            cave[bottom.y][i] = ROCK
        }
    }
}

class FlatVertical : Rock() {

    override val height: Int = 4

    override fun initiate(latestTop: Int) {
        left.y = latestTop - 4
        right.x = left.x ; right.y = left.y
        top.x = left.x ; top.y = left.y - 3
        bottom.x = top.x ; bottom.y = left.y
    }

    override fun moveLeft(cave: Cave) {
        val nextLeftPosition = left.x - 1
        if (
            nextLeftPosition >= LEFT_EDGE
            && cave[left.y][nextLeftPosition] == EMPTY
            && cave[left.y - 1][nextLeftPosition] == EMPTY
            && cave[left.y - 2][nextLeftPosition] == EMPTY
            && cave[top.y][nextLeftPosition] == EMPTY
        ) {
            onLeft()
        }
    }

    override fun moveRight(cave: Cave) {
        val nextRightPosition = right.x + 1
        if (
            nextRightPosition <= RIGHT_EDGE
            && cave[right.y][nextRightPosition] == EMPTY
            && cave[right.y - 1][nextRightPosition] == EMPTY
            && cave[right.y - 2][nextRightPosition] == EMPTY
            && cave[top.y][nextRightPosition] == EMPTY
        ) {
            onRight()
        }
    }

    override fun moveDown(cave: Cave): Boolean {
        val nextBottomPosition = bottom.y + 1
        return if (nextBottomPosition < cave.lastIndex && cave[nextBottomPosition][bottom.x] == EMPTY) {
            onDown()
            true
        } else {
            false
        }

    }

    override fun land(cave: Cave) {
        for (i in top.y..bottom.y) {
            cave[i][left.x] = ROCK
        }
    }
}

class Square : Rock() {

    override val height: Int = 2

    override fun initiate(latestTop: Int) {
        left.y = latestTop - 4
        right.x = left.x + 1 ; right.y = left.y
        top.x = left.x ; top.y = left.y - 1
        bottom.x = top.x ; bottom.y = left.y
    }

    override fun moveLeft(cave: Cave) {
        val nextLeftPosition = left.x - 1
        if (
            nextLeftPosition >= LEFT_EDGE
            && cave[left.y][nextLeftPosition] == EMPTY
            && cave[top.y][nextLeftPosition] == EMPTY
        ) {
            onLeft()
        }
    }

    override fun moveRight(cave: Cave) {
        val nextBottomPosition = right.x + 1
        if (
            nextBottomPosition <= RIGHT_EDGE
            && cave[right.y][nextBottomPosition] == EMPTY
            && cave[top.y][nextBottomPosition] == EMPTY
        ) {
            onRight()
        }
    }

    override fun moveDown(cave: Cave): Boolean {
        val nextBottomPosition = bottom.y + 1
        return if (
            nextBottomPosition < cave.lastIndex
            && cave[nextBottomPosition].sliceArray(left.x..right.x).all { it == EMPTY }
        ) {
            onDown()
            true
        } else {
            false
        }
    }

    override fun land(cave: Cave) {
        cave[top.y][left.x] = ROCK
        cave[top.y][right.x] = ROCK
        cave[bottom.y][left.x] = ROCK
        cave[bottom.y][right.x] = ROCK
    }
}

fun findHighestRockAfter2022(input: String) {
    val cave = MutableList(5) { y -> Array(7) { if (y == 4) ROCK else EMPTY } }
    val rockCreator = listOf<() -> Rock>(
        { FlatHorizontal() }, { Cross() }, { InvertedL() }, { FlatVertical() }, { Square() }
    )
    val added = mutableListOf<Int>()

    var towerHeight: Long = 0
    var previousTop = cave.lastIndex
    var latestTop = cave.lastIndex

    val jetFlowMod = input.length
    var rockCreatorIndex = 0 // mod 5
    var jetFlowIndex = 0 // mod jetFlowMod

    for (i in 0 until 4000) {
        val rock = rockCreator[rockCreatorIndex]()
        val needed = rock.height + 3
        val diff = latestTop - needed

        if (diff < 0) {
            repeat(abs(diff)) { cave.add(0, Array(7) { EMPTY }) }
            latestTop += abs(diff)
        }

        previousTop = latestTop
        rock.initiate(latestTop)

        var movedDown = true

        while (movedDown) {
            when (input[jetFlowIndex]) {
               '<' -> rock.moveLeft(cave)
               '>' -> rock.moveRight(cave)
            }
            movedDown = rock.moveDown(cave)
            jetFlowIndex = (jetFlowIndex + 1) % jetFlowMod
        }
        rock.land(cave)

        latestTop = cave.indexOfFirst { row -> row.any { it == ROCK } }

        added.add(previousTop - latestTop)
        if (latestTop < previousTop) {
            towerHeight += previousTop - latestTop
        }

        // Prune list after last new floor like tetris
        val newFloorIndex = cave.indexOfFirst { row -> row.all { it == ROCK } }
        if (newFloorIndex < cave.lastIndex) {
            repeat(cave.lastIndex - newFloorIndex) { cave.removeLast() }
        }

        rockCreatorIndex = (rockCreatorIndex + 1) % 5
    }

    val pattern = findPatternLengthAndAfter(added)
    println(pattern)
    val first = added.take(pattern.position).sum().toLong()
    val repeated = added.subList(pattern.position, pattern.position + pattern.length)
    val repeatedValue = repeated.sum().toLong()

    for (elevation in 4..12) {
        val num = 10.0.pow(elevation).toLong()
        val amountOfRanges: Long = (num - pattern.position) / pattern.length
        val rem = ((num - pattern.position) % pattern.length).toInt()
        val total = first + (amountOfRanges * repeatedValue) + repeated.take(rem).sum().toLong()

        println("Total height on 10 pow $elevation = $total")
    }
}

data class Pattern(val length: Int, val position: Int)

fun findPatternLengthAndAfter(added: List<Int>): Pattern {
    var found: Pattern? = null
    var patternLength = 1700

    while (found == null) {
        var after = 300

        while (found == null && after + (patternLength * 2) < added.size) {
            val sampleA = added.subList(after, after + patternLength)
            val sampleB = added.subList(after + patternLength, after + (patternLength * 2))

            if (sampleA == sampleB) {
                found = Pattern(patternLength, after)
            }
            after += 5
        }
        patternLength += 5
    }

    return found
}

