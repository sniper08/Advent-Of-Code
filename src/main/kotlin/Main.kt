import parser.inputCleaner
import parser.parseFile
import solutions._2023.calculateSumOfPowersCubesGame

fun main(args: Array<String>) {
    val startTime = System.currentTimeMillis()
    println(calculateSumOfPowersCubesGame(inputCleaner(parseFile(2023, 2))))
    println((System.currentTimeMillis() - startTime))
}

