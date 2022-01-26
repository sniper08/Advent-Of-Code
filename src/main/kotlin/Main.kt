import parser.inputCleaner
import parser.parseFile
import solutions._2015.calculateLowestManaSpent
import solutions._2015.calculateSmallestQuantumElement
import solutions._2015.calculateTuringLock

fun main(args: Array<String>) {
    val startTime = System.currentTimeMillis()
    println(calculateSmallestQuantumElement(inputCleaner(parseFile(2015, 24))))
    println((System.currentTimeMillis() - startTime))
}
