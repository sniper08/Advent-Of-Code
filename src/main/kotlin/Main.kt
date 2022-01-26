import parser.inputCleaner
import parser.parseFile
import solutions._2015.calculateLowestManaSpent

fun main(args: Array<String>) {
    val startTime = System.currentTimeMillis()
    println(calculateLowestManaSpent(inputCleaner(parseFile(2015, 22))))
    println((System.currentTimeMillis() - startTime))
}
