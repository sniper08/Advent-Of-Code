package parser

import java.io.File

fun parseFile(year: Int, dayNumber: Int) = File("src/main/kotlin/input/_$year/Input-$year-$dayNumber.txt")
    .readText(Charsets.UTF_8)
    .trimEnd()

fun inputCleaner(input: String, lineJumps: Int = 1) : Sequence<String> {
    val divider = CharArray(lineJumps) { '\n' }.joinToString("")
    return input.split(divider).asSequence()
}
