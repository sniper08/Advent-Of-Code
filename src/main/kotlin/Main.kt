import parser.inputCleaner
import parser.parseFile
import solutions._2022.findSNAFUNumberForBob
import solutions._2022.findShortestToCrossValleyPickingUpSnack

fun main(args: Array<String>) {
    val startTime = System.currentTimeMillis()
    println(findSNAFUNumberForBob(inputCleaner(parseFile(2022, 25))))
    println((System.currentTimeMillis() - startTime))
}

