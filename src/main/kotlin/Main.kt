import parser.inputCleaner
import parser.parseFile
import solutions._2015.calculateLowestManaSpent
import solutions._2015.calculateTuringLock

fun main(args: Array<String>) {
    val startTime = System.currentTimeMillis()
    println(calculateTuringLock(inputCleaner(parseFile(2015, 23))))
    println((System.currentTimeMillis() - startTime))
}
