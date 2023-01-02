import parser.inputCleaner
import parser.parseFile
import solutions._2022.calculateTotalSurfaceArea
import solutions._2022.calculateTotalSurfaceAreaCorrected

fun main(args: Array<String>) {
    val startTime = System.currentTimeMillis()
    println(calculateTotalSurfaceAreaCorrected(inputCleaner(parseFile(2022, 18))))
    println((System.currentTimeMillis() - startTime))
}
