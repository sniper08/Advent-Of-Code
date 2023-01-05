package solutions._2022

data class MathMonkey(
    val name: String,
    var value: Long? = null,
    val leftMonkeyName: String = "",
    val rightMonkeyName: String = "",
    val operation: String = ""
) {

    fun calculateValue(allMonkeys: List<MathMonkey>): Long? {
        val value = this.value

        return when {
            value != null && value > Long.MIN_VALUE -> value
            value != null -> null
            else -> {
                val leftMonkeyValue = allMonkeys.first { it.name == leftMonkeyName }.calculateValue(allMonkeys)
                val rightMonkeyValue = allMonkeys.first { it.name == rightMonkeyName }.calculateValue(allMonkeys)

                if (leftMonkeyValue != null && rightMonkeyValue != null) {
                    when (operation) {
                        "+" -> leftMonkeyValue + rightMonkeyValue
                        "-" -> leftMonkeyValue - rightMonkeyValue
                        "*" -> leftMonkeyValue * rightMonkeyValue
                        "/" -> leftMonkeyValue / rightMonkeyValue
                        else -> 0
                    }.also {
                        this.value = it
                    }
                } else {
                    null
                }
            }
        }
    }

    fun findMissingValue(allMonkeys: List<MathMonkey>, goalValue: Long) : Long {
        val leftMonkey = allMonkeys.first { it.name == leftMonkeyName }
        val rightMonkey = allMonkeys.first { it.name == rightMonkeyName }
        val leftMonkeyValue = leftMonkey.calculateValue(allMonkeys)
        val rightMonkeyValue = rightMonkey.calculateValue(allMonkeys)

        if (leftMonkeyValue != null && rightMonkeyValue == null) {
            val newGoal = when (operation) {
                "+" -> goalValue - leftMonkeyValue
                "-" -> leftMonkeyValue - goalValue
                "*" -> goalValue / leftMonkeyValue
                "/" -> leftMonkeyValue / goalValue
                else -> 0L
            }
            return if (rightMonkeyName == "humn") newGoal else rightMonkey.findMissingValue(allMonkeys, newGoal)
        }

        if (rightMonkeyValue != null && leftMonkeyValue == null) {
            val newGoal = when (operation) {
                "+" -> goalValue - rightMonkeyValue
                "-" -> goalValue + rightMonkeyValue
                "*" -> goalValue / rightMonkeyValue
                "/" -> goalValue * rightMonkeyValue
                else -> 0L
            }
            return if (leftMonkeyName == "humn") newGoal else leftMonkey.findMissingValue(allMonkeys, newGoal)
        }

        return 0L
    }
}

fun calculateMathMonkeyValue(input: Sequence<String>) {
    val allMonkeys = input.createMonkeys()

    val rootMonkeyValue = allMonkeys.first { it.name == "root" }.calculateValue(allMonkeys)
    println("Root Monkey value = $rootMonkeyValue")
}

fun calculateMathMonkeyValueCorrected(input: Sequence<String>) {
    val allMonkeys = input.createMonkeys()
    val meMonkey = allMonkeys.first { it.name == "humn" }.apply { value = Long.MIN_VALUE }
    val rootMonkey = allMonkeys.first { it.name == "root" }

    val leftMonkey = allMonkeys.first { it.name == rootMonkey.leftMonkeyName }
    val rightMonkey = allMonkeys.first { it.name == rootMonkey.rightMonkeyName }
    var leftMonkeyValue = leftMonkey.calculateValue(allMonkeys)
    var rightMonkeyValue = rightMonkey.calculateValue(allMonkeys)

    val meValue = when {
        leftMonkeyValue == null && rightMonkeyValue != null -> leftMonkey.findMissingValue(allMonkeys, rightMonkeyValue)
        rightMonkeyValue == null && leftMonkeyValue != null -> rightMonkey.findMissingValue(allMonkeys, leftMonkeyValue)
        else -> null
    }

    println("Left Monkey value = $leftMonkeyValue")
    println("Right Monkey value = $rightMonkeyValue")

    println()
    println("Me value = $meValue")

    meMonkey.value = meValue
    leftMonkeyValue = leftMonkey.calculateValue(allMonkeys)
    rightMonkeyValue = rightMonkey.calculateValue(allMonkeys)

    println()
    println("Left Monkey value = $leftMonkeyValue")
    println("Right Monkey value = $rightMonkeyValue")
}

fun Sequence<String>.createMonkeys() =
    toList()
        .map {
            val raw = it.split(": ", " ")
            val name = raw[0]
            val value = try { raw[1].toLong() } catch (e: Exception) { null }

            if (value != null) {
                MathMonkey(name, value)
            } else {
                MathMonkey(name, leftMonkeyName = raw[1], rightMonkeyName = raw[3], operation = raw[2])
            }
        }