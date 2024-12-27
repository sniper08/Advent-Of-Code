package solutions._2024

import day.Day
import parser.inputCleaner
import solutions._2024.Year2024Day17.Instruction3Bit.*
import kotlin.math.pow

class Year2024Day17 : Day {

    override val year: Int = 2024
    override val day: Int = 17

    override val lineJumpsInput: Int = 2

    /**
     * Run the program for the created computer
     */
    override fun part1(input: Sequence<String>): String {
        val computer3Bit = createComputer3Bit(input = input)
        //println(computer3Bit)

        val outputs = computer3Bit.runProgram()

//        println()
//        println(computer3Bit)

        return outputs
    }

    /**
     * Find the lowest number that when set as RegisterA in the computer causes the program to give
     * itself as the result of running it
     */
    override fun part2(input: Sequence<String>): String {
        val computer3Bit = createComputer3Bit(input = input)

        println("------- Limit in 8^2 --------")
        val currentProgramRuns = mutableListOf<ProgramRun>()
            .apply { addAll(computer3Bit.runProgramIn(rangeStart = 0, rangeEnd = 64L, comparisonDigits = 2)) }

        var comparisonDigits = 3

        while (currentProgramRuns.none { it.programOutput == computer3Bit.programPrint}) {
            println("------- Limit in 8^${comparisonDigits} --------")
            val innerCurrentProgramRuns = currentProgramRuns.toList()
            currentProgramRuns.clear()

            for (programRun in innerCurrentProgramRuns) {
                val rangeStart = programRun.input * 8
                val rangeEnd = rangeStart + 8

                currentProgramRuns.addAll(
                    computer3Bit.runProgramIn(rangeStart = rangeStart, rangeEnd = rangeEnd, comparisonDigits = comparisonDigits)
                )
            }

            comparisonDigits++
        }

        return "${currentProgramRuns.first().input}"
    }

    private fun createComputer3Bit(input: Sequence<String>): Computer3Bit {
        val splitRegisters = inputCleaner(input = input.first())
            .map {
                it.split(": ")
            }
        val splitProgram = input
            .last()
            .split(": ", ",")

        return Computer3Bit(
            registerA = splitRegisters.elementAt(0)[1].toLong(),
            registerB = splitRegisters.elementAt(1)[1].toLong(),
            registerC = splitRegisters.elementAt(2)[1].toLong(),
            program = splitProgram.drop(1).map { it.toLong() }
        )
    }

    enum class Instruction3Bit {
        ADV, BXL, BST, JNZ, BXC, OUT, BDV, CDV
    }

    fun List<Long>.print() = joinToString(",") { it.toString() }

    data class ProgramRun(
        val input: Long,
        val programOutput: String,
    )

    inner class Computer3Bit(
        var registerA: Long,
        var registerB: Long,
        var registerC: Long,
        val program: List<Long>
    ) {
        val programPrint = program.print()

        private val Long.literal get() =  this

        private val Long.combo get() = when (this) {
            in 0..3 -> literal
            4L -> registerA
            5L -> registerB
            6L -> registerC
            else -> error("Out of bounds")
        }

        fun runProgram(): String {
            val outputs = mutableListOf<Long>()

            var instructionPointer = 0L

            while (instructionPointer <= program.lastIndex) {
                val opcode = program.getOrNull(instructionPointer.toInt()) ?: return outputs.print()
                val operand = program.getOrNull(instructionPointer.toInt() + 1) ?: return outputs.print()

                val instruction = Instruction3Bit.entries[opcode.toInt()]

                when (instruction) {
                    ADV -> {
                        val numerator = registerA
                        val denominator = 2.0.pow(operand.combo.toDouble()).toLong()

                        registerA = numerator / denominator
                    }
                    BXL -> {
                        registerB = registerB xor operand.literal
                    }
                    BST -> {
                        registerB = operand.combo % 8
                    }
                    JNZ -> {
                        if (registerA != 0L) {
                            instructionPointer = operand.literal
                            continue
                        }
                    }
                    BXC -> {
                        registerB = registerB xor registerC
                    }
                    OUT -> {
                        outputs.add(operand.combo % 8)
                    }
                    BDV -> {
                        val numerator = registerA
                        val denominator = 2.0.pow(operand.combo.toDouble()).toLong()

                        registerB = numerator / denominator
                    }
                    CDV -> {
                        val numerator = registerA
                        val denominator = 2.0.pow(operand.combo.toDouble()).toLong()

                        registerC = numerator / denominator
                    }
                }
                instructionPointer += 2
            }

            return outputs.print()
        }

        fun runProgramIn(
            rangeStart: Long,
            rangeEnd: Long,
            comparisonDigits: Int,
        ): List<ProgramRun> {
            val programRuns = mutableListOf<ProgramRun>()
            val takeFromProgramPrint = (comparisonDigits * 2) - 1

            var counter = rangeStart

            while (counter in rangeStart..<rangeEnd) {
                registerA = counter
                val outputs = runProgram()
                if (outputs == programPrint.takeLast(takeFromProgramPrint)) {
                    println("Outputs: $outputs              ---> $counter")
                    programRuns.add(
                        ProgramRun(
                            input = counter,
                            programOutput = outputs
                        )
                    )
                }
                counter++
            }

            return programRuns
        }

        override fun toString(): String = """
            Register A: $registerA
            Register B: $registerB
            Register C: $registerC
            
            Program: ${program.print()}
        """.trimIndent()
    }
}