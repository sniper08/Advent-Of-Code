package solutions._2024

import day.Day
import doNothing
import parser.inputCleaner

class Year2024Day5 : Day {

    override val year: Int = 2024
    override val day: Int = 5
    override val lineJumpsInput: Int = 2

    /**
     * Find correct updates and then find the sum of their middle pages
     */
    override fun part1(input: Sequence<String>): String {
        val ruleHolder = RuleHolder(input = input.first())
        val updates = createUpdates(input = input.last())

        val correctUpdates = updates.filter { update ->
            val brokenRule = ruleHolder.findBrokenRule(update = update)

            brokenRule == null
        }.toSet()

        return "${correctUpdates.sumMiddlePages()}"
    }

    /**
     * Find incorrect updates, fix them and then find the sum of their middle pages
     */
    override fun part2(input: Sequence<String>): String {
        val ruleHolder = RuleHolder(input = input.first())
        val updates = createUpdates(input = input.last())

        val fixedUpdates = mutableSetOf<MutableList<Int>>()

        for (update in updates) {
            var brokenRule = ruleHolder.findBrokenRule(update = update)

            if (brokenRule != null) {
                fixedUpdates.add(update)

                do {
                    when (brokenRule) {
                        is BrokenRule.Before -> {
                            update.removeAt(index = brokenRule.pageIndex)
                            update.add(index = brokenRule.pageIndex - brokenRule.byPositions, element = brokenRule.page)
                        }
                        is BrokenRule.After -> {
                            update.removeAt(index = brokenRule.pageIndex)
                            update.add(index = brokenRule.pageIndex + brokenRule.byPositions, element = brokenRule.page)
                        }
                        else -> doNothing
                    }

                    brokenRule = ruleHolder.findBrokenRule(update = update)
                } while (brokenRule != null)
            }
        }

        return "${fixedUpdates.sumMiddlePages()}"
    }

    private class RuleHolder(input: String) {
        val keyMustAppearBeforeSetMapOfRules = mutableMapOf<Int, MutableSet<Int>>()
        val keyMustAppearAfterSetMapOfRules = mutableMapOf<Int, MutableSet<Int>>()

        init {
            inputCleaner(input = input)
                .forEach { rawOrderingRule ->
                    val split = rawOrderingRule.split("|")
                    val x = split[0].toInt()
                    val y = split[1].toInt()
                    keyMustAppearBeforeSetMapOfRules[x] = keyMustAppearBeforeSetMapOfRules.getOrDefault(x, mutableSetOf())
                        .also { it.add(y) }
                    keyMustAppearAfterSetMapOfRules[y] = keyMustAppearAfterSetMapOfRules.getOrDefault(y, mutableSetOf())
                        .also { it.add(x) }
                }
        }

        fun findBrokenRule(update: List<Int>): BrokenRule? {
            for ((i, page) in update.withIndex()) {
                val mustAppearBeforePageSet = keyMustAppearAfterSetMapOfRules.getOrDefault(page, mutableSetOf())
                val updateAfterPage = update.drop(i + 1)

                val appearsAfterAndBreaksRule = updateAfterPage.filter { pageAfter ->
                    mustAppearBeforePageSet.contains(pageAfter)
                }

                if (appearsAfterAndBreaksRule.isNotEmpty()) {
                  //  println("Breaks after rule because $page appears before $appearsAfterAndBreaksRule")
                    return BrokenRule.After(page = page, pageIndex = i, byPositions = appearsAfterAndBreaksRule.size)
                }

                if (i > 0) {
                    val mustAppearAfterPageSet = keyMustAppearBeforeSetMapOfRules.getOrDefault(page, mutableSetOf())
                    val updateBeforePage = update.take(i)

                    val appearsBeforeAndBreaksRule = updateBeforePage.filter { pageBefore ->
                        mustAppearAfterPageSet.contains(pageBefore)
                    }

                    if (appearsBeforeAndBreaksRule.isNotEmpty()) {
                      //  println("Breaks before rule because $page appears after $appearsBeforeAndBreaksRule")
                        return BrokenRule.Before(page = page, pageIndex = i, byPositions = appearsBeforeAndBreaksRule.size)
                    }
                }
            }

            return null
        }
    }

    private sealed class BrokenRule {
        abstract val page: Int
        abstract val pageIndex: Int
        abstract val byPositions: Int

        data class Before(
            override val page: Int,
            override val pageIndex: Int,
            override val byPositions: Int
        ) : BrokenRule()

        data class After(
            override val page: Int,
            override val pageIndex: Int,
            override val byPositions: Int
        ) : BrokenRule()
    }

    private fun createUpdates(input: String) = inputCleaner(input = input)
        .map { rawUpdate ->
            rawUpdate
                .split(",")
                .map { it.toInt() }
                .toMutableList()
        }.toSet()

    private fun Set<List<Int>>.sumMiddlePages() =
        sumOf { correctUpdate ->
            correctUpdate.elementAt((correctUpdate.size - 1) / 2)
        }
}
