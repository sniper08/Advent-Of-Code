package solutions._2022

import parser.inputCleaner
import kotlin.math.abs

data class MoveInstruction(val move: Int, val from: Int, val to: Int)

fun findTopCrateInSupplyStack(input: Sequence<String>) {
    val crates = parseCrates(inputCleaner(input.first()))
    crates.printCrates()

    parseInstructions(inputCleaner(input.last())).forEach {
        println(it.toString())
        executeMove(crates, it, new = true)
        crates.printCrates()
    }

    println(crates.map { it.first { char -> char != ' ' } }.joinToString("") { "$it" })
}

fun executeMove(allCrates: List<MutableList<Char>>, moveInstruction: MoveInstruction, new: Boolean) {
    val from = moveInstruction.from - 1
    val to = moveInstruction.to - 1

    allCrates[from]
        .filter { it != ' ' }
        .take(moveInstruction.move)
        .let { if (new) it.reversed() else it }
        .forEach { toMove ->
            val indexFrom = allCrates[from].indexOfFirst { it == toMove }
            allCrates[from][indexFrom] = ' '

            val indexTo = allCrates[to].indexOfLast { it == ' ' }
            if (indexTo == -1) {
                allCrates[to].add(0, toMove)
            } else {
                allCrates[to][indexTo] = toMove
            }
        }

    allCrates.sanitize()
}

fun List<MutableList<Char>>.sanitize() {
    val maxSize = maxOf { column ->
        column.count { it != ' ' }
    }

    forEach { crate ->
        val diff = maxSize - crate.size

        when {
            diff > 0 -> { crate.addAll(0, List(diff) { ' ' }) }
            diff < 0 -> { repeat(abs(diff)) { crate.removeAt(0) } }
        }
    }
}


fun parseCrates(cratesRaw: Sequence<String>): List<MutableList<Char>> {
    val size = cratesRaw.last().trim().split("   ").last().toInt()
    val cratesReversed = cratesRaw.toList().dropLast(1)
    val allCrates = List(size) { mutableListOf<Char>() }

    for (row in cratesReversed) {
        var i = 0

        while (i < size) {
            allCrates[i].add(row.getOrElse(i * 4 + 1) { ' ' })
            i++
        }
    }

    return allCrates
}

fun parseInstructions(instructionsRaw: Sequence<String>) = instructionsRaw
    .map { raw ->
        raw.split(" ").let {
            MoveInstruction(
                move = it[1].toInt(),
                from = it[3].toInt(),
                to = it[5].toInt()
            )
        }
    }

fun List<MutableList<Char>>.printCrates() {
    repeat(first().size) { i ->
        println(
            map { it[i] }
                .joinToString(" ") { char ->
                    if (char == ' ') "   " else "[$char]"
                }
        )
    }
    println((1..size).joinToString(separator = "   ", prefix = " ") { "$it" })
    println()
}

