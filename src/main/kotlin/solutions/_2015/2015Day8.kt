package solutions._2015

fun calculateLiteralsMinusMemory(input: Sequence<String>) {
    var literals = 0
    var literalsEncode = 0
    var memory = 0

    val escapedAll = listOf("\\\"", "\\\\")
    val escapedHex = "\\x"

    input.forEach { literal ->
        literals += literal.length
        literalsEncode += literal.length + 4

        var skip = 0
        for (i in 1 until literal.length - 1) {
            if (skip == 0) {
                val current = literal[i]
                val escaped = literal.substring(i..i + 1)

                when {
                    current != '\\' -> memory++
                    escapedAll.any(escaped::equals) -> {
                        skip = 1
                        memory ++
                        literalsEncode += 2
                    }
                    escaped == escapedHex -> {
                        skip = 3
                        memory++
                        literalsEncode++
                    }
                }
            } else {
                skip--
            }
        }
    }

    println("Literal $literals - Memory $memory --> ${literals - memory}")
    println("Literal Encoded $literalsEncode - Literal $literals --> ${literalsEncode - literals}")
}
