package solutions._2015

import kotlin.math.sqrt

fun calculateLowestHouseNumber(input: String) {
   val num = input.toInt()

    var current = 1
    var sum = current.findSumOffFactorsAfter50()

    while (sum < num) {
        println("Current $current and sum is $sum")
        current++
        sum = current.findSumOffFactorsAfter50()

        if (current == num && sum > num){
            println("The is no house number")
            break
        }
    }

    println("The lowest house number is $current")
}

fun Int.findSumOffFactors() = (1..sqrt(this.toDouble()).toInt() + 1)
    .fold(0) { acc, l ->
        if (this % l == 0) {
            acc + l + this / l
        } else {
            acc + 0
        }
    } * 10

fun Int.findSumOffFactorsAfter50() = (1..sqrt(this.toDouble()).toInt() + 1)
    .fold(0) { acc, l ->
        if (this % l == 0) {
            when {
                this <= 50 -> acc + l + this / l
                l <= 50 -> acc + this / l
                this / l <= 50 -> acc + l
                else -> acc + 0
            }
        } else {
            acc + 0
        }
    } * 11

