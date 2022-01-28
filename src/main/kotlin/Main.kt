import parser.inputCleaner
import parser.parseFile
import solutions._2016.findErrorCorrectedMessage
import solutions._2016.findMD5PasswordImproved

fun main(args: Array<String>) {
    val startTime = System.currentTimeMillis()
    println(findErrorCorrectedMessage(inputCleaner(parseFile(2016, 6))))
    println((System.currentTimeMillis() - startTime))
}
