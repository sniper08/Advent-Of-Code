import day.Day
import parser.inputCleaner
import parser.parseFile
import kotlin.reflect.full.primaryConstructor

fun main(args: Array<String>) {
    Runner(year = 2024)
        .run(
            20, 21, 22, 23
        )
}

class Runner(private val year: Int) {

    fun run(vararg days: Int) {
        days.forEach { day ->
            val runningDay = Class
                .forName("solutions._$year.Year${year}Day$day")
                .kotlin
                .primaryConstructor!!
                .call() as Day

            val dayInput = inputCleaner(
                input = parseFile(year = year, dayNumber = day),
                lineJumps = runningDay.lineJumpsInput
            )

            println("--------------Year: ${year}-------Day: ${day}----------------------------")
            println("--Part 1--")
            println("Running...")
            var startTime = System.currentTimeMillis()
            val part1Answer = runningDay.part1(input = dayInput)
            val part1Time = System.currentTimeMillis() - startTime

            println("--Part 2--")
            println("Running...")
            startTime = System.currentTimeMillis()
            val part2Answer = runningDay.part2(input = dayInput)
            val part2Time = System.currentTimeMillis() - startTime

            println("------------------------------------------------------------------")
            println("--Part 1--")
            println("Execution Time: $part1Time")
            println("Answer: $part1Answer")

            println("--Part 2--")
            println("Execution Time: $part2Time")
            println("Answer: $part2Answer")

            println("------------------------------------------------------------------")
        }
    }
}
