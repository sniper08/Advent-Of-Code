package solutions._2016

typealias KeyPad = Array<Array<Key>>

const val UP = 'U'
const val DOWN = 'D'
const val LEFT = 'L'
const val RIGHT = 'R'

data class Key(val x: Int, val y: Int, val num: String? = null) {

    fun findNext(instruction: String, keyPad: KeyPad): Key {
        var nextX = x
        var nextY = y

        for (move in instruction) {
            when (move) {
                UP -> if (nextY > 0) nextY--
                DOWN -> if (nextY < keyPad.lastIndex) nextY++
                LEFT -> if (nextX > 0) nextX--
                RIGHT -> if (nextX < keyPad.lastIndex) nextX++
            }
        }

        return keyPad[nextY][nextX]
    }

    fun findNextFancyKeyPad(instruction: String, keyPad: KeyPad): Key {
        var nextX = x
        var nextY = y

        for (move in instruction) {
            when (move) {
                UP -> {
                    if (nextY > 0) {
                        nextY--
                        if (keyPad[nextY][nextX].num.isNullOrBlank()) nextY++
                    }
                }
                DOWN -> {
                    if (nextY < keyPad.lastIndex) {
                        nextY++
                        if (keyPad[nextY][nextX].num.isNullOrBlank()) nextY--
                    }
                }
                LEFT -> {
                    if (nextX > 0) {
                        nextX--
                        if (keyPad[nextY][nextX].num.isNullOrBlank()) nextX++
                    }
                }
                RIGHT -> {
                    if (nextX < keyPad.lastIndex) {
                        nextX++
                        if (keyPad[nextY][nextX].num.isNullOrBlank()) nextX--
                    }
                }
            }
        }

        return keyPad[nextY][nextX]
    }
}

fun createSimpleKeyPad() = KeyPad(3) { y ->
    Array(3) { x ->
        val num = x + 1 + (y * 3)
        Key(x, y, num.toString())
    }
}

fun createFancyKeyPad(): KeyPad {
    val possibleKeys = listOf(
        Key(2, 0, "1"), // 0
        Key(1, 1, "2"), Key(2, 1, "3"), Key(3, 1, "4"), // 1
        Key(0, 2, "5"), Key(1, 2, "6"), Key(2, 2, "7"), //2
        Key(3, 2, "8"), Key(4, 2, "9"), // 2
        Key(1, 3, "A"), Key(2, 3, "B"), Key(3, 3, "C"), // 3
        Key(2, 4, "D"), //4
    )

    return KeyPad(5) { y ->
        Array(5) { x ->
            possibleKeys.find { it.y == y && it.x == x } ?: Key(x, y)
        }
    }
}

fun findBathroomCode(input: Sequence<String>, fancy: Boolean) {
    val keyPad = if (fancy) createFancyKeyPad() else createSimpleKeyPad()
    keyPad.print()
    var current = if (fancy) keyPad[2][0] else keyPad[1][1]
    var code = ""

    println(current)
    input.forEach {
        current = if (fancy) current.findNextFancyKeyPad(it, keyPad) else current.findNext(it, keyPad)
        code += current.num
        println(current)
    }

    println("Bathroom code: $code")
}

fun KeyPad.print() {
    forEach {
        println(
            it.joinToString(" ") {
                key -> key.num ?: " "
            }
        )
    }
}

