import parser.inputCleaner
import parser.parseFile
import solutions._2022.calculateMathMonkeyValueCorrected

fun main(args: Array<String>) {
    val startTime = System.currentTimeMillis()
    println(calculateMathMonkeyValueCorrected(inputCleaner(parseFile(2022, 21))))
    println((System.currentTimeMillis() - startTime))
}
