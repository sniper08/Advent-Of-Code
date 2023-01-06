import parser.inputCleaner
import parser.parseFile
import solutions._2022.findForceFieldPassword

fun main(args: Array<String>) {
    val startTime = System.currentTimeMillis()
    println(findForceFieldPassword(inputCleaner(parseFile(2022, 22), lineJumps = 2), inCube = true))
    println((System.currentTimeMillis() - startTime))
}
