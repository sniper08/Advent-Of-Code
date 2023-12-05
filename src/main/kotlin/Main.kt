import parser.inputCleaner
import parser.parseFile
import solutions._2023.*

fun main(args: Array<String>) {
    val startTime = System.currentTimeMillis()
    println(calculateLowestLocationForSeedRanges(inputCleaner(parseFile(2023, 5), lineJumps = 2)))
    println((System.currentTimeMillis() - startTime))
}
