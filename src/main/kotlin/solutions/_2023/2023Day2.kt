package solutions._2023

import solutions._2023.Color.*

enum class Color { RED, BLUE, GREEN }

data class Pick(val quantity: Int, val color: Color)
data class Game(
    val id: Int,
    val turns: List<Set<Pick>>
)

fun createGame(input: String): Game =
    input.split(": ", "; ")
        .let {
            val id = it[0].split(" ")[1].toInt()
            val turns = mutableListOf<Set<Pick>>()

            for (i in 1..it.lastIndex) {
                val turn = it[i]
                val set = mutableSetOf<Pick>()

                turn.split(", ")
                    .forEach { turnSplit ->
                        val pick = turnSplit.split(" ")

                        set.add(Pick(pick[0].toInt(), Color.valueOf(pick[1].uppercase())))
                    }
                turns.add(set)
            }

            Game(id, turns)
        }

fun calculateIdSumOfCubesGame(input: Sequence<String>) {
    val allocatedMaxValues = mapOf(
        RED to 12,
        GREEN to 13,
        BLUE to 14
    )

    val sumOfIds = input.sumOf {
        createGame(it)
            .also { game -> println(game) }
            .let { game ->
                var id = game.id

                turnLoop@ for (turn in game.turns) {
                    for (pick in turn) {
                        if (pick.quantity > allocatedMaxValues.getValue(pick.color)) {
                            id = 0
                            break@turnLoop
                        }
                    }
                }
                id
            }
    }

    println("The sum of the possible games' IDs is $sumOfIds")
}

fun calculateSumOfPowersCubesGame(input: Sequence<String>) {
    val sumOfPowers = input.sumOf {
        createGame(it)
            .also { game -> println(game) }
            .let { game ->
                val allocatedMaxValues = mutableMapOf(
                    RED to 1,
                    GREEN to 1,
                    BLUE to 1
                )

                game.turns.flatten().forEach { pick ->
                    if (pick.quantity > allocatedMaxValues.getValue(pick.color)) {
                        allocatedMaxValues[pick.color] = pick.quantity
                    }
                }
                allocatedMaxValues.values.reduce { acc, i -> acc * i }
            }
    }

    println("The sum of the powers for fewest number of colors per cube in a game is $sumOfPowers")
}