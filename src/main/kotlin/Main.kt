import parser.inputCleaner
import parser.parseFile
import solutions._2023.*

fun main(args: Array<String>) {
    val startTime = System.currentTimeMillis()
    println(summarizeReflectionsWithSmudge(inputCleaner(parseFile(year = 2023, dayNumber = 13), lineJumps = 2)))
    println((System.currentTimeMillis() - startTime))
}
