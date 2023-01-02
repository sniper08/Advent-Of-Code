import parser.inputCleaner
import parser.parseFile
import solutions._2022.findQualityLevels

fun main(args: Array<String>) {
    val startTime = System.currentTimeMillis()
    println(findQualityLevels(inputCleaner(parseFile(2022, 19))))
    println((System.currentTimeMillis() - startTime))
}
