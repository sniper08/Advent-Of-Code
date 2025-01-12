package solutions._2024

import day.Day

class Year2024Day11 : Day {

    /**
     * Number of stones after 25 blinks
     */
    override fun part1(input: Sequence<String>): String {
        val stones = createInitialStonesArrangement(input)

        return "${findNumberOfStonesAfter(numberOfBlinks = 25, initialArrangement = stones)}"
    }

    /**
     * Number of stones after 75 blinks
     */
    override fun part2(input: Sequence<String>): String {
        val stones = createInitialStonesArrangement(input)

        return "${findNumberOfStonesAfter(numberOfBlinks = 75, initialArrangement = stones)}"
    }

    private fun createInitialStonesArrangement(input: Sequence<String>) = input
        .first()
        .split(" ")
        .map { rawEngrave ->
            FloatingStone(engrave = rawEngrave)
        }
        .groupBy { it }
        .entries
        .associate { it.key to it.value.size.toLong() }

    private fun findNumberOfStonesAfter(numberOfBlinks: Int, initialArrangement: Map<FloatingStone, Long>): Long {
        val stonesAfterBlinkPerStone = mutableMapOf<FloatingStone, List<FloatingStone>>()

        val stonesPresentAfterBlink = initialArrangement.toMutableMap()

        repeat(numberOfBlinks) {
            val currentStonesPresent = stonesPresentAfterBlink.toMap()
            stonesPresentAfterBlink.clear()

            for (stone in currentStonesPresent) {
                val stonesAfterBlink = stonesAfterBlinkPerStone.getOrPut(
                    key = stone.key,
                    defaultValue = { stone.key.getStonesAfterBlink() }
                )
                val timesPresent = stone.value

                for (stoneAfterBlink in stonesAfterBlink) {
                    val alreadyPresent = stonesPresentAfterBlink[stoneAfterBlink] ?: 0L
                    stonesPresentAfterBlink[stoneAfterBlink] = alreadyPresent + timesPresent
                }
            }
        }

        return stonesPresentAfterBlink.values.sum()
    }

    data class FloatingStone(val engrave: String) {

        fun getStonesAfterBlink(): List<FloatingStone> {
            val engraveNumber = engrave.toLong()
            val engraveHasEvenNumberOfDigits = engrave.length % 2 == 0

            return when {
                engraveNumber == 0L -> listOf(FloatingStone(engrave = "1"))
                engraveHasEvenNumberOfDigits -> {
                    val size = engrave.length / 2
                    listOf(
                        FloatingStone(engrave = engrave.take(size)),
                        FloatingStone(engrave = engrave.takeLast(size).toLong().toString())
                    )
                }
                else -> {
                    listOf(FloatingStone(engrave = engraveNumber.times(2024).toString()))
                }
            }
        }

        override fun toString(): String = engrave
    }
}
