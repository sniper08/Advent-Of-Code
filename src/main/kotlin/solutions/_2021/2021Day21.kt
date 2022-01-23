package solutions._2021

import kotlin.math.max
import kotlin.math.min

const val DETERMINISTIC_WINNING_SCORE = 1000L
const val MAX_POSITION = 10
const val ROLLS_PER_TURN = 3L
const val REAL_WINNING_SCORE = 21L

data class PracticeDirac(
    var position1: Int,
    var position2: Int,
    var score1: Int,
    var score2: Int,
    var winner: Int = 0
) {
    fun updateScore(toAdd: Int, player: Int) {
        if (player == 1) {
            position1 += toAdd
            if (position1 > MAX_POSITION) position1 -= MAX_POSITION
            score1 += position1
        } else {
            position2 += toAdd
            if (position2 > MAX_POSITION) position2 -= MAX_POSITION
            score2 += position2
        }
        winner = when {
            score1 >= DETERMINISTIC_WINNING_SCORE -> 1
            score2 >= DETERMINISTIC_WINNING_SCORE -> 2
            else -> 0
        }
    }
}

data class Dirac(
    val position1: Int,
    val position2: Int,
    val score1: Int,
    val score2: Int
)

data class WinnerCounter(val a: Long, val b: Long)

fun calculateDiracDicePracticeWinner(input: Sequence<String>) {
    val (player1Position, player2Position) = input
        .map { it.split(":")[1].trim() }
        .let { it.elementAt(0).toInt() to it.elementAt(1).toInt()  }

    val practiceDirac = PracticeDirac(player1Position, player2Position, 0, 0)

    println("Player 1 start: ${practiceDirac.position1}")
    println("Player 2 start: ${practiceDirac.position2}")

    var rolls = 0L
    var toAdd = 6
    var firstPlayer = true

    while (practiceDirac.winner == 0) {
        practiceDirac.updateScore(toAdd, if (firstPlayer) 1 else 2)
        firstPlayer = firstPlayer.not()
        rolls += ROLLS_PER_TURN
        toAdd--
        if (toAdd < 0) toAdd = 9
    }

    println("Player 1 score: ${practiceDirac.score1}")
    println("Player 2 score: ${practiceDirac.score2}")
    println("Rolls: $rolls")
    println("Score times Rolls: ${min(practiceDirac.score1, practiceDirac.score2) * rolls}")
}

fun calculateDiracDiceWinner(input: Sequence<String>) {
    val (player1Position, player2Position) = input
        .map { it.split(":")[1].trim() }
        .let { it.elementAt(0).toInt() to it.elementAt(1).toInt()  }

    val winnersMap = mutableMapOf<Dirac, WinnerCounter>()

    val winners = rollQuantumTurn(
        universes = createUniverses(),
        Dirac(position1 = player1Position, position2 = player2Position, score1 = 0, score2 =  0),
        winnersMap
    )

    println(winners)
    println("Most wins: ${max(winners.a, winners.b)}")
}

fun rollQuantumTurn(
    universes: List<Int>,
    dirac: Dirac,
    winnersMap: MutableMap<Dirac, WinnerCounter>
): WinnerCounter {
    // If already in map, return it!!
    val previous = winnersMap[dirac]
    if (previous != null) {
        return previous
    }

    var winnersA = 0L
    var winnersB = 0L

   // println(universes)

    for (move1 in universes) {
        // Move to new position and add score 1
        val nextPosition1 = dirac.position1.move(move1)
        val nextScore1 = dirac.score1 + nextPosition1
        if (nextScore1 >= REAL_WINNING_SCORE) {
            // If winner add to counter, move to next universe
            winnersA++
            continue
        } else {
            for (move2 in universes) {
                // Move to new position and add score 2
                val nextPosition2 = dirac.position2.move(move2)
                val nextScore2 = dirac.score2 + nextPosition2
                if (nextScore2 >= REAL_WINNING_SCORE) {
                    // If winner add to counter, move to next universe
                    winnersB++
                    continue
                }

                // If no winner, create new Dirac
                val newDirac = Dirac(nextPosition1, nextPosition2, nextScore1, nextScore2)

                // Roll for NEW Dirac
                val winners = rollQuantumTurn(universes, newDirac, winnersMap)
                // Add Counters for CURRENT Dirac
                winnersA += winners.a
                winnersB += winners.b
            }
        }
    }

    // Add CURRENT dirac to map
    winnersMap[dirac] = WinnerCounter(winnersA, winnersB)

    return WinnerCounter(winnersA, winnersB)
}

fun createUniverses() : List<Int> {
    val universes = mutableListOf<Int>()
    val start = 3

    repeat(3) {
       var mediumStart = start + it
       repeat(3) {
           universes.add(mediumStart)
           universes.add(mediumStart + 1)
           universes.add(mediumStart + 2)
           mediumStart++
       }
    }

    return universes
}

fun Int.move(universe: Int) = (this + universe).let {
    if (it > MAX_POSITION) it - MAX_POSITION else it
}
