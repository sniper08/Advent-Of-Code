import parser.inputCleaner
import parser.parseFile
import solutions._2022.*

fun main(args: Array<String>) {
    val startTime = System.currentTimeMillis()
    println(findSandImpossibleToDrop(inputCleaner(parseFile(2022, 14))))
    println((System.currentTimeMillis() - startTime))
}
