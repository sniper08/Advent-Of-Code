import parser.inputCleaner
import parser.parseFile
import solutions._2016.calculateLitOnPixelsLittleScreen
import solutions._2022.*

fun main(args: Array<String>) {
    val startTime = System.currentTimeMillis()
    println(findOverlappingAssignments(inputCleaner(parseFile(2022, 4))))
    println((System.currentTimeMillis() - startTime))
}
