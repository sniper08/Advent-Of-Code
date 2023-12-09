import parser.inputCleaner
import parser.parseFile
import solutions._2023.*

fun main(args: Array<String>) {
    val startTime = System.currentTimeMillis()
    println(calculateTotalWinningsForCardGame(inputCleaner(parseFile(year = 2023, dayNumber = 7)), jokerRule = true))
    println((System.currentTimeMillis() - startTime))
}
