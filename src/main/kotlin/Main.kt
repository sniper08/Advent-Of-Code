import parser.inputCleaner
import parser.parseFile

import solutions._2016.findSectorIdDecrypted

fun main(args: Array<String>) {
    val startTime = System.currentTimeMillis()
    println(findSectorIdDecrypted(inputCleaner(parseFile(2016, 4))))
    println((System.currentTimeMillis() - startTime))
}
