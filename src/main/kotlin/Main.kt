import parser.inputCleaner
import parser.parseFile
import solutions._2016.*
import solutions._2022.*

fun main(args: Array<String>) {
    val startTime = System.currentTimeMillis()
    println(findShortestHillRouteScenic(inputCleaner(parseFile(2022, 12))))
    println((System.currentTimeMillis() - startTime))
}
