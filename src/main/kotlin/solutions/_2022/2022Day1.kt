package solutions._2022

import parser.inputCleaner

fun calculateMostCaloriesElf(input: Sequence<String>) {
    val maxCaloriesElf = getAllElvesCalories(input.toList())
        .maxOfOrNull { it.sum() }

    println("Max Calories all elves $maxCaloriesElf")
}

fun calculateMostCaloriesTopThreeElves(input: Sequence<String>) {
    val maxCaloriesTopThreeElves = getAllElvesCalories(input.toList())
        .map { it.sum() }
        .sortedDescending()
        .take(3)
        .sum()

    println("Max Calories to three elves $maxCaloriesTopThreeElves")
}

private fun getAllElvesCalories(input: List<String>) = input
    .map {
        inputCleaner(it).map { calorie -> calorie.toLong() }
    }