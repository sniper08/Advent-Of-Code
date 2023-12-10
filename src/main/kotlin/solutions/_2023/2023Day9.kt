package solutions._2023

data class History(
    val history: MutableList<Long>,
    val level: Int = 0
) {
    private val subHistory: History? = if (history.all { it == 0L }) {
        null
    } else {
        History(
            history = history
                .windowed(2, 1)
                .map {
                    it.last() - it.first()
                }.toMutableList(),
            level = level + 1
        )
    }

    fun addNext() {
        if (subHistory == null) {
            history.add(0)
        } else {
            subHistory.addNext()
            history.add(history.last() + subHistory.history.last())
        }
    }

    fun addPrevious() {
        if (subHistory == null) {
            history.add(0, 0)
        } else {
            subHistory.addPrevious()
            history.add(0, history.first() - subHistory.history.first())
        }
    }

    override fun toString(): String = history.joinToString(
        prefix = List(level) { "  " }.joinToString(""),
        separator = "   ",
        postfix = List(level) { "  " }.joinToString("")
    ) {
        it.toString()
    } + "\n" + (subHistory?.toString() ?: "")
}

fun calculateSumOfOasisExtrapolatedValues(input: Sequence<String>) {
    val histories = input.map {
        History(
            history = it
                .split(" ")
                .map { value -> value.toLong() }
                .toMutableList()
        )
    }

    var sumFirst = 0L
    var sumLast = 0L

    histories
        .toList()
        .forEach {
            it.addNext()
            it.addPrevious()
            println(it)
            sumFirst += it.history.first()
            sumLast += it.history.last()
        }

    println("The sum of last Oasis extrapolated values is $sumLast")
    println("The sum of previous Oasis extrapolated values is $sumFirst")
}