import parser.inputCleaner
import parser.parseFile
import solutions._2022.findShortestToCrossValley
import solutions._2022.findShortestToCrossValleyPickingUpSnack

fun main(args: Array<String>) {
    val startTime = System.currentTimeMillis()
    println(findShortestToCrossValleyPickingUpSnack(inputCleaner(parseFile(2022, 24))))
    println((System.currentTimeMillis() - startTime))
}

