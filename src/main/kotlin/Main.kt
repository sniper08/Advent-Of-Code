import parser.inputCleaner
import parser.parseFile
import solutions._2016.*
import solutions._2022.*

fun main(args: Array<String>) {
    val startTime = System.currentTimeMillis()
    println(findHighestScenicScore(inputCleaner(parseFile(2022, 8))))
    println((System.currentTimeMillis() - startTime))
}
