package solutions._2024

import ANSI_CYAN
import ANSI_GREEN
import ANSI_RED
import ANSI_RESET
import LongCoordinate
import day.Day
import parser.inputCleaner

class Year2024Day13 : Day {

    override val year: Int = 2024
    override val day: Int = 13

    override val lineJumpsInput: Int = 2

    /**
     * Find total cost by multiplying A presses * 3 and B presses * 1
     * It is an equation system with two variables
     * Some equations have no solution
     */
    override fun part1(input: Sequence<String>): String {
        val games = createArcadeGame(input = input)

        return "${games.sum(printing = false)}"
    }

    /**
     * Find total cost by multiplying A presses * 3 and B presses * 1 after applying a conversion fix
     * It is an equation system with two variables
     * Some equations have no solution
     */
    override fun part2(input: Sequence<String>): String {
        val games = createArcadeGame(input = input, unitConversionFix = 10000000000000)

        return "${games.sum(printing = true)}"
    }

    private fun Sequence<ArcadeGame>.sum(printing: Boolean) = sumOf { game ->
        if (printing) {
            println()
            println(game)
        }

        val prize = game.prize(printing = printing)
        if (printing) {
            println(prize)
        }

        prize.calculateTokensUsed()
    }

    private fun createArcadeGame(
        input: Sequence<String>,
        unitConversionFix: Long = 0L
    ) = input
        .map { rawGame ->
            val splitGameLines = inputCleaner(input = rawGame)
            val pressSeparators = arrayOf("+", ",")

            val splitPressA = splitGameLines.elementAt(0).split(delimiters = pressSeparators)
            val splitPressB = splitGameLines.elementAt(1).split(delimiters = pressSeparators)
            val splitPrize = splitGameLines.elementAt(2).split(delimiters = arrayOf("=", ","))

            ArcadeGame(
                pressA = LongCoordinate(x = splitPressA[1].toLong(), y = splitPressA[3].toLong()),
                pressB = LongCoordinate(x = splitPressB[1].toLong(), y = splitPressB[3].toLong()),
                prize = LongCoordinate(
                    x = splitPrize[1].toLong() + unitConversionFix,
                    y = splitPrize[3].toLong() + unitConversionFix
                )
            )
        }

    data class ArcadeGame(
        val pressA: LongCoordinate,
        val pressB: LongCoordinate,
        val prize: LongCoordinate
    ) {
        private val x1 = pressA.x
        private val x2 = pressB.x
        private val p1 = prize.x
        private val y1 = pressA.y
        private val y2 = pressB.y
        private val p2 = prize.y

        fun prize(printing: Boolean): Prize {
            val divATop = (p2 * x2) - (y2 * p1)
            val divABottom = (x2 * y1) - (y2 * x1)

            if (divATop == 0L && divABottom == 0L) {
                if(printing) println("${ANSI_CYAN}Infinite solutions$ANSI_RESET")
                return Prize.empty
            }

            val a = divATop / divABottom
            val b = (p1 - (x1 * a)) / x2

            val trueOnX = (x1 * a) + (x2 * b) == p1
            val trueOnY = (y1 * a) + (y2 * b) == p2

            return if (trueOnX && trueOnY) {
                if(printing) println("${ANSI_GREEN}One Solution$ANSI_RESET")
                Prize(pressA = a, pressB = b)
            } else {
                if(printing) println("${ANSI_RED}No solution$ANSI_RESET")
                Prize.empty
            }
        }

        override fun toString(): String = """
            Button A: ${pressA.toStringReversed()}
            Button B: ${pressB.toStringReversed()}
            Prize: ${prize.toStringReversed()}
        """.trimIndent()
    }

    data class Prize(
        val pressA: Long,
        val pressB: Long,
    ) {
        companion object {
            val empty = Prize(pressA = 0, pressB = 0)
        }

        fun calculateTokensUsed() = (3 * pressA) + pressB

        override fun toString(): String = "(3 * $pressA) + (1 * $pressB) = ${calculateTokensUsed()}"
    }
}