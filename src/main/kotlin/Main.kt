import parser.parseFile
import solutions._2022.findHighestRockAfter2022

fun main(args: Array<String>) {
    val startTime = System.currentTimeMillis()
    println(findHighestRockAfter2022(parseFile(2022, 17)))
    println((System.currentTimeMillis() - startTime))
}
