import parser.inputCleaner
import parser.parseFile
import solutions._2016.calculateLitOnPixelsLittleScreen
import solutions._2022.MyPlay
import solutions._2022.calculatePointsRockPaperScissors
import solutions._2022.calculatePointsRockPaperScissorsImproved

fun main(args: Array<String>) {
    val startTime = System.currentTimeMillis()
    println(calculateLitOnPixelsLittleScreen(inputCleaner(parseFile(2016, 8))))
    println((System.currentTimeMillis() - startTime))
}
