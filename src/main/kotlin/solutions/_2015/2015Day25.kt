package solutions._2015

const val MULTIPLIER = 252533L
const val DIVIDER = 33554393L

fun findSantaWeatherMachineCode(input: String) {
    val split = input.replace(".", "")
        .replace(",", "")
        .split(" ")
    val rows = split[split.lastIndex - 2].toLong()
    val cols = split.last().toLong()

    val position = cols.sum() + (cols * (rows - 1L)) + (rows - 2).sum()
    var next = 20151125L

    repeat((position - 1L).toInt()) {
        next = (next * MULTIPLIER) % DIVIDER
    }

    println("Position $position - $next")
}

fun Long.sum() = this * (this + 1L) / 2L