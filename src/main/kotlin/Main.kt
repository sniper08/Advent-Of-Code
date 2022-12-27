import parser.inputCleaner
import parser.parseFile
import solutions._2022.*

fun main(args: Array<String>) {
    val startTime = System.currentTimeMillis()
    println(calculateMostPressureReleased(inputCleaner(parseFile(2022, 16)), withElephant = true))
    println((System.currentTimeMillis() - startTime))
}
