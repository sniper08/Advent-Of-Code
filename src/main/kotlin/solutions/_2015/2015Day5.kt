package input._2015

val notAllowed = arrayOf("ab", "cd", "pq", "xy")
val double = ('a'..'z').map { it.toString() + it }.toTypedArray()
val vowels = arrayOf('a', 'e', 'i', 'o', 'u')

fun calculateNiceCount(input: Sequence<String>) {
    var counter = 0

    for (file in input) {
        if (!file.containsAny(notAllowed)
            && file.containsAny(double)
            && file.containsThree(vowels)
        ) {
            counter++
        }
    }

    println("Total Nice: $counter")
}

fun calculateNiceCountCorrected(input: Sequence<String>) {
    var counter = 0

    for (file in input) {
        var firstRule = false // pair of letters that appears twice, no overlap
        var secondRule = false // same letter with letter in between

        for (i in 0..file.length - 3) {
            val current = file[i]
            val next = file [i + 1]
            val afterNext = file[i + 2]

            if (!secondRule) {
                secondRule = current == afterNext
            }

            if (!firstRule && i < file.length - 3) {
                val pair = current.toString() + next
                firstRule = file.substring(i + 2).contains(pair)
            }

            if (firstRule && secondRule) {
                counter++
                break
            }
        }
    }

    println("Total Nice: $counter")
}

fun String.containsAny(words: Array<String>) = words.any(this::contains)
fun String.containsThree(vowels: Array<Char>) = vowels.sumOf { this.count(it::equals) } >= 3
