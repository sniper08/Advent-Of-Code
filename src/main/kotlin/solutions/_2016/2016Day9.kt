package solutions._2016

fun findDecompressedSequenceLength(input: String) {
    var length = 0
    var index = 0

    while (index <= input.lastIndex)  {
        val current = input[index]

        when (current) {
            '(' -> {
                val indexOfMarkerClose = input.drop(index).indexOfFirst { it == ')' } + index
                val marker = input.substring(index + 1, indexOfMarkerClose).split("x")
                val charactersToTake = marker.first().toInt()
                val times = marker.last().toInt()

                length += charactersToTake * times
                index = indexOfMarkerClose + charactersToTake + 1
            }
            ' ' -> { index++ }
            else -> {
                length++
                index++
            }
        }
    }

    println("Decompressed length: $length")
}

fun findDecompressedSequenceLengthV2(input: String) {
    var length = 0L
    var index = 0

    while (index <= input.lastIndex)  {
        println(index)
        val current = input[index]

        when (current) {
            '(' -> {
                val indexOfMarkerClose = input.drop(index).indexOfFirst { it == ')' } + index
                val marker = input.substring(index + 1, indexOfMarkerClose).split("x")
                val nextIndex = indexOfMarkerClose + marker.first().toInt() + 1
                val lengthToAdd = calculateLengthToAdd(
                    charactersAfterMarker = input.substring(indexOfMarkerClose + 1, nextIndex),
                    multiplier = marker.last().toLong()
                )

                length += lengthToAdd
                index = nextIndex
            }
            ' ' -> { index++ }
            else -> {
                length++
                index++
            }
        }
    }

    println("Decompressed length: $length")
}

fun calculateLengthToAdd(charactersAfterMarker: String, multiplier: Long): Long {
    var lengthToAdd = 0L
    var index = 0

    while (index <= charactersAfterMarker.lastIndex) {
        val current = charactersAfterMarker[index]

        if (current == '(') {
            val indexOfMarkerClose = charactersAfterMarker.drop(index).indexOfFirst { it == ')' } + index
            val marker = charactersAfterMarker.substring(index + 1, indexOfMarkerClose).split("x")
            val nextIndex = indexOfMarkerClose + marker.first().toInt() + 1
            val charAfterMarker = charactersAfterMarker[indexOfMarkerClose + 1]

            if (charAfterMarker == '(') {
                lengthToAdd += calculateLengthToAdd(
                    charactersAfterMarker = charactersAfterMarker.substring(indexOfMarkerClose + 1, nextIndex),
                    multiplier = marker.last().toLong() * multiplier
                )
            } else {
                lengthToAdd += multiplier * marker.first().toLong() * marker.last().toLong()
            }
            index = nextIndex
        } else {
            index++
        }
    }

    return if (lengthToAdd == 0L) {
        charactersAfterMarker.length * multiplier
    } else {
        lengthToAdd
    }
}