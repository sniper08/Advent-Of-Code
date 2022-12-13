import parser.inputCleaner
import parser.parseFile
import solutions._2016.*
import solutions._2022.*

fun main(args: Array<String>) {
    val startTime = System.currentTimeMillis()
    println(findLevelOfMonkeyBusiness(inputCleaner(parseFile(2022, 11), lineJumps = 2)))
    println((System.currentTimeMillis() - startTime))
}
