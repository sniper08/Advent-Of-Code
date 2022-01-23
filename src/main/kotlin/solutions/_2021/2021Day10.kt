package solutions._2021

val correctChunks = setOf("()", "[]", "{}", "<>")

val incorrectChunks = setOf("(]", "(}", "(>", "[)", "[}", "[>", "{)", "{]", "{>", "<)", "<]", "<}")

val pointsIncorrect = mapOf(
    ')' to 3,
    ']' to 57,
    '}' to 1197,
    '>' to 25137
)

val pointsClosing = setOf(
    ')' to 1L,
    ']' to 2L,
    '}' to 3L,
    '>' to 4L
)

val openers = setOf('(', '[' , '{' , '<')

fun calculateCorruptedLines(input: Sequence<String>): Int {
    var points = 0

    original@ for (original in input) {
        var previous = original
        var sanitized = original

        do {
            previous = sanitized

            for (correctChunk in correctChunks) {
                sanitized = sanitized.replace(correctChunk, "")

                val incorrectChunkPresent = incorrectChunks.firstOrNull { sanitized.contains(it) }

                if (incorrectChunkPresent != null) {
                    points += pointsIncorrect[incorrectChunkPresent[1]] ?: 0
                    continue@original
                }
            }
        } while (sanitized.isNotEmpty() && sanitized != previous)
    }

    return points
}

fun calculateMissingClosingChunks(input: Sequence<String>): Long {
    val sanitizedList = mutableListOf<String>()
    var points = 0

    original@ for (original in input) {
        var previous = original
        var sanitized = original

        do {
            previous = sanitized

            for (correctChunk in correctChunks) {
                sanitized = sanitized.replace(correctChunk, "")

                val incorrectChunkPresent = incorrectChunks.firstOrNull { sanitized.contains(it) }

                if (incorrectChunkPresent != null) {
                    continue@original
                }
            }
        } while (sanitized.isNotEmpty() && sanitized != previous)

        if (sanitized == previous) sanitizedList.add(sanitized)
    }

    val pointsList = mutableListOf<Long>()

    sanitizedList.forEach {
        var closersValue = 0L
        for (opener in it.reversed()) {
            val index = openers.indexOfFirst { containedOpener -> containedOpener == opener }

            closersValue = closersValue * 5L + pointsClosing.elementAt(index).second
        }

        pointsList.add(closersValue)
    }

    return pointsList.sorted()[pointsList.size / 2]
}
