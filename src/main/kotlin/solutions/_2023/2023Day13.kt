package solutions._2023

import parser.inputCleaner
import solutions._2023.Orientation.HORIZONTAL
import solutions._2023.Orientation.VERTICAL

enum class Orientation {
    VERTICAL,
    HORIZONTAL;

    fun opposite() = if (this == VERTICAL) HORIZONTAL else VERTICAL
}

const val MIRROR = '#'
const val ROCK = '.'

data class Pattern(
    val verticalMap: MutableMap<Int, MutableSet<Int>>,
    val horizontalMap: MutableMap<Int, MutableSet<Int>>,
    val maxY: Int,
    val maxX: Int
)

data class FoundIndex(val orientation: Orientation, val index: Int) {

    fun summarize(): Int {
       // println("Reflection line between lines ${index + 1} and ${index + 2} -> ${orientation.name.uppercase()}")
        return when (orientation) {
            VERTICAL -> index + 1
            HORIZONTAL -> (index + 1) * 100
        }
    }
}

fun summarizeReflections(input: Sequence<String>) {
    val summarize = input
        .sumOf {
            val pattern = createPattern(inputCleaner(it))
            findIndex(pattern, VERTICAL)?.summarize()
                ?: findIndex(pattern, HORIZONTAL)?.summarize()
                ?: 0
        }
    println("Result after summarizing all pattern $summarize")
}

fun summarizeReflectionsWithSmudge(input: Sequence<String>) {
    val summarize = input
        .sumOf {
            val patternInput = inputCleaner(it)
            val originalPattern = createPattern(patternInput)
            val originalFound = findIndex(originalPattern, VERTICAL) ?: findIndex(originalPattern, HORIZONTAL) ?: return@sumOf 0

            var replaceY = 0
            var replaceX = 0
            var foundIndex: FoundIndex? = null

            while (foundIndex == null && replaceY <= originalPattern.maxY && replaceX <= originalPattern.maxX) {
                val pattern = createPattern(patternInput, replaceY, replaceX)
                val nextOrientation = originalFound.orientation.opposite()

                var possibleFoundIndex = findIndex(pattern, nextOrientation)

                if (possibleFoundIndex == null) {
                    possibleFoundIndex = findIndex(pattern, originalFound.orientation, originalFound.index)
                }

                if (possibleFoundIndex != null) {
                    foundIndex = possibleFoundIndex
                } else {
                    replaceY = if (replaceX < originalPattern.maxX) replaceY else replaceY + 1
                    replaceX = if (replaceX < originalPattern.maxX) replaceX + 1 else 0
                }
            }

            foundIndex?.summarize() ?: originalFound.summarize()
        }
    println("Result after summarizing all patterns $summarize")
}

fun findIndex(pattern: Pattern, orientation: Orientation, skipIndex: Int = -1): FoundIndex? {
    val foundIndex = when (orientation) {
        VERTICAL -> findReflectingIndex(pattern.verticalMap, skipIndex)
        HORIZONTAL -> findReflectingIndex(pattern.horizontalMap, skipIndex)
    }
    return if (foundIndex < 0) null else FoundIndex(orientation, foundIndex)
}

fun createPattern(input: Sequence<String>, replaceY: Int = -1, replaceX: Int = -1): Pattern {
    val pattern = input.toList()
    val verticalMap = mutableMapOf<Int, MutableSet<Int>>()
    val horizontalMap = mutableMapOf<Int, MutableSet<Int>>()

    for (y in 0..pattern.lastIndex) {
        val yLine = pattern[y]
        for (x in 0..yLine.lastIndex) {
            val realChar = yLine[x]
            val char = if (y == replaceY && x == replaceX) {
                if (realChar == MIRROR) ROCK else MIRROR
            } else {
                realChar
            }

            if (char == MIRROR) {
                val inHorizontal = horizontalMap.getOrDefault(y, mutableSetOf()).apply { add(x) }
                horizontalMap[y] = inHorizontal

                val inVertical = verticalMap.getOrDefault(x, mutableSetOf()).apply { add(y) }
                verticalMap[x] = inVertical
            }
        }
    }

    return Pattern(verticalMap.toSortedMap(), horizontalMap.toSortedMap(), maxY = pattern.lastIndex, maxX = pattern.first().lastIndex)
}

private fun findReflectingIndex(map: Map<Int, MutableSet<Int>>, skipIndex: Int = -1): Int {
    var foundIndex = -1
    var index = 0

    while (foundIndex < 0 && index <= map.keys.last()) {
        val a = map[index]
        val b = map[index + 1]

        if (a == b) {
            var found = true
            var nextLeft = index - 1
            var nextRight = index + 2
            var leftA = map[nextLeft]
            var rightB = map[nextRight]

            while (found && leftA != null && rightB != null) {
                found = leftA == rightB
                nextLeft--
                nextRight++
                leftA = map[nextLeft]
                rightB = map[nextRight]
            }

            if (found && index != skipIndex) {
                foundIndex = index
            } else {
                index++
            }
        } else {
            index++
        }
    }
    return foundIndex
}