import parser.inputCleaner
import parser.parseFile
import solutions._2023.*

fun main(args: Array<String>) {
    val startTime = System.currentTimeMillis()
    println(calculateCubicMetersInLagoon(inputCleaner(parseFile(year = 2023, dayNumber = 18)), inHex = true))
    println((System.currentTimeMillis() - startTime))
}
