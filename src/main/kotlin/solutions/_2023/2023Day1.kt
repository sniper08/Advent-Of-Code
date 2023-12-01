package solutions._2023

import kotlin.math.min

data class Found(
    var firstDigit: Char? = null,
    var lastDigit: Char? = null
) {
    fun toInt() = "${firstDigit ?: '0'}${lastDigit ?: '0'}".toInt()

    fun add(c: Char) {
        if (firstDigit == null) {
            firstDigit = c
            lastDigit = c
        } else {
            lastDigit = c
        }
    }
}
fun calculateSumCalibrationValues(input: Sequence<String>) {
    val sumCalibrationValues = input.sumOf { encrypted ->
        val found = Found()

        for (c in encrypted) {
            if (c.isDigit()) found.add(c)
        }

        found.toInt()
    }

    println("The sum of calibration values is $sumCalibrationValues")
}

fun calculateSumCalibrationValuesCorrected(input: Sequence<String>) {
    val possibleNumbers = mapOf(
        'o' to mapOf("one" to '1'),
        't' to mapOf("two" to '2', "three" to '3'),
        'f' to mapOf("four" to '4', "five" to '5'),
        's' to mapOf("six" to '6', "seven" to '7'),
        'e' to mapOf("eight" to '8'),
        'n' to mapOf("nine" to '9')
     )

    val sumCalibrationValues = input.sumOf { encrypted ->
        val found = Found()

        var index = 0

        while (index <= encrypted.lastIndex) {
            val c = encrypted[index]

            var nextIndex = index + 1

            if (c.isDigit()) {
                found.add(c)
            } else {
                possibleNumbers[c]?.let { possibleNumbersForChar ->
                    for (number in possibleNumbersForChar) {
                        val length = number.key.length
                        val endIndex = min(index + length, encrypted.length)
                        val possibleInEncrypted = encrypted.substring(index, endIndex)

                        if (possibleInEncrypted == number.key) {
                            found.add(number.value)
                            nextIndex = endIndex - 1
                            break
                        }
                    }
                }
            }
            index = nextIndex
        }

        found.toInt()
    }

    println("The sum of calibration values is $sumCalibrationValues")
}