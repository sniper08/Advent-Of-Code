package solutions._2015

import createMD5Hash
import day.Day
import java.security.MessageDigest

class Year2015Day4 : Day {

    private val mD5finder = MD5finder()

    /**
     * Lowest number to create MD5 hash with 5 zeroes as padding
     */
    override fun part1(input: Sequence<String>): String {
        val minMD5Num = mD5finder.findMinMD5Num(
            input = input.first(),
            zeroesPadding = 5
        )
        return "$minMD5Num"
    }

    /**
     * Lowest number to create MD5 hash with 6 zeroes as padding
     */
    override fun part2(input: Sequence<String>): String {
        val minMD5Num = mD5finder.findMinMD5Num(
            input = input.first(),
            zeroesPadding = 6
        )
        return "$minMD5Num"
    }
}

private class MD5finder {

    private val md5 = MessageDigest.getInstance("MD5")

    fun findMinMD5Num(
        input: String,
        zeroesPadding: Int,
    ): Long {
        val zeroesPaddingString = CharArray(zeroesPadding) { '0' }.joinToString("")
        var found = false
        var minNumber = 0L

        while (!found) {
            val newInput = input + minNumber.toString()
            val hash = md5.createMD5Hash(newInput)
            println("$newInput is hashed to $hash")

            if (hash.startsWith(zeroesPaddingString)) {
                found = true
            } else {
                minNumber++
            }
        }

        return minNumber
    }
}
