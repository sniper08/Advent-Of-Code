package solutions._2024

import day.Day
import parser.inputCleaner
import kotlin.math.min

class Year2024Day19 : Day {

    override val lineJumpsInput: Int = 2

    /**
     * Find the sum of designs that are possible
     */
    override fun part1(input: Sequence<String>): String {
        val towelPatterns = createTowelPatterns(input = input)
        val minPatternSize = towelPatterns.minOf { it.length }
        val maxPatternSize = towelPatterns.maxOf { it.length }

        val possibleDesigns = inputCleaner(input = input.last())
            .sumOf { design ->
                val patternThatMakesUpDesign = findFirstPatternThatMakesUpDesign(
                    design = design,
                    patterns = towelPatterns,
                    minPatternSize = minPatternSize,
                    maxPatternSize = maxPatternSize
                )

              //  println("$design --> $patternThatMakeUpDesign")

                if (patternThatMakesUpDesign.isEmpty()) 0 else 1L
            }

        return "$possibleDesigns"
    }

    /**
     * Find the sum all pattern combinations for all designs that are possible
     */
    override fun part2(input: Sequence<String>): String {
        val towelPatterns = createTowelPatterns(input = input)
        val minPatternSize = towelPatterns.minOf { it.length }
        val maxPatternSize = towelPatterns.maxOf { it.length }

        val patternsCountThatMakeUpDesign = mutableMapOf<String, Long>()

        val possibleWaysToMakeUpDesign = inputCleaner(input = input.last())
            .sumOf { design ->
                val allPatternsThatMakeUpDesign = findPatternsCountThatMakeUpDesign(
                    design = design,
                    patterns = towelPatterns,
                    minPatternSize = minPatternSize,
                    maxPatternSize = maxPatternSize,
                    patternsCountThatMakeUpDesign = patternsCountThatMakeUpDesign
                )

                allPatternsThatMakeUpDesign
            }

        return "$possibleWaysToMakeUpDesign"
    }

    private fun createTowelPatterns(input: Sequence<String>) = inputCleaner(input = input.first())
        .first()
        .split(", ")
        .toSet()

    fun findFirstPatternThatMakesUpDesign(
        design: String,
        patterns: Set<String>,
        minPatternSize: Int,
        maxPatternSize: Int
    ): List<String> {
        val patternsToUse = mutableListOf<String>()
        var patternSize = minPatternSize

        while (patternSize in minPatternSize..maxPatternSize) {
            val toCheck = design.take(patternSize)

            if (patterns.contains(toCheck)) {
                val leftToCheck = design.drop(patternSize)

                if (leftToCheck.isNotBlank()) {
                    val patternsThatMakeUpLeftToCheck = findFirstPatternThatMakesUpDesign(
                        design = leftToCheck,
                        patterns = patterns,
                        minPatternSize = minPatternSize,
                        maxPatternSize
                    )

                    if (patternsThatMakeUpLeftToCheck.isEmpty()) {
                        patternSize++
                    } else {
                        patternsToUse.add(toCheck)
                        patternsToUse.addAll(patternsThatMakeUpLeftToCheck)
                        break
                    }
                } else {
                    patternsToUse.add(toCheck)
                    break
                }
            } else {
                patternSize++
            }
        }

        return patternsToUse
    }

    private fun findPatternsCountThatMakeUpDesign(
        design: String,
        patterns: Set<String>,
        minPatternSize: Int,
        maxPatternSize: Int,
        patternsCountThatMakeUpDesign: MutableMap<String, Long>
    ): Long {
        val alreadyFoundPatternsCountThatMakeUpThisDesign = patternsCountThatMakeUpDesign.getOrPut(
            key = design,
            defaultValue = { 0L }
        )

        if (alreadyFoundPatternsCountThatMakeUpThisDesign == 0L) {
            var patternsCountThatMakeUpThisDesign = 0L

            for (patternSize in minPatternSize..min(maxPatternSize, design.length)) {
                val patternToCheck = design.take(patternSize)

                if (patterns.contains(patternToCheck)) {
                    val leftToCheckDesign = design.drop(patternSize)

                    if (leftToCheckDesign.isNotBlank()) {
                        val alreadyFoundPatternsCountThatMakeUpLeftToCheckDesign = patternsCountThatMakeUpDesign.getOrPut(
                            key = leftToCheckDesign,
                            defaultValue = { 0L }
                        )

                        if (alreadyFoundPatternsCountThatMakeUpLeftToCheckDesign == 0L) {
                            val patternsCountThatMakeUpLeftToCheckDesign = findPatternsCountThatMakeUpDesign(
                                design = leftToCheckDesign,
                                patterns = patterns,
                                minPatternSize = minPatternSize,
                                maxPatternSize = maxPatternSize,
                                patternsCountThatMakeUpDesign = patternsCountThatMakeUpDesign
                            )

                            if (patternsCountThatMakeUpLeftToCheckDesign > 0) {
                                patternsCountThatMakeUpThisDesign += patternsCountThatMakeUpLeftToCheckDesign
                            }
                        } else {
                            patternsCountThatMakeUpThisDesign += alreadyFoundPatternsCountThatMakeUpLeftToCheckDesign
                        }
                    } else {
                        patternsCountThatMakeUpThisDesign++
                    }
                }
            }

            // Save Patterns Count That Make Up This Design
            patternsCountThatMakeUpDesign[design] = patternsCountThatMakeUpThisDesign

            return patternsCountThatMakeUpThisDesign
        } else {
            return alreadyFoundPatternsCountThatMakeUpThisDesign
        }
    }
}