import parser.inputCleaner
import parser.parseFile
import solutions._2016.calculateValidTrianglesByColumn

fun main(args: Array<String>) {
    val startTime = System.currentTimeMillis()
    println(calculateValidTrianglesByColumn(inputCleaner(parseFile(2016, 3))))
    println((System.currentTimeMillis() - startTime))
}
