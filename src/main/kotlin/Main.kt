import parser.inputCleaner
import parser.parseFile
import solutions._2016.findBathroomCode

fun main(args: Array<String>) {
    val startTime = System.currentTimeMillis()
    println(findBathroomCode(inputCleaner(parseFile(2016, 2)), fancy = true))
    println((System.currentTimeMillis() - startTime))
}
