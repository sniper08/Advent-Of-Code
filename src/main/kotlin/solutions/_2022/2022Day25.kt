package solutions._2022

import kotlin.math.pow

fun findSNAFUNumberForBob(input: Sequence<String>) {
    val sum = input.sumOf {
        val decimal = it.snafuToDecimal()
        val base5 = decimal.toString(5)
        val snafu = base5.base5ToSnafu()

        println("$decimal ========= $base5 ====== $snafu")
        decimal
    }

    val sumInBase5 = sum.toString(5)
    val sumSnafu = sumInBase5.base5ToSnafu()

    println("Sum = $sum")
    println("Base 5 = $sumInBase5")
    println("Sum Snafu = $sumSnafu")
}

fun String.snafuToDecimal(): Long {
    var decimal: Long = 0

    for ((i, c) in this.withIndex()) {
        val multiplier = 5.0.pow(lastIndex - i).toLong()

        decimal += try {
            multiplier * c.digitToInt()
        } catch (e: Exception) {
            when (c) {
                '-' -> multiplier * -1
                '=' -> multiplier * -2
                else -> 0
            }
        }
    }

    return decimal
}

fun String.base5ToSnafu(): String {
    val snafu = this.map { it.toString() }.toMutableList()
    var add = false

    for (i in lastIndex downTo 0) {
        val current = snafu[i].toInt() + if (add) 1 else 0

        snafu[i] = when (current) {
            3 -> { add = true ; "=" }
            4 -> { add = true ; "-" }
            5 -> { add = true ; "0" }
            else -> { add = false ; current.toString() }
        }
    }

    if (add) {
        snafu.add(0, "1")
    }

    return snafu.joinToString("") { it }
}

fun MutableList<String>.snafu() = joinToString("") { if (it == " ") "0" else it }