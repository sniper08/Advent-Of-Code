package solutions._2016

import solutions._2015.NUMBER_REGEX
import kotlin.math.absoluteValue

data class EncryptedPassword(
    val pass: String,
    val sectorID: Int,
    val checksum: String
) {
    companion object {
        private const val ALPH_BEGIN = 'a'.code
        private const val ALPH_END = 'z'.code
    }

    fun isValid(): Boolean {
        var valid = true
        val passCount = pass
            .filter { it.isLetter() }
            .toSet()
            .map { it to pass.count { ch -> it == ch } }
            .sortedByDescending { it.second }
            .toMap().entries
            .groupBy({it.value}, {it.key})

        var countIndex = 0
        var checksumIndex = 0

        while(valid && checksumIndex < checksum.length) {
            val countGroup = passCount.entries.elementAt(countIndex).value
            val leftInChecksum = checksum.substring(checksumIndex)

            valid = if (countGroup.size >= leftInChecksum.length) {
               if (countGroup.containsAll(leftInChecksum.toList())) {
                    val sorted = leftInChecksum.toMutableList().sorted().joinToString("")
                    checksumIndex = checksum.length
                    leftInChecksum == sorted
                } else {
                    false
                }
            } else {
                val original = leftInChecksum.substring(0, countGroup.size)
                if (original.toList().containsAll(countGroup)) {
                    val sorted = original.toMutableList().sorted().joinToString("")
                    checksumIndex += countGroup.size
                    original == sorted
                } else {
                    false
                }
            }
            countIndex++
        }

        return valid
    }

    fun decrypt(): String {
        val passList = pass
            .replace(sectorID.toString(), "")
            .replace("-", " ")
            .toMutableList()

        repeat(sectorID) {
            for ((i, char) in passList.withIndex()) {
                if (char != ' ') {
                    var temp = char.code

                    if (temp < ALPH_END) {
                        temp++
                    } else {
                        temp = ALPH_BEGIN
                    }
                    passList[i] = temp.toChar()
                } else {
                    passList[i] = char
                }
            }
        }

        return passList.joinToString("")
    }
}

fun calculateSectorIdSum(input: Sequence<String>) {
    val passwords = input
        .toList()
        .map {
            val split = it.replace("]","").split("[")
            val sectorID = NUMBER_REGEX.find(split[0])?.value.orEmpty()

            EncryptedPassword(pass = split[0], sectorID = sectorID.toInt().absoluteValue, checksum = split[1])
        }

    val sectorIdSum = passwords.fold(0) { acc, encryptedPassword ->
        val valid = encryptedPassword.isValid()
        println(valid)
        if (valid) {
            acc + encryptedPassword.sectorID
        } else {
            acc
        }
    }
    println("------ The Sector ID sum is $sectorIdSum -----")
}

fun findSectorIdDecrypted(input: Sequence<String>) {
    val located = input
        .toList()
        .map {
            val split = it.replace("]","").split("[")
            val sectorID = NUMBER_REGEX.find(split[0])?.value.orEmpty()

            EncryptedPassword(pass = split[0], sectorID = sectorID.toInt().absoluteValue, checksum = split[1])
        }
        .filter { it.isValid() }
        .filter { it.decrypt().startsWith("north") }

    println(located)
}