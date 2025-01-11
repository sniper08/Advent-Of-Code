import day.Day
import parser.inputCleaner
import parser.parseFile
import solutions._2024.*

fun main(args: Array<String>) {
    Runner().run(
        Year2024Day23()
    )
}

class Runner {

    fun run(vararg days: Day) {
        days.forEach { day ->
            val dayInput = inputCleaner(
                input = parseFile(year = day.year, dayNumber = day.day),
                lineJumps = day.lineJumpsInput
            )


            println("--------------Year: ${day.year}-------Day: ${day.day}----------------------------")
            println("--Part 1--")
            println("Running...")
            var startTime = System.currentTimeMillis()
            val part1Answer = day.part1(input = dayInput)
            val part1Time = System.currentTimeMillis() - startTime

            println("--Part 2--")
            println("Running...")
            startTime = System.currentTimeMillis()
            val part2Answer = day.part2(input = dayInput)
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
