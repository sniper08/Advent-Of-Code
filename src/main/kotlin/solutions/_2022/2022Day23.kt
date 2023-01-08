package solutions._2022

import solutions._2022.ElfMove.*

enum class ElfMove { N, S, W, E }

typealias Grove = MutableList<MutableList<Elf?>>
typealias ProposedMoves = MutableMap<ElfCoordinate, Int>

data class ElfCoordinate(var y: Int, var x: Int)
data class Elf(var current: ElfCoordinate, var proposed: ElfCoordinate? = null) {

    fun proposeMove(grove: Grove, proposedMoves: ProposedMoves, elfMoves: List<ElfMove>) {
        val subGrid = listOf(
            // top
            grove.getOrNull(current.y - 1)?.getOrNull(current.x - 1),
            grove.getOrNull(current.y - 1)?.getOrNull(current.x),
            grove.getOrNull(current.y - 1)?.getOrNull(current.x + 1),
            // middle
            grove.getOrNull(current.y)?.getOrNull(current.x - 1),
            grove.getOrNull(current.y)?.getOrNull(current.x + 1),
            // bottom
            grove.getOrNull(current.y + 1)?.getOrNull(current.x - 1),
            grove.getOrNull(current.y + 1)?.getOrNull(current.x),
            grove.getOrNull(current.y + 1)?.getOrNull(current.x + 1)
        )

        if (subGrid.all { it == null }) {
            proposed = null
        } else {
            for (elfMove in elfMoves) {
                val nextProposed = when (elfMove) {
                    N -> if (subGrid.subList(0, 3).all { it == null }) ElfCoordinate(y = current.y - 1, x = current.x) else null
                    S -> if (subGrid.subList(5, subGrid.size).all { it == null }) ElfCoordinate(y = current.y + 1, x = current.x) else null
                    W -> if (listOf(subGrid[0], subGrid[3], subGrid[5]).all { it == null }) ElfCoordinate(y = current.y, x = current.x - 1) else null
                    E -> if (listOf(subGrid[2], subGrid[4], subGrid[7]).all { it == null }) ElfCoordinate(y = current.y, x = current.x + 1) else null
                }
                if (nextProposed != null) {
                    proposed = nextProposed
                    proposedMoves[nextProposed] = proposedMoves.getOrDefault(nextProposed, 0) + 1
                    break
                }
            }
        }
    }

    fun move(grove: Grove, proposedMoves: ProposedMoves) {
        val currentProposed = proposed ?: return

        if (proposedMoves.getOrDefault(currentProposed, 0) == 1) {
            grove[currentProposed.y][currentProposed.x] = this
            grove[current.y][current.x] = null
            current.y = currentProposed.y; current.x = currentProposed.x
            proposed = null
        }
    }
}

fun findEmptyGroundInGrove(input: Sequence<String>) {
    val grove = input.createGrove(10)

    val elfMoves = mutableListOf<ElfMove>()
    val proposedMoves = mutableMapOf<ElfCoordinate, Int>()

    repeat(2) {
        grove.executeRound(elfMoves, proposedMoves)
    }

    val allElfs = grove.flatten().filterNotNull()
    val minY = allElfs.minOf { it.current.y }
    val maxY = allElfs.maxOf { it.current.y }
    val minX = allElfs.minOf { it.current.x }
    val maxX = allElfs.maxOf { it.current.x }

    val emptyGround = grove.slice(minY..maxY)
        .sumOf { row -> row.slice(minX..maxX).count { it == null } }

    println("\nEmpty ground = $emptyGround")
}

fun findRoundsToNoMovesInGrove(input: Sequence<String>) {
    val grove = input.createGrove(60)

    val elfMoves = mutableListOf<ElfMove>()
    val proposedMoves = mutableMapOf<ElfCoordinate, Int>()
    var rounds = 0

    do {
        grove.executeRound(elfMoves, proposedMoves)
        rounds++
    } while (proposedMoves.isNotEmpty())

    println("Rounds till no moves = $rounds")
}

fun Sequence<String>.createGrove(added: Int): Grove {
    val uglyList = toMutableList().apply {
        repeat(added) {
            val string = List(first().length) { "." }.joinToString("") { it }
            //add(0, string)
        }
    }.map {
        MutableList(added) { '.' }.apply { addAll(it.toList()) }
    }

    return MutableList(uglyList.size) { y ->
        MutableList(uglyList.first().size) { x ->
            if (uglyList.elementAt(y)[x] == '.') null else Elf(current = ElfCoordinate(y = y, x = x))
        }
    }
}

fun Grove.executeRound(elfMoves: MutableList<ElfMove>, proposedMoves: ProposedMoves) {
    if (elfMoves.isEmpty()) {
        elfMoves.addAll(ElfMove.values())
    } else {
        elfMoves.add(elfMoves.first())
        elfMoves.removeAt(0)
    }
    proposedMoves.clear()

    val allElfs = flatten().filterNotNull()

    if (allElfs.any { it.current.y == lastIndex }) {
        add(MutableList(first().size) { null })
    }

    if (allElfs.any { it.current.x == first().lastIndex }) {
        forEach { it.add(null) }
    }

    if (allElfs.any { it.current.y == 0} ) {
        add(0, MutableList(first().size) { null })
        allElfs.forEach { it.current.y++ }
    }

    allElfs.forEach { it.proposeMove(this, proposedMoves, elfMoves) }
    allElfs.forEach { it.move(this, proposedMoves) }
}