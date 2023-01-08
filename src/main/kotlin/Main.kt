import parser.inputCleaner
import parser.parseFile
import solutions._2022.findEmptyGroundInGrove
import solutions._2022.findForceFieldPassword
import solutions._2022.findRoundsToNoMovesInGrove

fun main(args: Array<String>) {
    val startTime = System.currentTimeMillis()
    println(findEmptyGroundInGrove(inputCleaner(parseFile(2022, 23))))
    println((System.currentTimeMillis() - startTime))
}

