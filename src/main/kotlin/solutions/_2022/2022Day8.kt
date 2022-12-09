package solutions._2022

data class Tree(val value: Int, var visible: Boolean)

fun findVisibleTrees(input: Sequence<String>) {
    val yCount = input.count()
    val treeGrid = List(yCount) { y ->
        val row = input.elementAt(y)

        List(input.first().length) { x ->
            val treeValue = row[x].digitToInt()
            val visible = y == 0
                    || x == 0
                    || y == yCount - 1
                    || x == input.first().lastIndex
                    || evaluateVisible(x, y, treeValue, row.map { it.digitToInt() }, column = input.map { it[x].digitToInt() }.toList())

            Tree(treeValue, visible)
        }
    }

    treeGrid.forEach {
        println(it.joinToString("") { tree -> if (tree.visible) "${tree.value}" else " " })
    }
    println("\nVisible trees = ${treeGrid.flatten().count { it.visible }}")
}

fun evaluateVisible(x: Int, y: Int, treeValue: Int, row: List<Int>, column: List<Int>): Boolean =
    column.subList(0, y).max() < treeValue
            || column.subList(y + 1, column.size).max() < treeValue
            || row.subList(0, x).max() < treeValue
            || row.subList(x + 1, row.size).max() < treeValue

fun findHighestScenicScore(input: Sequence<String>) {
    var highest = 0

    val columnIndices = input.first().indices.drop(1).dropLast(1)
    val columns = mutableListOf<List<Int>>()

    columnIndices.forEach { x ->
        columns.add(input.map { it[x].digitToInt() }.toList())
    }

    for (y in 1 until input.count() - 1) {
        val rawRow = input.elementAt(y)
        val row = rawRow.map { it.digitToInt() }

        for (x in columnIndices) {
            val treeValue = rawRow[x].digitToInt()
            val column = columns[x -1]

            val scoreLeft = row.subList(0, x).reversed().calculateAxisScore(treeValue)
            val scoreRight = row.subList(x + 1, row.size).calculateAxisScore(treeValue)
            val scoreTop = column.subList(0, y).reversed().calculateAxisScore(treeValue)
            val scoreBottom = column.subList(y + 1, column.size).calculateAxisScore(treeValue)
            val score = scoreLeft * scoreRight * scoreTop * scoreBottom

            if (score > highest) {
                highest = score
            }
        }
    }

    println("The highest scenic score is = $highest")
}

fun List<Int>.calculateAxisScore(treeValue: Int) = indexOfFirst { it >= treeValue }
    .let {
        if (it > -1) {
            it + 1
        } else {
            size
        }
    }


