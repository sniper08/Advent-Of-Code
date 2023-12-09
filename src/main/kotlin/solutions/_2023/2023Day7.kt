package solutions._2023

import solutions._2023.Type.*

class EqualHandsException(val a: Hand, val b: Hand) : Exception()

private val JOKER = 'J' to 11

enum class Type(val worth: Int) {
    FIVE_OF_KIND(7),
    FOUR_OF_KIND(6),
    FULL_HOUSE(5),
    THREE_OF_KIND(4),
    TWO_PAIR(3),
    ONE_PAIR(2),
    HIGH_CARD(1)
}

val letterCardsMappings = mapOf(
    'T' to 10,
    JOKER,
    'Q' to 12,
    'K' to 13,
    'A' to 14
)

data class Hand(
    val handString: String,
    val bid: Long,
    val jokerRule: Boolean = false,
    val jokerCount: Int = handString.count { it == JOKER.first }
) {
    private val values: List<Int> = handString.map { if (it.isDigit()) it.digitToInt() else letterCardsMappings.getValue(it) }
    private val type: Type = findType()

    private fun findType(): Type {
        val groups = values.groupBy { it }

        return when (groups.size) {
            5 -> findType5Groups()
            4 -> findType4Groups()
            3 -> findType3Groups(groups)
            2 -> findType2Groups(groups)
            else -> FIVE_OF_KIND // 0 should never happen
        }
    }

    private fun findType5Groups(): Type =
        if (jokerRule && jokerCount > 0) {
            ONE_PAIR
        } else {
            HIGH_CARD
        }

    private fun findType4Groups(): Type =
        if (jokerRule && jokerCount > 0) {
            THREE_OF_KIND
        } else {
            ONE_PAIR
        }

    private fun findType3Groups(groups: Map<Int, List<Int>>): Type {
        val isThereAGroupWith3cards = groups.values.any { it.size == 3 }

        return if (jokerRule && jokerCount > 0) {
            when {
                isThereAGroupWith3cards -> FOUR_OF_KIND
                jokerCount == 2 -> FOUR_OF_KIND
                else -> FULL_HOUSE
            }
        } else {
            if (isThereAGroupWith3cards) {
                THREE_OF_KIND
            } else {
                TWO_PAIR
            }
        }
    }

    private fun findType2Groups(groups: Map<Int, List<Int>>): Type =
        when {
            jokerRule && jokerCount > 0 -> FIVE_OF_KIND
            groups.values.any { it.size == 4 } -> FOUR_OF_KIND
            else -> FULL_HOUSE
        }

    fun isHigherThan(other: Hand): Boolean {
        if (type.worth == other.type.worth) {
            for (i in 0..values.lastIndex) {
                val a = values[i]
                val b = other.values[i]

                if (a != b) {
                    return when {
                        jokerRule && a == JOKER.second -> false
                        jokerRule && b == JOKER.second -> true
                        else -> a > b
                    }
                }
            }
            throw EqualHandsException(this, other)
        } else {
            return type.worth > other.type.worth
        }
    }

    override fun toString() = "[$handString] -> Bid = $bid -> ${type.name.uppercase()}${if (jokerCount > 0) "--> Contains JOKER" else ""}"
}

fun calculateTotalWinningsForCardGame(input: Sequence<String>, jokerRule: Boolean = false) {
    val hands = input
        .map {
            val split = it.split(" ")
            Hand(handString = split.first(), bid = split.last().trim().toLong(), jokerRule)
        }.sortedWith { a, b ->
            if (a.isHigherThan(b)) 1 else -1
        }

    val totalWinnings = hands.foldIndexed(0L) { i, acc, hand ->
        val perHand = hand.bid * (i + 1)
        println("$hand -> $perHand")
        acc + perHand
    }

    println("Total winning for game of cards is $totalWinnings")
}
