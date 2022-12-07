import parser.inputCleaner
import parser.parseFile
import solutions._2016.*
import solutions._2022.*

fun main(args: Array<String>) {
    val startTime = System.currentTimeMillis()
    println(findStartOfPacketMarketInProtocol(parseFile(2022, 6), markerSize = 14))
    println((System.currentTimeMillis() - startTime))
}
