package solutions._2021

import solutions._2021.Direction.*

data class Instruction(
    val direction: Direction,
    val amount: Int
)

enum class Direction(val value: String) {
    FORWARD("forward"),
    UP("up"),
    DOWN("down");

    companion object {
        fun fromString(value: String) = when(value) {
            FORWARD.value -> FORWARD
            UP.value -> UP
            DOWN.value -> DOWN
            else -> null
        }
    }
}

fun calculateFinalDepth(instructions: Sequence<String>) : Int {
    var horizontal = 0
    var depth = 0

    instructions
        .map { it.split(" ") }
        .mapNotNull { instructions ->
            Direction.fromString(instructions[0])?.let { Instruction(it, instructions[1].toInt()) }
        }.forEach {
            when (it.direction) {
                FORWARD -> { horizontal += it.amount }
                UP -> { depth -= it.amount }
                DOWN -> { depth += it.amount }
            }
        }

    return horizontal * depth
}

fun calculateFinalDepthCorrected(instructions: Sequence<String>) : Int {
    var horizontal = 0
    var depth = 0
    var aim = 0

    instructions
        .map { it.split(" ") }
        .mapNotNull { instructions ->
            Direction.fromString(instructions[0])?.let { Instruction(it, instructions[1].toInt()) }
        }.forEach {
            when (it.direction) {
                FORWARD -> {
                    horizontal += it.amount
                    depth += aim * it.amount
                }
                UP -> { aim -= it.amount }
                DOWN -> { aim += it.amount }
            }
        }

    return horizontal * depth
}
