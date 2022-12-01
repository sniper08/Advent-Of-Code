import parser.inputCleaner
import parser.parseFile
import solutions._2022.calculateMostCaloriesTopThreeElves

fun main(args: Array<String>) {
    val startTime = System.currentTimeMillis()
    println(calculateMostCaloriesTopThreeElves(inputCleaner(parseFile(2022, 1), lineJumps = 2)))
    println((System.currentTimeMillis() - startTime))
}
