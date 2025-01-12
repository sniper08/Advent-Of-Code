package solutions._2024

import day.Day

class Year2024Day3 : Day {

    private val mulRegex = Regex("mul\\((\\d+),(\\d+)\\)")

    /**
     * Find sum of all multiplication in corrupted program
     */
    override fun part1(input: Sequence<String>): String {
        val rawString = input.joinToString("") { it }

        val multiplications = mulRegex
            .findMultiplications(rawMultiplications = rawString)
            .toList()

        return "${multiplications.sum()}"
    }

    override fun part2(input: Sequence<String>): String {
        val dontSplitter = "don't()"
        val doSplitter = "do()"

        val dontSplitterLength = dontSplitter.length
        val doSplitterLength = doSplitter.length

        var remainingRawString = input.joinToString("") { it }
        val multiplications = mutableListOf<String>()

        while (remainingRawString.isNotEmpty()) {
            val dontSplitIndex = remainingRawString.indexOf(dontSplitter)

            if (dontSplitIndex > -1) {
                multiplications.addAll(
                    // Only use the mul values found before the split, they are enabled
                    mulRegex.findMultiplications(rawMultiplications = remainingRawString.substring(startIndex = 0, endIndex = dontSplitIndex))
                )

                val doSplitIndex = remainingRawString
                    // Only use the string after the split, it might become enabled
                    .substring(startIndex = dontSplitIndex + dontSplitterLength)
                    .indexOf(doSplitter)

                remainingRawString = if (doSplitIndex > -1) {
                    // Only use the string after the split, it is enabled
                    remainingRawString.substring(startIndex = dontSplitIndex + doSplitIndex + doSplitterLength)
                } else {
                    ""
                }
            } else {
                multiplications.addAll(
                    mulRegex.findMultiplications(rawMultiplications = remainingRawString)
                )
                remainingRawString = ""
            }
        }

        return "${multiplications.sum()}"
    }

    private fun List<String>.sum() = sumOf { rawMultiplication ->
        println(rawMultiplication)
        val split = rawMultiplication.split("(", ",", ")")

        split[1].toLong() * split[2].toLong()
    }

    private fun Regex.findMultiplications(rawMultiplications: String) =
        findAll(rawMultiplications)
            .map { it.value }
}
