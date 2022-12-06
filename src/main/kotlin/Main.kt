import parser.inputCleaner
import parser.parseFile
import solutions._2016.*
import solutions._2022.*

fun main(args: Array<String>) {
    val startTime = System.currentTimeMillis()
    println(findDecompressedSequenceLengthV2(parseFile(2016, 9)))
    println((System.currentTimeMillis() - startTime))
}
