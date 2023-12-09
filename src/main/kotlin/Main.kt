import parser.inputCleaner
import parser.parseFile
import solutions._2023.*

fun main(args: Array<String>) {
    val startTime = System.currentTimeMillis()
    println(calculateStepToFindExitInDesertNetworkV2(inputCleaner(parseFile(year = 2023, dayNumber = 8), lineJumps = 2)))
    println((System.currentTimeMillis() - startTime))
}
