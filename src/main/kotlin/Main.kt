import parser.inputCleaner
import parser.parseFile
import solutions._2016.Protocol
import solutions._2016.findIPv7AddressesSupporting

fun main(args: Array<String>) {
    val startTime = System.currentTimeMillis()
    println(findIPv7AddressesSupporting(Protocol.SSL, inputCleaner(parseFile(2016, 7))))
    println((System.currentTimeMillis() - startTime))
}
