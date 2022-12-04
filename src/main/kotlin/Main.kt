import parser.inputCleaner
import parser.parseFile
import solutions._2016.calculateLitOnPixelsLittleScreen
import solutions._2022.*

fun main(args: Array<String>) {
    val startTime = System.currentTimeMillis()
    println(calculatePrioritiesSumByType(inputCleaner(parseFile(2022, 3))))
    println((System.currentTimeMillis() - startTime))
}
