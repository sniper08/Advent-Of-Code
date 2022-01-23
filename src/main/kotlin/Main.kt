import parser.parseFile
import solutions._2015.calculateLowestHouseNumber

fun main(args: Array<String>) {
    val startTime = System.currentTimeMillis()
    println(calculateLowestHouseNumber(parseFile(2015, 20)))
    println((System.currentTimeMillis() - startTime))
}
