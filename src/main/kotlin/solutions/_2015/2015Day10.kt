package solutions._2015

const val ONE_ONE = "1"
const val TWO_ONES = "11"
const val THREE_ONES = "111"
const val ONE_TWO = "2"
const val TWO_TWOS = "22"
const val THREE_TWOS = "222"
const val ONE_THREE = "3"
const val TWO_THREES = "33"
const val THREE_THREES = "333"

val dictionary = mapOf(
    ONE_ONE to "11",
    TWO_ONES to "21",
    THREE_ONES to "31",
    ONE_TWO to "12",
    TWO_TWOS to "22",
    THREE_TWOS to "32",
    ONE_THREE to "13",
    TWO_THREES to "23",
    THREE_THREES to "33"
)

fun createNextMap() = mutableMapOf<String, MutableSet<Int>>().apply {
    putAll(dictionary.map { it.key to mutableSetOf() })
}

fun calculateLengthElvesLookElvesSay(input: String) {
    val currentMap = createNextMap()
    val highestPos = playFirstTurn(input, currentMap)

    val lengthAfterTurns = playNextTurns(currentMap, highestPos, 49)

    println("Length $lengthAfterTurns")
}

fun playNextTurns(startingMap: Map<String, MutableSet<Int>>, highestStartingPos: Int, turns: Int): Int {
    var highestPos = highestStartingPos
    var currentMap = startingMap

    repeat(turns) {
        var takenFromNext = 0
        var nextHighestPos = 0
        val nextMap = createNextMap()
        for (i in 0..highestPos) {
            val key = currentMap.findKeyForPos(i)
            val keyValue = dictionary[key]!!
            var nextKey: String? = null
            var nextKeyValue: String? = null

            if (i <= highestPos - 1 && takenFromNext < 2) {
                nextKey = currentMap.findKeyForPos(i + 1)
                nextKeyValue = dictionary[nextKey]
            }

            when (takenFromNext) {
                0 -> {
                    val first = keyValue.first()
                    val last = keyValue.last()
                    val keyValuesEqual = first == last
                    if (keyValuesEqual) {
                        val nextFirstEqual = last == nextKeyValue?.first()
                        nextMap[last.createKey(if (nextFirstEqual) 3 else 2)]?.add(nextHighestPos++)
                        takenFromNext += if (nextFirstEqual) 1 else 0
                    } else {
                        val nextValueFirst = nextKeyValue?.first()
                        nextMap[first.toString()]?.add(nextHighestPos++)
                        when {
                            last == nextValueFirst && last == nextKeyValue?.last() -> {
                                nextMap[last.createKey(3)]?.add(nextHighestPos++)
                                takenFromNext += 2
                            }
                            last == nextValueFirst -> {
                                nextMap[last.createKey(2)]?.add(nextHighestPos++)
                                takenFromNext += 1
                            }
                            else -> nextMap[last.toString()]?.add(nextHighestPos++)
                        }
                    }
                }
                1 -> {
                    takenFromNext = 0
                    val last = keyValue.last()
                    val nextValueFirst = nextKeyValue?.first()
                    when {
                        last == nextValueFirst && last == nextKeyValue?.last() -> {
                            nextMap[last.createKey(3)]?.add(nextHighestPos++)
                            takenFromNext += 2
                        }
                        last == nextValueFirst -> {
                            nextMap[last.createKey(2)]?.add(nextHighestPos++)
                            takenFromNext++
                        }
                        else -> {
                            nextMap[last.toString()]?.add(nextHighestPos++)
                        }
                    }
                }
                2 -> takenFromNext = 0
            }
        }
        currentMap = nextMap
        highestPos = nextHighestPos - 1
    }
    return (highestPos + 1) * 2
}

fun playFirstTurn(input: String, currentMap: Map<String, MutableSet<Int>>): Int {
    var skip = 0
    var index = 0
    for (i in input.indices) {
        if (skip == 0) {
            val equalCount = input.countEqual(i)
            val key = input[i].createKey(equalCount)
            currentMap[key]?.add(index++)
            skip += equalCount - 1
        } else {
            skip--
        }
    }
    return index - 1
}

fun Map<String, MutableSet<Int>>.findKeyForPos(pos: Int) = toList().first { it.second.contains(pos) }.first

fun String.countEqual(fromIndex: Int): Int {
    var count = 1
    var index = fromIndex

    while (get(fromIndex) == getOrNull(++index)) {
        count++
    }

    return count
}

fun Char.createKey(times: Int): String {
    var key = ""
    repeat(times) { key += this }
    return key
}
