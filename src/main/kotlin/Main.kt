import parser.parseFile
import solutions._2016.findMD5PasswordImproved

fun main(args: Array<String>) {
    val startTime = System.currentTimeMillis()
    println(findMD5PasswordImproved(parseFile(2016, 5)))
    println((System.currentTimeMillis() - startTime))
}
