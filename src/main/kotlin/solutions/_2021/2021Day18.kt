package solutions._2021

const val PAIR_CLOSER = ']'
const val COMMA = ','

enum class ParentRelation { X, Y }

data class SpecialPair(
    var x: Int? = null,
    var xPair: SpecialPair? = null,
    var y: Int? = null,
    var yPair: SpecialPair? = null,
    var nestedLevel: Int,
    var size: Int = 0,
    var parentPair: SpecialPair? = null,
    var parentRelation: ParentRelation? = null
) {
    override fun toString(): String {
        return "[${x ?: xPair?.toString() ?: 0},${y ?: yPair?.toString() ?: 0}]"
    }

    fun increaseNestedLevel() = apply { nestedLevel++ }
    fun decreaseNestedLevel() = apply { if (nestedLevel > 0) nestedLevel-- }

    operator fun plus(another: SpecialPair): SpecialPair {
        val newParent = SpecialPair(nestedLevel = nestedLevel)

        this.parentPair = newParent
        this.parentRelation = ParentRelation.X
        another.parentPair = newParent
        another.parentRelation = ParentRelation.Y

        newParent.xPair = this
        newParent.yPair = another

        return newParent
    }

    fun findNested(levels: Int): SpecialPair? {
        return if (levels == 0) {
            xPair ?: yPair
        } else {
            xPair?.findNested(levels - 1) ?: yPair?.findNested(levels - 1)
        }
    }

    fun findOver10(): SpecialPair? {
        val xB = x ?: 0
        val yB = y ?: 0

        return if (xB >= 10 || yB >= 10) {
            if (xB >= 10) {
//                if (yPair == null) {
                    this
//                } else {
//                    yPair?.findOver10() ?: this
//                }
            } else {
                if (xPair == null) {
                    this
                } else {
                    xPair?.findOver10() ?: this
                }
            }
        } else {
            xPair?.findOver10() ?: yPair?.findOver10()
        }
    }

    fun split() {
        val xB = x ?: 0
        val yB = y ?: 0

        if (xB >= 10) {
            val base = xB / 2
            val res = xB.mod(2)
            x = null
            xPair = SpecialPair(
                x = base, y = base + res, nestedLevel = nestedLevel + 1, parentPair = this, parentRelation = ParentRelation.X
            )
        } else {
            val base = yB / 2
            val res = yB.mod(2)
            y = null
            yPair = SpecialPair(
                x = base, y = base + res, nestedLevel = nestedLevel + 1, parentPair = this, parentRelation = ParentRelation.Y
            )
        }
    }

    fun explode() {
        val xB = x ?: 0
        val yB = y ?: 0
        when(parentRelation) {
            ParentRelation.X -> {
                parentPair?.xPair = null
                parentPair?.x = 0
                if (parentPair?.y == null) {
                    parentPair?.yPair?.increaseXAfterExplosion(yB)
                } else {
                    parentPair?.y = parentPair?.y?.plus(yB)
                }
                parentPair?.catchExplosion(xB, parentRelation, parentRelation!!)
            }
            ParentRelation.Y -> {
                parentPair?.yPair = null
                parentPair?.y = 0
                if (parentPair?.x == null) {
                    parentPair?.xPair?.increaseYAfterExplosion(xB)
                } else {
                    parentPair?.x = parentPair?.x?.plus(xB)
                }
                parentPair?.catchExplosion(yB, parentRelation, parentRelation!!)
            }
        }
    }

    fun catchExplosion(
        explosion: Int,
        childRelation: ParentRelation?,
        originalRelation: ParentRelation
    ) {
        if (parentPair != null) {
            when {
                childRelation == ParentRelation.X && parentRelation == ParentRelation.X -> {
                    parentPair?.catchExplosion(explosion, parentRelation, originalRelation)
                }
                childRelation == ParentRelation.X && parentRelation == ParentRelation.Y-> {
                    if (parentPair?.x != null) {
                        parentPair?.x = parentPair?.x?.plus(explosion)
                    } else {
                        parentPair?.xPair?.increaseYAfterExplosion(explosion)
                    }
                }
                childRelation == ParentRelation.Y && parentRelation == ParentRelation.Y -> {
                    parentPair?.catchExplosion(explosion, parentRelation, originalRelation)
                }
                childRelation == ParentRelation.Y && parentRelation == ParentRelation.X-> {
                    if (parentPair?.y != null) {
                        parentPair?.y = parentPair?.y?.plus(explosion)
                    } else {
                        parentPair?.yPair?.increaseXAfterExplosion(explosion)
                    }
                }
            }
        } else {

        }
    }

    fun increaseXAfterExplosion(explosion: Int) {
        if (x != null) {
            x = x?.plus(explosion)
        } else {
            xPair?.increaseXAfterExplosion(explosion)
        }
    }

    fun increaseYAfterExplosion(explosion: Int) {
        if (y != null) {
            y = y?.plus(explosion)
        } else {
            yPair?.increaseYAfterExplosion(explosion)
        }
    }

    fun calculateMagnitude(): Long {
        val xMagnitude = 3L * (x?.toLong() ?: xPair?.calculateMagnitude() ?: 0L)
        val yMagnitude = 2L * (y?.toLong() ?: yPair?.calculateMagnitude() ?: 0L)

        return xMagnitude + yMagnitude
    }
}

