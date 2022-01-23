package solutions._2015

fun calculateEggnogCombination(input: Sequence<String>) {
    val containers = input.toList().map { it.toInt() }.sorted()

    val combinations = mutableMapOf<List<Int>, Int>().withDefault { 0 }

    containers.forEachIndexed { i, next ->
        val nextRemaining = containers.subList(i + 1, containers.size)

        if (nextRemaining.isNotEmpty()) {
            listOf(next).findAllCombinations(
                limit = 150,
                remaining = nextRemaining,
                combinations = combinations
            )
        }
    }

    println(containers)
    combinations.forEach { println(it) }

    val total = combinations.values.sum()
    println("The combinations total is $total")

    val minContainerNumber = combinations.keys.minOf { it.size }
    val totalCountWithMin = combinations
        .filter { it.key.size == minContainerNumber }
        .values.sum()

    println("The minimun number of containers needed is $minContainerNumber and there are $totalCountWithMin combinations")
}

fun List<Int>.findAllCombinations(limit: Int, remaining: List<Int>, combinations: MutableMap<List<Int>, Int>) {
    for ((i, next) in remaining.withIndex()) {
        val clone = toMutableList()
        val nextSum = clone.sum() + next

        when {
            nextSum == limit -> {
                clone.add(next)
                combinations[clone] = combinations.getValue(clone) + 1
            }
            nextSum > limit -> break
            else -> {
                clone.add(next)
                val nextRemaining = remaining.subList(i + 1, remaining.size)
                if (nextRemaining.isNotEmpty()) {
                    clone.findAllCombinations(limit, nextRemaining, combinations)
                } else {
                    break
                }
            }
        }
    }
}
