import parser.inputCleaner
import parser.parseFile
import solutions._2015.calculateLowestCostRPG

fun main(args: Array<String>) {
    val startTime = System.currentTimeMillis()
    println(calculateLowestCostRPG(inputCleaner(parseFile(2015, 21))))
    println((System.currentTimeMillis() - startTime))
}
