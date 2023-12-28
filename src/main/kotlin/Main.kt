import parser.inputCleaner
import parser.parseFile
import solutions._2023.*

fun main(args: Array<String>) {
    val startTime = System.currentTimeMillis()
    println(calculateLeastHeatLossUltra(inputCleaner(parseFile(year = 2023, dayNumber = 17))))
    println((System.currentTimeMillis() - startTime))
}
