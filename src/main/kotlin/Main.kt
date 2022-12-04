import parser.inputCleaner
import parser.parseFile
import solutions._2016.*
import solutions._2022.*

fun main(args: Array<String>) {
    val startTime = System.currentTimeMillis()
    println(findErrorCorrectedMessage(inputCleaner(parseFile(2016, 6))))
    println((System.currentTimeMillis() - startTime))
}
