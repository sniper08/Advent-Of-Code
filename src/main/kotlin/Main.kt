import parser.parseFile
import solutions._2016.calculateBlockToEasterBunnyHQVisitedTwice

fun main(args: Array<String>) {
    val startTime = System.currentTimeMillis()
    println(calculateBlockToEasterBunnyHQVisitedTwice(parseFile(2016, 1)))
    println((System.currentTimeMillis() - startTime))
}
