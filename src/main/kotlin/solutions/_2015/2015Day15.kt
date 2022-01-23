package solutions._2015

data class Ingredient(
    val name: String,
    val capacity: Long,
    val durability: Long,
    val flavor: Long,
    val texture: Long,
    val calories: Long,
) {
    fun print() {
        println("$name capacity $capacity durability $durability flavor $flavor texture $texture calories $calories")
    }
}

data class Recipe(
    val quantities: List<Int>,
    var totalScore: Long = 0,
    var totalCalories: Long = 0
)

fun createIngredients(input: Sequence<String>) = input
    .toList()
    .map {
        val split = it.replace(",", "").split(" ")

        Ingredient(
            name = split[0],
            capacity = split[2].toLong(),
            durability = split[4].toLong(),
            flavor = split[6].toLong(),
            texture = split[8].toLong(),
            calories = split[10].toLong()
        )
    }

fun calculateBestRecipeNoCalories(input: Sequence<String>) {
    val ingredients = createIngredients(input)

    val highest = 100 - (ingredients.size - 1)
    val possible = 1..highest
    val recipes = mutableListOf<Recipe>()

    for (a in possible.reversed()) {
        val quantities = mutableListOf<Int>().apply { add(a) }.findPossibleQuantities(ingredients.size, possible)

        for (quantity in quantities) {
            val totalScore = calculateTotalScore(ingredients, quantity)

            if (totalScore > 0) {
                recipes.add(Recipe(quantity, totalScore))
            }
        }
    }

    ingredients.forEach { it.print() }

    val best = recipes.maxByOrNull { it.totalScore }
    println("The quantities score are ${best?.quantities} with a score of ${best?.totalScore}")
}

fun calculateBestRecipeWithCalories(input: Sequence<String>, calories: Long = 500) {
    val ingredients = createIngredients(input)

    val highest = 100 - (ingredients.size - 1)
    val possible = 1..highest
    val recipes = mutableListOf<Recipe>()

    for (a in possible.reversed()) {
        val quantities = mutableListOf<Int>().apply { add(a) }.findPossibleQuantities(ingredients.size, possible)

        for (quantity in quantities) {
            val totalCalories = calculateTotalCalories(ingredients, quantity)

            if (totalCalories == 500L) {
                val totalScore = calculateTotalScore(ingredients, quantity)

                if (totalScore > 0) {
                    recipes.add(Recipe(quantity, totalScore, totalCalories))
                }
            }
        }
    }

    ingredients.forEach { it.print() }
    recipes.forEach { println(it) }

    val best = recipes.maxByOrNull { it.totalScore }
    println("The quantities score are ${best?.quantities} with a score of ${best?.totalScore}")
}

fun List<Int>.findPossibleQuantities(ingredientsSize: Int, possible: IntRange) : List<List<Int>>  {
    val combinations = mutableListOf<List<Int>>()
    val currentSum = sum()

    for (next in possible) {
        val clone = toMutableList()
        val nextSum = currentSum + next

        if (ingredientsSize - size == 1) {
            if (nextSum == 100) {
                clone.add(next)
                combinations.add(clone)
                break
            }
        } else {
            if (100 - nextSum >= ingredientsSize - size - 1) {
                clone.add(next)
                combinations.addAll(clone.findPossibleQuantities(ingredientsSize, possible))
            } else {
                break
            }
        }
    }

    return combinations
}

fun calculateTotalCalories(ingredient: List<Ingredient>, quantities: List<Int>) = ingredient
    .map { it.calories }
    .calculateTotal(quantities)

fun calculateTotalScore(ingredient: List<Ingredient>, quantities: List<Int>): Long {
    val totalCapacity = ingredient.map { it.capacity }.calculateTotal(quantities)
    if (totalCapacity < 0L) {
        return 0
    }
    val totalDurability = ingredient.map { it.durability }.calculateTotal(quantities)
    if (totalDurability < 0L) {
        return 0
    }
    val totalFlavor = ingredient.map { it.flavor }.calculateTotal(quantities)
    if (totalFlavor < 0L) {
        return 0
    }
    val totalTexture = ingredient.map { it.texture }.calculateTotal(quantities)
    if (totalTexture < 0L) {
        return 0
    }
    return totalCapacity * totalDurability * totalFlavor * totalTexture
}

fun List<Long>.calculateTotal(combination: List<Int>) = foldRightIndexed(0L) { index, capacity, acc ->
    val multiply = capacity * combination[index].toLong()
    acc + multiply
}
