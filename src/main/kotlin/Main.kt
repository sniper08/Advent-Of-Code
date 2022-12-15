import parser.inputCleaner
import parser.parseFile
import solutions._2016.*
import solutions._2022.*

fun main(args: Array<String>) {
    val startTime = System.currentTimeMillis()
    println(calculateSumOfCorrectlyPacketsIndices(inputCleaner(parseFile(2022, 13), lineJumps = 2)))
    println((System.currentTimeMillis() - startTime))
}
