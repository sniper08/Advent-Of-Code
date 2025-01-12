package day

const val NOT_IMPLEMENTED_YET = "Not implemented yet"

interface Day {

    val lineJumpsInput: Int
        get() = 1

    fun part1(input: Sequence<String>): String
    fun part2(input: Sequence<String>): String
}