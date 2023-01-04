package solutions._2022

import kotlin.math.abs

data class Position(var value: Long, var initialIndex: Int) {
    override fun toString(): String = value.toString()
}

fun decryptGroveCoordinates(input: Sequence<String>) {
    val arrangement = input.toList().mapIndexed { i, value ->
        Position(value.toLong() * 811589153, initialIndex = i)
    }

    println(arrangement)

    repeat(10) {
        var nextMovingIndex = 0
        while (nextMovingIndex <= arrangement.lastIndex) {
            val fromMove = arrangement.first { it.initialIndex == nextMovingIndex }.copy()
            val currentIndex = arrangement.indexOf(fromMove)
            when {
                fromMove.value > 0 -> {
                    var nextPosition = (currentIndex + abs(fromMove.value % arrangement.lastIndex) + 1).toInt()

                    if (nextPosition <= arrangement.lastIndex) {
                        for (j in currentIndex + 1 until nextPosition) {
                            val from = arrangement[j]
                            val to = arrangement[j - 1]
                            to.value = from.value ; to.initialIndex = from.initialIndex
                        }
                        val toMove = arrangement[nextPosition - 1]
                        toMove.value = fromMove.value ; toMove.initialIndex = fromMove.initialIndex
                    } else {
                        nextPosition -= arrangement.size
                        for (j in currentIndex - 1 downTo nextPosition) {
                            val from = arrangement[j]
                            val to = arrangement[j + 1]
                            to.value = from.value ; to.initialIndex = from.initialIndex
                        }
                        val toMove = arrangement[nextPosition]
                        toMove.value = fromMove.value ; toMove.initialIndex = fromMove.initialIndex
                    }
                }
                fromMove.value < 0 -> {
                    var nextPosition = (currentIndex - (abs(fromMove.value) % arrangement.lastIndex) - 1).toInt()

                    if (nextPosition >= 0) {
                        for (j in currentIndex - 1 downTo nextPosition + 1) {
                            val from = arrangement[j]
                            val to = arrangement[j + 1]
                            to.value = from.value ; to.initialIndex = from.initialIndex
                        }
                        val toMove = arrangement[nextPosition + 1]
                        toMove.value = fromMove.value ; toMove.initialIndex = fromMove.initialIndex
                    } else {
                        nextPosition += arrangement.size
                        for (j in currentIndex + 1..nextPosition) {
                            val from = arrangement[j]
                            val to = arrangement[j - 1]
                            to.value = from.value ; to.initialIndex = from.initialIndex
                        }
                        val toMove = arrangement[nextPosition]
                        toMove.value = fromMove.value ; toMove.initialIndex = fromMove.initialIndex
                    }
                }
            }

           // println(arrangement)
            nextMovingIndex++
        }
       println(arrangement)
    }

    val indexOf0 = arrangement.indexOfFirst { it.value == 0L }

    val position1000 = (indexOf0 + (1000 % arrangement.size)) % arrangement.size
    val position2000 = (position1000 + (1000 % arrangement.size)) % arrangement.size
    val position3000 = (position2000 + (1000 % arrangement.size)) % arrangement.size

    println("1000th --- ${arrangement[position1000]} --- Index = $position1000")
    println("2000th --- ${arrangement[position2000]} --- Index = $position2000")
    println("3000th --- ${arrangement[position3000]} --- Index = $position3000")

    println("Sum ${listOf(arrangement[position1000], arrangement[position2000], arrangement[position3000]).sumOf { it.value }}")
}