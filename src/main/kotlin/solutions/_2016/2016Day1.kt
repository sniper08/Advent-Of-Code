package solutions._2016

import kotlin.math.abs

const val R = "R"
const val L = "L"

data class AdaptiveCoordinate(var x: Int = 0, var y: Int = 0) {
    fun getDistanceTo0() = abs(x) + abs(y)

    override fun toString(): String = "$x,$y"
}

enum class Direction { N, E, S, W }

object Oscillator {

    var currentDirection: Direction = Direction.N

    private fun turnRight() {
        currentDirection = when(currentDirection) {
            Direction.N -> Direction.E
            Direction.E -> Direction.S
            Direction.S -> Direction.W
            Direction.W -> Direction.N
        }
    }

    private fun turnLeft() {
        currentDirection = when(currentDirection) {
            Direction.N -> Direction.W
            Direction.W -> Direction.S
            Direction.S -> Direction.E
            Direction.E -> Direction.N
        }
    }

    private fun moveForward(steps: Int, coordinate: AdaptiveCoordinate) {
        when (currentDirection) {
            Direction.N -> coordinate.y += steps
            Direction.E -> coordinate.x += steps
            Direction.S -> coordinate.y -= steps
            Direction.W -> coordinate.x -= steps
        }
    }

    fun processInstruction(instruction: String, current: AdaptiveCoordinate): Int {
        val turn = instruction.first().toString()
        val steps = instruction.substring(1).toInt()

        when (turn){
            R -> turnRight()
            L -> turnLeft()
        }

        moveForward(steps, current)
        return steps
    }

    fun processInstructionWithTracking(
        instruction: String,
        current: AdaptiveCoordinate,
        tracker: MutableMap<String, Int>
    ): AdaptiveCoordinate? {
        val tracking = AdaptiveCoordinate(current.x, current.y)
        val steps = processInstruction(instruction, current)

        return if (trackMoveTillTwice(steps, tracking, tracker)) {
            tracking
        } else {
            null
        }
    }

    private fun trackMoveTillTwice(
        steps: Int,
        tracking: AdaptiveCoordinate,
        tracker: MutableMap<String, Int>
    ): Boolean {
        repeat(steps) {
            when (currentDirection) {
                Direction.N -> tracking.y++
                Direction.E -> tracking.x++
                Direction.S -> tracking.y--
                Direction.W -> tracking.x--
            }

            val key = tracking.toString()
            tracker[key] = tracker.getValue(key) + 1
            if (tracker[key] == 2) return true // dont track any more if one coordinate visited twice
        }
        return false
    }
}

fun calculateBlockToEasterBunnyHQ(input: String) {
    val start = AdaptiveCoordinate()

    input.replace(" ", "")
        .split(",")
        .forEach { Oscillator.processInstruction(it, start) }

    println(start)
    println("Blocks to Easter Bunny Headquarters -> ${start.getDistanceTo0()}")
}

fun calculateBlockToEasterBunnyHQVisitedTwice(input: String) {
    val instructions = input.replace(" ", "").split(",")
    val start = AdaptiveCoordinate()

    val visited = mutableMapOf<String, Int>().withDefault { 0 }
    var found : AdaptiveCoordinate? = null

    var index = 0
    while (index in instructions.indices) {
        found = Oscillator.processInstructionWithTracking(instructions[index], start, visited)
        if (found != null) break

        index++
    }

    println(found)
    println("Blocks to Easter Bunny Headquarters -> ${found?.getDistanceTo0()}")
}