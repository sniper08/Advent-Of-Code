import parser.parseFile
import solutions._2015.findSantaWeatherMachineCode

fun main(args: Array<String>) {
    val startTime = System.currentTimeMillis()
    println(findSantaWeatherMachineCode(parseFile(2015, 25)))
    println((System.currentTimeMillis() - startTime))
}
