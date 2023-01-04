import parser.inputCleaner
import parser.parseFile
import solutions._2022.decryptGroveCoordinates
import solutions._2022.findQualityLevels

fun main(args: Array<String>) {
    val startTime = System.currentTimeMillis()
    println(decryptGroveCoordinates(inputCleaner(parseFile(2022, 20))))
    println((System.currentTimeMillis() - startTime))
}
