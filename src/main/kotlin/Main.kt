import parser.inputCleaner
import parser.parseFile
import solutions._2023.calculateSumOfGearRatiosInEngineSchematics
import solutions._2023.calculateSumOfNumberPartsInEngineSchematics
import solutions._2023.calculateSumOfPowersCubesGame

fun main(args: Array<String>) {
    val startTime = System.currentTimeMillis()
    println(calculateSumOfNumberPartsInEngineSchematics(inputCleaner(parseFile(2023, 3))))
    println((System.currentTimeMillis() - startTime))
}
