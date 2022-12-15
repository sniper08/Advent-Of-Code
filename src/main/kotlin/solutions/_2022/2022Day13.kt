package solutions._2022

import kotlinx.serialization.json.*
import parser.inputCleaner
import solutions._2022.EvaluationType.*

enum class EvaluationType { CORRECT, INCORRECT, CONTINUE }

fun calculateSumOfCorrectlyPacketsIndices(input: Sequence<String>) {
    var sum = 0

    input.forEachIndexed {  i, it ->
        val packetRaw = inputCleaner(it)
        val evaluate = checkIfOrderedCorrectly(
            Json.parseToJsonElement(packetRaw.first()).jsonArray,
            Json.parseToJsonElement(packetRaw.last()).jsonArray
        )

        if (evaluate == CORRECT) {
            sum += i + 1
        }
    }

    println("The sum of indices = $sum")
}

fun calculateMulOfDivisorPackagesIndices(input: Sequence<String>) {
    val divisor2 = "[[2]]"
    val divisor6 = "[[6]]"

    val orderPackages = input
        .flatMap { inputCleaner(it) }
        .map { Json.parseToJsonElement(it).jsonArray }
        .plusElement(Json.parseToJsonElement(divisor2).jsonArray)
        .plusElement(Json.parseToJsonElement(divisor6).jsonArray)
        .sortedWith { a, b ->
            val evaluationType = checkIfOrderedCorrectly(a, b)
            val reverseEvaluationType = checkIfOrderedCorrectly(b, a)

            when {
                evaluationType == CORRECT && reverseEvaluationType == INCORRECT -> -1
                evaluationType == INCORRECT && reverseEvaluationType == CORRECT -> 1
                else -> 0
            }
        }.map { it.toString() }

    orderPackages.forEach { println(it) }

    val indexDivisor2 = orderPackages.indexOf(divisor2) + 1
    val indexDivisor6 = orderPackages.indexOf(divisor6) + 1

    println("The decoder for the stress signal = ${indexDivisor2 * indexDivisor6}")
}

fun checkIfOrderedCorrectly(left: JsonArray, right: JsonArray): EvaluationType {
    var index = 0
    var leftEntry = left.getOrNull(index)
    var rightEntry = right.getOrNull(index)

    while (leftEntry != null && rightEntry != null) {
        val leftValue = try { leftEntry.jsonPrimitive.int } catch (e: Exception) { null }
        val rightValue = try { rightEntry.jsonPrimitive.int } catch (e: Exception) { null }
        val evaluationType = when {
            leftValue != null && rightValue != null -> {
                when {
                    leftValue < rightValue -> CORRECT
                    leftValue > rightValue -> INCORRECT
                    else -> CONTINUE
                }
            }
            leftValue != null -> checkIfOrderedCorrectly(JsonArray(listOf(leftEntry)), rightEntry.jsonArray)
            rightValue != null -> checkIfOrderedCorrectly(leftEntry.jsonArray, JsonArray(listOf(rightEntry)))
            else -> checkIfOrderedCorrectly(leftEntry.jsonArray, rightEntry.jsonArray)
        }

        if (evaluationType != CONTINUE) {
            return evaluationType
        }

        index++
        leftEntry = left.getOrNull(index)
        rightEntry = right.getOrNull(index)

        if (leftEntry == null && rightEntry == null) {
            return CONTINUE
        }
    }

    return when {
        leftEntry == null -> CORRECT
        rightEntry == null -> INCORRECT
        else -> CONTINUE
    }
}
