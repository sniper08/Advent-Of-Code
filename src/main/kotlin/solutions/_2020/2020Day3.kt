package solutions._2020

fun countTrees(levels: Sequence<String>, increaseX: Int = 3, increaseY: Int = 1) : Int {
    val levelsList = levels.toList()
    val levelSize = levelsList.first().length
    val tree = '#'

    var counterTrees = 0
    var x = 1 + increaseX
    var y = 0 + increaseY

    while (y <= levelsList.size - 1) {
        if (x > levelSize) x -= levelSize
        if (levelsList[y][x - 1] == tree) counterTrees++
        x += increaseX; y += increaseY
    }

    return counterTrees
}

fun countTreesAllPatterns(levels: Sequence<String>) : Long {
    var multiplier :Long = 1

    List(4) { it + (it + 1) }.let {
        it.forEach {
            multiplier *= countTrees(levels, it, 1)
        }
        multiplier *= countTrees(levels, it.first(), 2)
    }

    return multiplier
}