fun calculateSnailFishMagnitude(input: Sequence<String>) {
    val pairs = input
        .toList()
        .map { SpecialPair(nestedLevel = 0).apply { parseFrom(it) } }

    var sum: SpecialPair = pairs[0]
    for (i in 1 until pairs.size) {
        sum += pairs[i]
        var nested = sum.findNested(3)
        var over10 = sum.findOver10()

        while (nested != null || over10 != null) {
            if (nested != null) {
                nested.explode()
                nested = sum.findNested(3)
                over10 = sum.findOver10()
                continue
            }
            over10?.split()
            nested = sum.findNested(3)
            over10 = sum.findOver10()
        }
    }

    println(sum.toString())
    println(sum.calculateMagnitude())
}

fun calculateSnailFishHighestMagnitude(input: Sequence<String>) {
    val pairs = input
        .toList()
        .map { SpecialPair(nestedLevel = 0).apply { parseFrom(it) } }

    var highestMagnitude = 0L

    for (i in pairs.indices) {
        val pair = pairs[i]
        val toSum = pairs.toMutableList().apply { removeAt(i) }

        for (toSumPair in toSum) {
            val sum = SpecialPair(nestedLevel = 0).apply { parseFrom((pair + toSumPair).toString()) }
            var nested = sum.findNested(3)
            var over10 = sum.findOver10()

            while (nested != null || over10 != null) {
                if (nested != null) {
                    nested.explode()
                    nested = sum.findNested(3)
                    over10 = sum.findOver10()
                    continue
                }
                over10?.split()
                nested = sum.findNested(3)
                over10 = sum.findOver10()
            }
            val magnitude = sum.calculateMagnitude()
            if (magnitude > highestMagnitude) {
                highestMagnitude = magnitude
            }
        }
    }

    println(highestMagnitude)
}

fun SpecialPair.parseFrom(input: String) {
    var x: Int? = null
    var y: Int? = null
    val xPair = SpecialPair(nestedLevel = this.nestedLevel + 1, parentPair = this, parentRelation = ParentRelation.X)
    val yPair = SpecialPair(nestedLevel = this.nestedLevel + 1, parentPair = this, parentRelation = ParentRelation.Y)
    val commaPosition: Int

    if (input[1].isDigit()) {
        commaPosition = input.indexOfFirst { it == COMMA }
        x = input.substring(1, commaPosition).toInt()
    } else {
        xPair.parseFrom(input.drop(1))
        commaPosition = xPair.size + 1
    }

    val remaining = input.substring(commaPosition + 1)
    val closingPosition: Int

    if (remaining[0].isDigit()) {
        closingPosition = remaining.indexOfFirst { it == PAIR_CLOSER }
        y = remaining.substring(0, closingPosition).toInt()
    } else {
        yPair.parseFrom(remaining)
        closingPosition = yPair.size
    }

    this.x = x
    this.y = y
    this.xPair = if (xPair.size > 0) xPair else null
    this.yPair = if (yPair.size > 0) yPair else null
    this.size = commaPosition + closingPosition + 2
}

