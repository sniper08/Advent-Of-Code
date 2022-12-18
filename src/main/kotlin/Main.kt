import parser.inputCleaner
import parser.parseFile
import solutions._2022.*

fun main(args: Array<String>) {
    val startTime = System.currentTimeMillis()
    println(findDistressSignalTuningFrequency(inputCleaner(parseFile(2022, 15))))
    println((System.currentTimeMillis() - startTime))
}
