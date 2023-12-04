package solutions._2023

import kotlin.math.max
import kotlin.math.pow

data class Card(
    val winningNumbers: Set<Int>,
    val foundNumbers: Set<Int>,
    val matchingCards: Int,
    val points: Int,
    var instances: Int = 1
)

fun createCard(input: String, instances: Int): Card {
    val firstSplit = input.split(": ", " | ")
    val winningNumbers = firstSplit[1].split(" ").mapNotNull { if (it.isBlank()) null else it.trim().toInt() }.toSet()
    val foundNumbers = firstSplit[2].split(" ").mapNotNull { if (it.isBlank()) null else it.trim().toInt() }.toSet()

    var matchingCards = 0

    for (n in foundNumbers) {
        if (winningNumbers.contains(n)) matchingCards++
    }

    return Card(winningNumbers, foundNumbers, matchingCards, (2.0).pow(matchingCards - 1).toInt(), instances)
}

fun calculateScratchCardsWorth(input: Sequence<String>) {
    val cardsWorth = input.sumOf {
        createCard(it, 1)
            .let { card ->
                println(card)
                card.points
            }
    }

    println("The card are worth $cardsWorth points")
}

fun countScratchCards(input: Sequence<String>) {
    val cardInstances = mutableMapOf<Int, Int>()
    var total = 0L

    for ((i, cardInput) in input.withIndex()) {
        cardInstances[i] = cardInstances.getOrDefault(i, 0) + 1
        val card = createCard(cardInput, cardInstances.getValue(i))
        total += card.instances

        if (card.matchingCards > 0) {
            for (n in 1..card.matchingCards) {
                val nextIndex = i + n
                cardInstances[nextIndex] = cardInstances.getOrDefault(nextIndex, 0) + card.instances
            }
        }

        println(card)
    }

    println("The total amount of cards is $total")
}