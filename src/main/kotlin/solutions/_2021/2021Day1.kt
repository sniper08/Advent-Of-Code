package solutions._2021

fun calculateDepthIncrease(measures: Sequence<String>): Int = measures
    .map { it.toInt() }
    .windowed(2)
    .count { it[0] < it[1] }

fun calculateSlidingWindowIncrease(measures: Sequence<String>): Int = measures
    .map { it.toInt() }
    .windowed(3)
    .windowed(2)
    .count { it[0].sum() < it[1].sum() }
