import parser.inputCleaner
import parser.parseFile
import solutions._2023.calculateSumCalibrationValuesCorrected

fun main(args: Array<String>) {
    val startTime = System.currentTimeMillis()
    println(calculateSumCalibrationValuesCorrected(inputCleaner(parseFile(2023, 1))))
    println((System.currentTimeMillis() - startTime))
}

