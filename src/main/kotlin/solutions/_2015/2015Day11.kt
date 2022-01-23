package solutions._2015

val alphabet = 'a'..'z'
val alphabetSequence = alphabet.windowed(3).map { it.joinToString("") }

const val I = 'i'
const val O = 'o'
const val L = 'l'

fun findNextValidPassword(input: String) {
    val currentPass = input.toMutableList()

    while (true) {
        currentPass.increase()
        val currentPassString = currentPass.joinToString("")
        if (currentPassString.isValidPassword()) {
            println("$currentPassString Is Valid")
            break
        } else {
            println("$currentPassString Not Valid")
        }
    }
}

fun MutableList<Char>.increase() {
    val invalidChars = withIndex().filter { it.value == I || it.value == O || it.value == L }
    val cuttingPos = invalidChars.maxByOrNull { it.index }

    if (invalidChars.isEmpty() || cuttingPos?.index == size - 1) {
        var pos = size - 1
        var applyForNextPos = true

        while (applyForNextPos && pos >= 0) {
            val current = get(pos)
            val replace = if (current == alphabet.last) {
                alphabet.first
            } else {
                alphabet.elementAt(alphabet.indexOf(current) + 1)
            }
            removeAt(pos)
            add(pos, replace)
            applyForNextPos = current == alphabet.last
            pos--
        }
    } else {
        cuttingPos?.let {
            removeAt(it.index)
            add(it.index, alphabet.elementAt(alphabet.indexOf(it.value) + 1))

            for (i in it.index + 1 until size) {
                removeAt(i)
                add(i, alphabet.first)
            }
        }
    }
}

fun String.isValidPassword(): Boolean {
    val pairSet = mutableSetOf<String>()
    var threeInARow = false

    for (i in indices) {
        val char = get(i)

        if (!char.isValid()) return false
        if (!threeInARow && length - i >= 3) {
            threeInARow = alphabetSequence.any { it == substring(i, i + 3) }
        }
        val nextChar = getOrNull(i + 1)
        if (nextChar != null && char == nextChar) {
            pairSet.add("$char$nextChar")
        }
    }

    return pairSet.size >= 2 && threeInARow
}

fun Char.isValid() = this != I && this != O && this != L
