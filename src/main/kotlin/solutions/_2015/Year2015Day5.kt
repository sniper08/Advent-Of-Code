package solutions._2015

import day.Day

class Year2015Day5 : Day {

    private val notAllowed = arrayOf("ab", "cd", "pq", "xy")
    private val double = ('a'..'z').map { it.toString() + it }.toTypedArray()
    private val vowels = arrayOf('a', 'e', 'i', 'o', 'u')

    /**
     * String that were NICE, not NAUGHTY
     */
    override fun part1(input: Sequence<String>): String {
        fun String.containsAny(words: Array<String>) = words.any(this::contains)
        fun String.containsThree(vowels: Array<Char>) = vowels.sumOf { this.count(it::equals) } >= 3

        var niceStringsCounter = 0

        for (string in input) {
            if (
                !string.containsAny(notAllowed)
                && string.containsAny(double)
                && string.containsThree(vowels)
            ) {
                niceStringsCounter++
            }
        }

        return "$niceStringsCounter"
    }

    /**
     * String that were NICE, not NAUGHTY
     */
    override fun part2(input: Sequence<String>): String {
        var niceStringsCounter = 0

        for (string in input) {
            var firstRule = false // pair of letters that appears twice, no overlap
            var secondRule = false // same letter with letter in between

            for (i in 0..string.length - 3) {
                val current = string[i]
                val next = string [i + 1]
                val afterNext = string[i + 2]

                if (!secondRule) {
                    secondRule = current == afterNext
                }

                if (!firstRule && i < string.length - 3) {
                    val pair = current.toString() + next
                    firstRule = string.substring(i + 2).contains(pair)
                }

                if (firstRule && secondRule) {
                    niceStringsCounter++
                    break
                }
            }
        }

        return "$niceStringsCounter"
    }
}
