package solutions._2015

import getArray
import getIntValue
import getObject
import getStringValue
import kotlinx.serialization.json.*

val NUMBER_REGEX = Regex("(-+)?(\\d+)")

const val RED = "red"

fun calculateAccountingSum(input: String) {
    val allNumbers = NUMBER_REGEX.findAll(input).map { it.value.toInt() }

    println(allNumbers.toList())
    println("Sum all: ${allNumbers.sum()}")
}

fun calculateAccountingSumNoRed(input: String) {
    val array = Json.parseToJsonElement(input).findUsefulValues()

    println(array)
    println("Sum all correction on Red: ${array.sum()}")
}

fun JsonElement.findUsefulValues() : List<Int> {
    val usefulValues = mutableListOf<Int>()

    getArray()?.findUsefulValues(usefulValues) ?: getObject()?.findUsefulValues(usefulValues)

    return usefulValues
}

fun JsonArray.findUsefulValues(usefulValues: MutableList<Int>) {
    forEach { it.addUsefulValues(usefulValues) }
}

fun JsonObject.findUsefulValues(usefulValues: MutableList<Int>) {
    if (values.none { it.getStringValue() == RED }) {
        values.forEach { it.addUsefulValues(usefulValues) }
    }
}

fun JsonElement.addUsefulValues(usefulValues: MutableList<Int>) {
    getIntValue()
        ?.let { intValue -> usefulValues.add(intValue) }
        ?: run { usefulValues.addAll(this.findUsefulValues()) }
}
