import parser.inputCleaner
import parser.parseFile
import solutions._2016.*
import solutions._2022.*

fun main(args: Array<String>) {
    val startTime = System.currentTimeMillis()
    println(findSignalStrengthsSum(inputCleaner(parseFile(2022, 10))))
    println((System.currentTimeMillis() - startTime))
}
