import parser.inputCleaner
import parser.parseFile
import solutions._2016.*
import solutions._2022.*

fun main(args: Array<String>) {
    val startTime = System.currentTimeMillis()
    println(findTopCrateInSupplyStack(inputCleaner(parseFile(2022, 5), lineJumps = 2)))
    println((System.currentTimeMillis() - startTime))
}
