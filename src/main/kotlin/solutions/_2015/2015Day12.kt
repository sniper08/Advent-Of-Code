package solutions._2015

import kotlinx.serialization.json.*

val NUMBER_REGEX = Regex("(-+)?(\\d+)")

const val RED = "red"

fun calculateAccountingSum(input: String) {
    val allNumbers = NUMBER_REGEX.findAll(input).map { it.value.toInt() }

    println(allNumbers.toList())
    println("Sum all: ${allNumbers.sum()}")
}

fun calculateAccountingSumNoRed(input: String) {
    val array = Json.parseJson(input).findUsefulValues()

    println(array)
    println("Sum all correction on Red: ${array.sum()}")
}

fun JsonElement.findUsefulValues() : List<Int> {
    val usefulValues = mutableListOf<Int>()

    getArray()?.findUsefulValues(usefulValues) ?: getObject()?.findUsefulValues(usefulValues)

    return usefulValues
}

fun JsonArray.findUsefulValues(usefulValues: MutableList<Int>) {
    content.forEach { it.addUsefulValues(usefulValues) }
}

fun JsonObject.findUsefulValues(usefulValues: MutableList<Int>) {
    if (content.none { it.value.getStringValue() == RED }) {
        content.values.forEach { it.addUsefulValues(usefulValues) }
    }
}

fun JsonElement.getArray() = try { jsonArray } catch (e: JsonException) { null }
fun JsonElement.getObject() = try { jsonObject } catch (e: JsonException) { null }
fun JsonElement.getStringValue() = try { primitive.content } catch (e: JsonException) { null }
fun JsonElement.getIntValue() = try { primitive.intOrNull } catch (e: JsonException) { null }
fun JsonElement.addUsefulValues(usefulValues: MutableList<Int>) {
    getIntValue()
        ?.let { intValue -> usefulValues.add(intValue) }
        ?: run { usefulValues.addAll(this.findUsefulValues()) }
}
