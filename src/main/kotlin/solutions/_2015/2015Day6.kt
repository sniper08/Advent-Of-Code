package solutions._2015

const val TOGGLE = "toggle"

class Led {
    var brightness: Long = 0L

    fun increase() { brightness++ }

    fun decrease() {
        if (brightness > 0L) {
            brightness--
        }
    }

    fun toggle() { brightness += 2L }
}

fun calculateLitLights(input: Sequence<String>) {
    val grid = Array(1000) { Array(1000) { false } }

    for (instruction in input) {
        val split = instruction.split(" ", ",")
        grid.updateLitState(split, instruction.startsWith(TOGGLE))
    }

    println("Total on: ${grid.flatten().count { it }}")
}

fun Array<Array<Boolean>>.updateLitState(split: List<String>, toggle: Boolean) {
    val xRange = split[split.size - 5].toInt()..split[split.size - 2].toInt()
    val yRange = split[split.size - 4].toInt()..split.last().toInt()

    slice(yRange).forEach { line ->
        xRange.forEach {
            line[it] = if (toggle) !line[it] else split[1].contains("n")
        }
    }
}

fun calculateLitLightsWellTranslated(input: Sequence<String>) {
    val grid = Array(1000) { Array(1000) { Led() } }

    for (instruction in input) {
        val split = instruction.split(" ", ",")
        grid.updateLitState(split, instruction.startsWith(TOGGLE))
    }

    println("Total on: ${grid.flatten().sumOf { it.brightness }}")
}

fun Array<Array<Led>>.updateLitState(split: List<String>, toggle: Boolean) {
    val xRange = split[split.size - 5].toInt()..split[split.size - 2].toInt()
    val yRange = split[split.size - 4].toInt()..split.last().toInt()

    slice(yRange).forEach { line ->
        xRange.forEach {
            val led = line[it]
            when {
                toggle -> led.toggle()
                split[1].contains("n") -> led.increase()
                else -> led.decrease()
            }
        }
    }
}
