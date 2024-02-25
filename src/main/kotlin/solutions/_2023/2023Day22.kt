package solutions._2023

data class Brick(
    val index: Int,
    val xSnapshotRange: IntRange,
    val ySnapshotRange: IntRange,
    var zSnapshotRange: IntRange
) {
    fun overlaps(other: Brick): Boolean =
        xSnapshotRange.overlap(other.xSnapshotRange)
                && ySnapshotRange.overlap(other.ySnapshotRange)
                && zSnapshotRange.overlap(other.zSnapshotRange)
}

fun calculateSumOfBricksToDisintegrate(input: Sequence<String>) {
    val brickSupportsBricks = mutableMapOf<Int, MutableSet<Int>>()
    val brickSupportedByBricks = mutableMapOf<Int, MutableSet<Int>>()
    val snapShot = input
        .toList()
        .toSet()
        .mapIndexed { i, it ->
            val split = it.split("~", ",")
            brickSupportsBricks[i] = mutableSetOf()
            brickSupportedByBricks[i] = mutableSetOf()

            val firstX = split[0].toInt()
            val secondX = split[3].toInt()
            val firstY = split[1].toInt()
            val secondY = split[4].toInt()
            val firstZ = split[2].toInt()
            val secondZ = split[5].toInt()

            Brick(
                index = i,
                xSnapshotRange = buildRange(firstX, secondX),
                ySnapshotRange = buildRange(firstY, secondY),
                zSnapshotRange = buildRange(firstZ, secondZ)
            )
        }.sortedBy { it.zSnapshotRange.first }

    val settled = mutableListOf<Brick>()

    for (falling in snapShot) {
        if (falling.zSnapshotRange.first == 1) {
            settled.add(falling)
        } else {
            var currentZ = falling.zSnapshotRange
            val supports = mutableListOf<Brick>()

            while (supports.isEmpty() && falling.zSnapshotRange.first > 1) {
                currentZ = falling.zSnapshotRange
                falling.zSnapshotRange = (falling.zSnapshotRange.first - 1)..<falling.zSnapshotRange.last
                supports.addAll(settled.filter { it.overlaps(falling) })
            }

            if (supports.isEmpty()) {
                settled.add(falling)
            } else {
                falling.zSnapshotRange = currentZ
                for (support in supports) {
                    brickSupportsBricks[support.index]?.add(falling.index)
                    brickSupportedByBricks[falling.index]?.add(support.index)
                }
                settled.add(falling)
            }
        }
    }

//    println()
//    settled.groupBy { it.zSnapshotRange.first }.forEach { println(it) }

    val (canBeDisintegrated, cantBeDisintegrated) = brickSupportsBricks.entries.partition {
        if (it.value.isEmpty()) {
            true
        } else {
            var canBeDisintegrated = false

            for (supported in it.value) {
                val supportingBricks = brickSupportedByBricks.getValue(supported).toList().toMutableList()
                supportingBricks.remove(it.key)
                canBeDisintegrated = supportingBricks.isNotEmpty()
                if (!canBeDisintegrated) break
            }

            canBeDisintegrated
        }
    }

//    println()
//    println("Can be disintegrated: ${canBeDisintegrated.size}")

    println()
    brickSupportsBricks.entries.forEach { println(it) }

    println()
    brickSupportedByBricks.entries.forEach { println(it) }

    val wouldFall = cantBeDisintegrated.associate { base ->
        val alreadyFallen = mutableSetOf<Int>(base.key)
        val toEvaluate = mutableSetOf<Int>().apply { addAll(base.value) }

        while (toEvaluate.isNotEmpty()) {
            val nextToEvaluate = mutableSetOf<Int>()

            for (possibleFalling in toEvaluate) {
                val supporting = brickSupportedByBricks.getValue(possibleFalling)
                val diff = supporting - alreadyFallen

                if (diff.isEmpty()) {
                    alreadyFallen.add(possibleFalling)
                    nextToEvaluate.addAll(brickSupportsBricks.getValue(possibleFalling))
                }
            }

            toEvaluate.clear()
            toEvaluate.addAll(nextToEvaluate)
        }

        base.key to alreadyFallen.drop(1)
    }

    println()
    wouldFall.entries.forEach { println(it) }

    println("Would fall total = ${wouldFall.entries.sumOf { it.value.size } }")
}

fun buildRange(first: Int, second: Int) =
    when {
        first > second -> second..first
        else -> first..second
    }

fun IntRange.overlap(other: IntRange) = this.first in other
        || other.first in this
        || this.last in other
        || other.last in this