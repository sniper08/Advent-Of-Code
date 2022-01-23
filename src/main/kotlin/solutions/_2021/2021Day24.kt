package solutions._2021

import solutions._2021.AluOperation.*
import java.util.*

enum class AluOperation(val operator: String) {
    INP("inp"),
    ADD("add"),
    MUL("mul"),
    DIV("div"),
    MOD("mod"),
    EQL("eql");

    companion object {
        fun fromStringOperation(operator: String?) =
            when (operator) {
                INP.operator -> INP
                ADD.operator -> ADD
                MUL.operator -> MUL
                DIV.operator -> DIV
                MOD.operator -> MOD
                EQL.operator -> EQL
                else -> INP
            }
    }
}

data class AluInstruction(
    val index: Int,
    val aluOperation: AluOperation,
    val recipient: Char,
    val other: Char? = null,
    var otherNum: Int? = null
) {
    fun canTakeInput() = aluOperation == INP

    fun setInput(input: Int) {
        if (other == null && otherNum == null) {
            otherNum = input
        }
    }

    companion object {
        fun fromStringInstruction(instruction: String, index: Int): AluInstruction =
            instruction.split(" ").let {
                val other: Char? = try { it[2].first() } catch (e: Exception) { null }
                val otherNum: Int? = try { it[2].toInt() } catch (e: Exception) { null }

                AluInstruction(
                    index = index,
                    aluOperation = AluOperation.fromStringOperation(it[0]),
                    recipient = it[1].first(),
                    other = other,
                    otherNum = otherNum
                )
            }
    }
}

class ALU {

    val holders = mutableMapOf(
        'w' to 0,
        'x' to 0,
        'y' to 0,
        'z' to 0
    )

    @Throws(InputMismatchException::class)
    fun executeInstruction(aluInstruction: AluInstruction) {
        val operationLeft = holders[aluInstruction.recipient]!!
        val other: Int = aluInstruction.otherNum ?: holders[aluInstruction.other]!!

        holders[aluInstruction.recipient] = when (aluInstruction.aluOperation) {
            INP -> other
            ADD -> operationLeft + other
            MUL -> operationLeft * other
            DIV -> if (other == 0) {
                throw InputMismatchException("DIV by 0 not possible $aluInstruction")
            } else {
                operationLeft / other
            }
            MOD -> if (operationLeft < 0 || other <= 0) {
                throw InputMismatchException("MOD not possible $aluInstruction")
            } else  {
                operationLeft % other
            }
            EQL -> if (operationLeft == other) 1 else 0
        }
    }

    fun print() {
        holders.forEach {
            println("Value ${it.key}: ${it.value}")
        }
    }
}

data class AluInstructionReduce(val check: Int, val offSet: Int)

fun calculateMONADNumbers(input: Sequence<String>) {
    val patterns = findStackPattern(input).toList()
    val toFind = mutableMapOf<Int, Pair<Int,Int>>()
    val stack = mutableMapOf<Int, Int>().apply { put(0, patterns.first().offSet) }

    var index = 1
    while (stack.isNotEmpty()) {
        val pattern = patterns[index]
        if (pattern.check > 0) {
            stack[index] = pattern.offSet
        } else {
            val lastKey = stack.keys.last()
            val lastOffSet = stack.values.last()
            stack.remove(lastKey)
            toFind[index] = Pair(lastKey, lastOffSet + pattern.check)
        }
        index++
    }

    patterns.forEach { println(it) }
    println(toFind)

    val highest = findHighestNOMAD(toFind)
    println("Highest Nomad : $highest")
    verifyMONAD(highest, input)

    val lowest = findLowestNOMAD(toFind)
    println("Lowest Nomad : $lowest")
    verifyMONAD(highest, input)
}

fun findHighestNOMAD(toFind: Map<Int, Pair<Int,Int>>): Long {
    val nomadDigits = MutableList(14) { 0 }

    toFind.forEach { digit ->
        val offSet = digit.value.second

        if (offSet < 0) {
            nomadDigits[digit.key] = 9 + offSet
            nomadDigits[digit.value.first] = 9
        } else {
            nomadDigits[digit.key] = 9
            nomadDigits[digit.value.first] = 9 - offSet
        }
    }

    return nomadDigits.joinToString("").toLong()
}

fun findLowestNOMAD(toFind: Map<Int, Pair<Int,Int>>): Long {
    val nomadDigits = MutableList(14) { 0 }

    toFind.forEach { digit ->
        val offSet = digit.value.second

        if (offSet < 0) {
            nomadDigits[digit.key] = 1
            nomadDigits[digit.value.first] = 1 - offSet
        } else {
            nomadDigits[digit.key] = 1 + offSet
            nomadDigits[digit.value.first] = 1
        }
    }

    return nomadDigits.joinToString("").toLong()
}

fun findStackPattern(input: Sequence<String>) =
    input.windowed(18, 18)
        .map {
            val check = it[5].split(" ")[2].toInt()
            val offSet = it[15].split(" ")[2].toInt()

            AluInstructionReduce(check, offSet)
        }

fun verifyMONAD(monad: Long, input: Sequence<String>) {
    val alu = ALU()
    var inputCounter = 0
    val inputList = (monad).toDigits()

    input.forEachIndexed { indexInstruction, it ->
        val instruction = AluInstruction.fromStringInstruction(it, indexInstruction).also { aluInstruction ->
            if (aluInstruction.canTakeInput()) {
                aluInstruction.setInput(inputList[inputCounter])
                inputCounter++
            }
        }
        alu.executeInstruction(instruction)
    }

    if (alu.holders['z']!! == 0) {
        alu.print()
    }
}

fun Long.toDigits() = toString().map { it.toString().toInt() }
