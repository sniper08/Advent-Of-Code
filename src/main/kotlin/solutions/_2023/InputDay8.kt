package solutions._2023

import parser.inputCleaner
import java.lang.Exception

data class NodeDirections(val left: String, val right: String)

enum class Instructions { R, L }

fun calculateStepToFindExitInDesertNetwork(input: Sequence<String>) {
    val firstNode = "AAA"
    val lastNode = "ZZZ"
    val instructions = input.first()
    val nodeDirectionsList = getNodeDirectionsList(input)

    var steps = 0
    var instructionIndex = -1
    var currentNode = firstNode

    while (currentNode != lastNode) {
        steps++
        instructionIndex++
        val instruction = instructions[instructionIndex % instructions.length]
        val nodeDirections = nodeDirectionsList.getValue(currentNode)

        currentNode = nextNode(instruction, nodeDirections)
    }

    println("Steps to exit network $steps")
}

fun calculateStepToFindExitInDesertNetworkV2(input: Sequence<String>) {
    val startEnding = 'A'
    val endEnding = 'Z'
    val instructions = input.first()
    val nodeDirectionsList = getNodeDirectionsList(input)

    val foundValues = nodeDirectionsList.keys
        .filter { it.endsWith(startEnding) }
        .map { node ->
            var steps = 0L
            var instructionIndex = -1
            var currentNode = node
            var foundAt = 0L
            var lastFound = 0L

            repeat(1) {
                do {
                    steps++
                    instructionIndex++
                    val instruction = instructions[instructionIndex % instructions.length]
                    val nodeDirections = nodeDirectionsList.getValue(currentNode)

                    currentNode = nextNode(instruction, nodeDirections)
                } while (!currentNode.endsWith(endEnding))

                foundAt = steps - lastFound
                lastFound = steps
            }

            println("$node found finish after $foundAt steps")
            foundAt
        }.toSet()

    val steps = foundValues.reduce { acc, foundAt -> findLCM(acc, foundAt) }

    println("Steps to exit network $steps")
}

private fun getNodeDirectionsList(input: Sequence<String>) = inputCleaner(input.last())
    .map {
        val split = it.split(" = (", ", ", ")")
        split[0] to NodeDirections(split[1], split[2])
    }.toMap()

private fun nextNode(instruction: Char, nodeDirections: NodeDirections) =
    when (instruction.toString()) {
        Instructions.L.name.uppercase() -> nodeDirections.left
        Instructions.R.name.uppercase() -> nodeDirections.right
        else -> throw Exception("NOT FOUND")
    }

fun findLCM(a: Long, b: Long): Long {
    val larger = if (a > b) a else b
    val maxLcm = a * b
    var lcm = larger
    while (lcm <= maxLcm) {
        if (lcm % a == 0L && lcm % b == 0L) {
            return lcm
        }
        lcm += larger
    }
    return maxLcm
}