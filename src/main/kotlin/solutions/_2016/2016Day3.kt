package solutions._2016

import solutions._2015.NUMBER_REGEX

fun calculateValidTriangles(input: Sequence<String>) {
    val totalValid = input
        .map { NUMBER_REGEX.findAll(it).map { num -> num.value.toInt() }.toList() }
        .count { it.isValidTriangle() }

    println("Total triangles $totalValid")
}

fun calculateValidTrianglesByColumn(input: Sequence<String>) {
    val totalValid = input
        .map { NUMBER_REGEX.findAll(it).map { num -> num.value.toInt() }.toList() }
        .let {
            val firstColumn = mutableListOf<Int>()
            val secondColumn = mutableListOf<Int>()
            val thirdColumn = mutableListOf<Int>()

            // slightly faster than mapping to three different lists
            it.forEach { line ->
                firstColumn.add(line.first())
                secondColumn.add(line[1])
                thirdColumn.add(line.last())
            }

            firstColumn.countValidTriangles() + secondColumn.countValidTriangles() + thirdColumn.countValidTriangles()
        }
    println("Total triangles $totalValid")
}

fun List<Int>.countValidTriangles() = chunked(3).count { it.isValidTriangle() }

fun List<Int>.isValidTriangle() =
    if (size == 3) {
        val a = first()
        val b = get(1)
        val c = last()

        a + b > c && a + c > b && b + c > a
    } else {
        false
    }