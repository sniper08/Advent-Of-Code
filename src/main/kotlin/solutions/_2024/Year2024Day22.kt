package solutions._2024

import day.Day
import solutions._2021.toDigits

class Year2024Day22 : Day {

    override val year: Int = 2024
    override val day: Int = 22

    /**
     * Find sum of all the 2000th computed secret numbers
     */
    override fun part1(input: Sequence<String>): String {
        val sumOfSecretNumbers = input.sumOf { rawSecretNumber ->
            var secretNumber = rawSecretNumber.toLong()

            repeat(2000) {
                secretNumber = secretNumber.nextSecretNumber()
            }

            secretNumber
        }
        return "$sumOfSecretNumbers"
    }

    /**
     * Find max number of bananas per price fluctuation sequence
     */
    override fun part2(input: Sequence<String>): String {
        val bananasTotalPerFluctuationSequence = mutableMapOf<FluctuationSequence, Long>()

        input.forEach { rawSecretNumber ->
            val prices = mutableListOf<Price>()

            var secretNumber = rawSecretNumber.toLong()
            var lastDigit = secretNumber.lastDigit()
            prices.add(Price(secretNumber = secretNumber, lastDigit = lastDigit))

            repeat(2000) {
                secretNumber = secretNumber.nextSecretNumber()
                val newPrice = Price(secretNumber = secretNumber, lastDigit = secretNumber.lastDigit())
                prices.add(newPrice.copy(diffToPrevious = newPrice.lastDigit - lastDigit))
                lastDigit = newPrice.lastDigit
            }

            val alreadyCounted = mutableSetOf<FluctuationSequence>()
            prices
                .windowed(4,1)
                .forEach { rawFluctuationSequence ->
                    val firstFluctuation = rawFluctuationSequence[0]

                    if (firstFluctuation.diffToPrevious != Int.MAX_VALUE) {
                        val lastFluctuation = rawFluctuationSequence[3]

                        val fluctuationSequence = FluctuationSequence(
                            first = firstFluctuation.diffToPrevious,
                            second = rawFluctuationSequence[1].diffToPrevious,
                            third = rawFluctuationSequence[2].diffToPrevious,
                            fourth = lastFluctuation.diffToPrevious
                        )

                        if (!alreadyCounted.contains(fluctuationSequence)) {
                            val existingTotal = bananasTotalPerFluctuationSequence[fluctuationSequence] ?: 0L
                            bananasTotalPerFluctuationSequence[fluctuationSequence] = existingTotal + lastFluctuation.lastDigit.toLong()
                            alreadyCounted.add(fluctuationSequence)
                        }
                    }
                }
        }

        val maxNumberOfBananas = bananasTotalPerFluctuationSequence.entries.maxBy { it.value }
        println(maxNumberOfBananas)

        return "${maxNumberOfBananas.value}"
    }

    data class FluctuationSequence(
        val first: Int,
        val second: Int,
        val third: Int,
        val fourth: Int
    ) {
        override fun toString(): String = "($first,$second,$third,$fourth)"
    }

    data class Price(
        val secretNumber: Long,
        val lastDigit: Int,
        val diffToPrevious: Int = Int.MAX_VALUE
    ) {
        override fun toString(): String = "$secretNumber: $lastDigit ${if (diffToPrevious == Int.MAX_VALUE) "" else "($diffToPrevious)"}"
    }

    private fun Long.mix(other: Long) = this xor other
    private fun Long.prune() = this % 16777216
    private fun Long.nextSecretNumber(): Long {
        var secretNumber = (this * 64).mix(other = this).prune()
        secretNumber = (secretNumber / 32).mix(other = secretNumber).prune()
        return (secretNumber * 2048).mix(other = secretNumber).prune()
    }

}

private fun Long.lastDigit() = toDigits().last()