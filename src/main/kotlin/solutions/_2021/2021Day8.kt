package solutions._2021

// signals needed per number
val uniqueSignal = mapOf(
    1 to 2,
    4 to 4,
    7 to 3,
    8 to 7,
)

// pattern per number
val uniquePatterns = mutableMapOf(
    1 to mutableSetOf<Char>(),
    4 to mutableSetOf<Char>(),
    7 to mutableSetOf<Char>(),
    8 to mutableSetOf<Char>()
)

// for patterns with 6 signals
// to be the number(key) they need to fulfill // (currentpattern - pair.first).size = pair.second
val randomSignal6 get() = mapOf(
    0 to (uniquePatterns[8] to 1),
    6 to (uniquePatterns[7] to 1),
    9 to (uniquePatterns[7] to 0)
)
val randomSignal5 get() = mapOf(
    2 to (uniquePatterns[4] to 2),
    3 to (uniquePatterns[1] to 0),
    5 to (uniquePatterns[4] to 1)
)

fun calculateUniqueSignalPatterns(input: Sequence<String>) : Int {
    var counter = 0

    input.forEach {
        counter +=
            it.split(" | ")[1]
                .split(' ')
                .count { signal -> uniqueSignal.containsValue(signal.length) }
    }

    return counter
}

fun calculateUniqueSignalPatternsCorrected(input: Sequence<String>) : Long {
    var counter: Long = 0L
    input.forEach { patternAndOutPut ->
        val (pattern, output) = patternAndOutPut.split(" | ")
            .let {
                Pair(it[0].split(' '), it[1].split(' '))
            }

        pattern.forEach {
            when (it.length) {
                uniqueSignal[1] -> uniquePatterns[1] = it.toHashSet()
                uniqueSignal[4] -> uniquePatterns[4] = it.toHashSet()
                uniqueSignal[7] -> uniquePatterns[7] = it.toHashSet()
                uniqueSignal[8] -> uniquePatterns[8] = it.toHashSet()
                else -> { }
            }
        }

        val outputNumber = CharArray(4)

        output.forEachIndexed { index, signalForOutput ->
            when (val setSignalForOutput = signalForOutput.toHashSet()) {
                uniquePatterns[1] -> outputNumber[index] = '1'
                uniquePatterns[4] -> outputNumber[index] = '4'
                uniquePatterns[7] -> outputNumber[index] = '7'
                uniquePatterns[8] -> outputNumber[index] = '8'
                else -> {
                    if (setSignalForOutput.size == 5) {
                        when {
                            (randomSignal5[3]?.first?.subtract(setSignalForOutput))?.size == randomSignal5[3]?.second ->  outputNumber[index] = '3'
                            (randomSignal5[5]?.first?.subtract(setSignalForOutput))?.size == randomSignal5[5]?.second ->  outputNumber[index] = '5'
                            (randomSignal5[2]?.first?.subtract(setSignalForOutput))?.size == randomSignal5[2]?.second ->  outputNumber[index] = '2'
                        }
                    } else {
                        when {
                            (randomSignal6[6]?.first?.subtract(setSignalForOutput))?.size == randomSignal6[6]?.second ->  outputNumber[index] = '6'
                            (randomSignal6[9]?.first?.subtract(setSignalForOutput))?.size == randomSignal6[9]?.second
                                    && (uniquePatterns[4]?.subtract(setSignalForOutput))?.size == 0 ->  outputNumber[index] = '9'
                            else -> outputNumber[index] = '0'
                        }
                    }
                }
            }
        }
        counter += outputNumber.concatToString().toLong()
    }

    return counter
}
