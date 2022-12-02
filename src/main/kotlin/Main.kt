import parser.inputCleaner
import parser.parseFile
import solutions._2022.MyPlay
import solutions._2022.calculatePointsRockPaperScissors
import solutions._2022.calculatePointsRockPaperScissorsImproved

fun main(args: Array<String>) {
    val startTime = System.currentTimeMillis()
    println(calculatePointsRockPaperScissors(inputCleaner(parseFile(2022, 2))))
    println((System.currentTimeMillis() - startTime))
}
