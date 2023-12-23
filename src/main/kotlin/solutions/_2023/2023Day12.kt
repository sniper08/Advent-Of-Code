package solutions._2023

import java.util.Stack

const val DAMAGED = '#'
const val UNKNOWN = '?'
const val OPERATIONAL = '.'

data class IndexCheck(
    val counters: List<Int>,
    val index: Int,
    val lastReduced: Boolean = false
) {
    fun reduceCurrentIndex(): IndexCheck {
        val newCounters = counters.toMutableList()
        newCounters[index] = counters[index] - 1
        return IndexCheck(newCounters, index, lastReduced = true)
    }

    fun currentShouldBeDamaged() = counters[index] > 0 && lastReduced
    fun isCurrentClosed() = counters[index] == 0
    fun next() = IndexCheck(counters.toList(), index + 1)
    fun allClosed() = counters.all { it == 0 }

    fun neededSpaces() = counters.foldIndexed(0) { index, acc, i -> acc + i + if (i > 0 && index < counters.lastIndex) 1 else 0 }
}

data class SpringReading(
    val springs: String,
    val damagedGroups: List<Int>,
) {
    fun correctAmounts(): Long {
        var amount = 0L
        val nextChecks = mutableMapOf<IndexCheck, Long>()
        nextChecks[IndexCheck(damagedGroups, 0)] = 1L

        for (i in springs.indices) {
            val checksForNextIndex = mutableMapOf<IndexCheck, Long>()
            val current = springs[i]

            for (checkEntry in nextChecks) {
                val check = checkEntry.key
                val neededSpaces = check.neededSpaces()
                val leftToCheck = springs.length - i

                if (check.allClosed()) {
                    val damagedInLeft = springs.substring(i, springs.length).count { it == DAMAGED }

                    if (damagedInLeft == 0) {
                        amount += checkEntry.value
                    }
                } else {
                    val checks = when (current) {
                        OPERATIONAL -> {
                            if (check.isCurrentClosed()) {
                                listOf(check.next())
                            } else if (!check.currentShouldBeDamaged()) {
                                listOf(check)
                            } else {
                                null
                            }
                        }
                        DAMAGED -> {
                            if (!check.isCurrentClosed()) {
                                // It means we can still count this one for the current damaged group
                                listOf(check.reduceCurrentIndex())
                            } else {
                                null
                            }
                        }
                        UNKNOWN -> {
                            when {
                                check.currentShouldBeDamaged() -> {
                                    // It means previous was a '#' and this one should be a '#'
                                    listOf(check.reduceCurrentIndex()) // means this one is a '#'
                                }
                                check.isCurrentClosed() -> {
                                    // It means the previous index closed the damaged group so this one should be '.'
                                    listOf(check.next())
                                }
                                leftToCheck > neededSpaces -> {
                                    // It means we have enough space for a '#' and a '.'
                                    listOf(
                                        check.reduceCurrentIndex(),// means this one is a '#'
                                        check // // means this one is a '.'
                                    )
                                }
                                leftToCheck == neededSpaces -> {
                                    // It means we have enough we need to start counting for next group, so this one should be '#'
                                    listOf(check.reduceCurrentIndex())
                                }

                                else -> null
                            }
                        }
                        else -> null
                    }
                    checks?.forEach {
                        val existing = checksForNextIndex[it]

                        if (existing != null) {
                            checksForNextIndex[it] = existing + checkEntry.value
                        } else {
                            checksForNextIndex[it] = checkEntry.value
                        }
                    }
                }
            }
            nextChecks.clear()
            nextChecks.putAll(checksForNextIndex)
        }

        for (check in nextChecks) {
            if (check.key.allClosed()) amount += check.value
        }
        return amount
    }

    fun createCorrectReadings(): Long {
        var count = 0L
        val damagedNeeded = damagedGroups.sum()

        val processingStack = Stack<String>()
        processingStack.push(springs)

        var next = try { processingStack.pop() } catch (e: Exception) { null }

        while (next != null) {
            val withOperational = next.replaceFirst(UNKNOWN, OPERATIONAL)
            var damagedCount = withOperational.count { it == DAMAGED }
            when {
                damagedCount == damagedNeeded -> {
                    val match = Regex("($DAMAGED+)").findAll(withOperational)
                        .mapIndexed { index, betterCandidate -> betterCandidate to damagedGroups[index] }
                        .all { it.first.value.length == it.second }

                    if (match) count++
                }

                damagedCount < damagedNeeded && withOperational.contains(UNKNOWN) -> {
                    if (!processingStack.contains(withOperational)) {
                        processingStack.push(withOperational)
                    }
                }
            }

            val withDamaged = next.replaceFirst(UNKNOWN, DAMAGED)
            damagedCount = withDamaged.count { it == DAMAGED }
            when {
                damagedCount == damagedNeeded -> {
                    val match = Regex("($DAMAGED+)").findAll(withDamaged)
                        .mapIndexed { index, betterCandidate -> betterCandidate to damagedGroups[index] }
                        .all { it.first.value.length == it.second }

                    if (match) count++
                }
                damagedCount < damagedNeeded && withDamaged.contains(UNKNOWN) -> {
                    if (!processingStack.contains(withDamaged)) {
                        processingStack.push(withDamaged)
                    }
                }
            }

            next = try { processingStack.pop() } catch (e: Exception) { null }
        }

        return count
    }

    override fun toString(): String = "$springs $damagedGroups"
}

fun calculateDamageSpringsArrangements(input: Sequence<String>) {
    val springReadings = input
        .map { reading ->
            val split = reading.split(" ")

            SpringReading(
                springs = split[0],
                damagedGroups = split[1].split(",").map { it.toInt() }
            )
        }.toList()

    val sum = springReadings.sumOf {
        println(it)
        val amount = it.correctAmounts()
        //val amountRegex = it.createCorrectReadings()
        println(amount)
       // println(amountRegex)
        amount
    }

    println("The total of correct readings is $sum")
}

fun calculateDamageSpringsArrangementsUnfolded(input: Sequence<String>) {
    val springReadings = input
        .map { reading ->
            val split = reading.split(" ")
            var springs = split[0]
            var damagedGroups = split[1]

            repeat(4) {
                springs += '?'
                springs += split[0]
                damagedGroups += ','
                damagedGroups += split[1]
            }


            SpringReading(
                springs = springs,
                damagedGroups = damagedGroups.split(",").map { it.toInt() }
            )
        }.toList()

    val sum = springReadings.sumOf {
        println(it)
        val amount = it.correctAmounts()
        println(amount)
        amount
    }

    println("The total of correct readings is $sum")
}