import parser.parseFile
import solutions._2015.calculateAccountingSum
import solutions._2015.calculateAccountingSumNoRed

fun main(args: Array<String>) {
    val startTime = System.currentTimeMillis()
    println(calculateAccountingSumNoRed(parseFile(2015, 12)))
    println((System.currentTimeMillis() - startTime))
}
