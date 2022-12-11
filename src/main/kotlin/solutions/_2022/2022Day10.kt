package solutions._2022

typealias CrtScreen = Array<MutableList<Char>>
data class ExecutionValues(var x: Long, var cycle: Int) {
    fun executeCycle(checks: MutableMap<Int, Long?>, increase: Long = 0L) {
        val check = checks.entries.firstOrNull { it.value == null }
        if (cycle == check?.key) {
            check.setValue(cycle * x)
            println("---***************---")
        }

        println("Cycle $cycle - X = $x == Signal Strength = ${check?.value}")
        cycle++
        x += increase
    }

    fun executeCycleDrawing(crtScreen: CrtScreen, increase: Long = 0L) {
        crtScreen.withIndex().firstOrNull { it.value.size < 40 }
            ?.let {
                val position = (cycle - (40 * it.index)) - 1
                it.value.add(if (position in (x - 1)..(x + 1)) '#' else '.')
            }

        println("\nCycle: $cycle - Sprite Middle: $x")
        crtScreen.filter { it.size > 0 }.forEach {
            println(it.joinToString("") { pixel -> "$pixel" })
        }

        cycle++
        x += increase
    }
}
fun findSignalStrengthsSum(input: Sequence<String>) {
    val executionValues = ExecutionValues(x = 1, cycle = 1)

    val checks = mutableMapOf<Int, Long?>(
        20 to null,
        60 to null,
        100 to null,
        140 to null,
        180 to null,
        220 to null
    )

    executeInstructions(input) { increase ->
        executionValues.executeCycle(checks, increase)
    }

    println("Sum of signal strengths = ${checks.values.sumOf { it ?: 0 }}")
}

fun findCRTScreenLetters(input: Sequence<String>) {
    val executionValues = ExecutionValues(x = 1, cycle = 1)
    val crtScreen = Array(6) { mutableListOf<Char>() }

    executeInstructions(input) { increase ->
        executionValues.executeCycleDrawing(crtScreen, increase)
    }
}

fun executeInstructions(input: Sequence<String>, executable: (Long) -> Unit) {
    input.forEach {
        val instructionRaw = it.split(" ")

        when (instructionRaw[0]) {
            "noop" -> executable(0)
            "addx" -> {
                executable(0)
                executable(instructionRaw[1].toLong())
            }
        }
    }
}


