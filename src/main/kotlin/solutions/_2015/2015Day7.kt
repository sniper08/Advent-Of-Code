package solutions._2015

import solutions._2015.BitWiseOperation.*

enum class BitWiseOperation { AND, OR, LSHIFT, RSHIFT, NOT }

data class BitwiseValue(
    val value: String,
    var intValue: UShort? = null,
    val bitwiseValue: String
) {

    fun findIntValue(values: Map<String, BitwiseValue>) : UShort {
        var firstIntValue: UShort = 0u
        var secondIntValue: UShort = 0u

        return intValue ?: run {
            val split = bitwiseValue.split(" ")
            val operator = when {
                split.size > 2 -> {
                    firstIntValue = split[0].toUShort(values)
                    secondIntValue = split[2].toUShort(values)
                    split[1]
                }
                split.size == 2 -> {
                    firstIntValue = split[1].toUShort(values)
                    split[0]
                }
                else -> {
                    firstIntValue = split[0].toUShort(values)
                    null
                }
            }
            when (operator) {
                AND.name -> firstIntValue and secondIntValue
                OR.name -> firstIntValue or secondIntValue
                LSHIFT.name -> firstIntValue.toInt().shl(secondIntValue.toInt()).toUShort()
                RSHIFT.name -> firstIntValue.toInt().shr(secondIntValue.toInt()).toUShort()
                NOT.name -> firstIntValue.inv()
                else -> firstIntValue
            }.also { intValue = it }
        }
    }
}

fun String.toUShort(values: Map<String, BitwiseValue>) = try {
    toUShort()
} catch (e: Exception) {
    values[this]!!.findIntValue(values)
}

fun calculateBitWiseValue(input: Sequence<String>) {
    val values = input.map {
        val split = it.split(" -> ")
        val intValue = try { split[0].toUShort() } catch (e: Exception) { null }

        split[1] to BitwiseValue(value = split[1], intValue, bitwiseValue = split[0])
    }.toMap()

    values.forEach { it.value.findIntValue(values) }

    var valueForA = values["a"]!!.intValue

    println("First value for a: $valueForA")

    values.forEach { it.value.intValue = null }
    values["b"]!!.intValue = valueForA
    values.forEach { it.value.findIntValue(values) }

    valueForA = values["a"]!!.intValue
    println("Second value for a: $valueForA")
}
