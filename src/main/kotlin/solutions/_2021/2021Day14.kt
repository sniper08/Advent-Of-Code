package solutions._2021

import parser.inputCleaner

data class ChangeablePosition(var position: Long)

data class Transition(
    val from: String,
    val element: Char,
    val transition1: String,
    val transition2: String,
    var total: Long = 0L,
    var previousStepCount: Long = 0L,
    var currentStepCount: Long = 0L
) {

    fun increaseTotal() {
        total++
    }

    fun increaseTotalBy(amount: Long) {
        total += amount
    }

    fun increaseCurrentStepCountBy(amount: Long) {
        currentStepCount += amount
    }

    fun finishStep() {
        previousStepCount = currentStepCount
        currentStepCount = 0L
    }
}

fun createTransitionsList(input: String) = inputCleaner(input)
    .toList()
    .associate {
        it.split(" -> ").let { transition ->
            transition[0] to
                    Transition(
                        from = transition[0],
                        element = transition[1].first(),
                        transition1 = "" + transition[0].first() + transition[1],
                        transition2 = "" + transition[1] + transition[0].last()
                    )
        }
    }

fun calculateElementsDifference(input: Sequence<String>) : Long {
    val transitionList = createTransitionsList(input.last())
    val polymer = input.first()

    var expectedCount: Long = polymer.length.toByte() + polymer.length.toLong() - 1L

    repeat(40) {
        println("Expected Count ${it + 1} : $expectedCount")
        expectedCount = expectedCount + expectedCount -1
    }

    polymer.windowed(2).forEach { bigram ->
        transitionList[bigram]?.let {
            it.increaseTotal()
            it.increaseCurrentStepCountBy(1L)
        }
    }
    transitionList.forEach { it.value.finishStep() }

    repeat(39) {
        transitionList.filter { it.value.previousStepCount > 0 }.forEach {
            val transition = it.value

            transitionList[transition.transition1]?.let { transition1 ->
                transition1.increaseTotalBy(transition.previousStepCount)
                transition1.increaseCurrentStepCountBy(transition.previousStepCount)
            }
            transitionList[transition.transition2]?.let { transition2 ->
                transition2.increaseTotalBy(transition.previousStepCount)
                transition2.increaseCurrentStepCountBy(transition.previousStepCount)
            }
        }
        transitionList.forEach { it.value.finishStep() }
    }

    return transitionList.values
        .groupBy(Transition::element, Transition::total)
        .map { it.key to it.value.sum() }
        .toMap()
        .let { finalCount ->
            val initialPolymerCount = polymer.groupingBy { it }.eachCount()

            mutableMapOf<Char, Long>().apply {
                finalCount.forEach {
                    this[it.key] = it.value + (initialPolymerCount[it.key]?.toLong() ?: 0L)
                }
            }
        }
        .onEach { println(it) }
        .let {
            println("Total count : " + it.values.sum())
            val max: Long = it.maxOf { it.value }
            val min: Long = it.minOf { it.value }

            max - min
        }
}
