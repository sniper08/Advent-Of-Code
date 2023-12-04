import parser.inputCleaner
import parser.parseFile
import solutions._2023.*

fun main(args: Array<String>) {
    val startTime = System.currentTimeMillis()
    println(countScratchCards(inputCleaner(parseFile(2023, 4))))
    println((System.currentTimeMillis() - startTime))
}
