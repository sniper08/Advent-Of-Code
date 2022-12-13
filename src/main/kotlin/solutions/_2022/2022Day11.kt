package solutions._2022

import parser.inputCleaner

data class Item(var worry: Long) {
    override fun toString(): String = "$worry"
}

data class Monkey(
    val index: Int,
    val items: MutableList<Item>,
    val operationEx: (Item) -> Unit,
    val testEx: (Item) -> Boolean,
    val divisor: Long,
    val indexWhenTrue: Int,
    val indexWhenFalse: Int,
) {
    var itemsInspected = 0

    fun modulate(item: Item, modulator: Long) {
        item.worry = item.worry % modulator
    }

    fun reduceWorryForItem(item: Item) {
        item.worry = item.worry / 3
    }

    fun throwToOtherMonkey(item: Item, monkeys: List<Monkey>) {
        val throwToMonkeyIndex = if (testEx(item)) indexWhenTrue else indexWhenFalse
        monkeys[throwToMonkeyIndex].items.add(item)
        itemsInspected++
    }

    companion object {
        fun create(monkeyRaw: Sequence<String>, index: Int): Monkey {
            val operationRaw = monkeyRaw.elementAt(2).trim().split(": ", " = ", " ")
            val operationValue = try { operationRaw[4].toLong() } catch (e: Exception) { null }
            val divisor = monkeyRaw.elementAt(3).trim().split(" ")[3].toLong()

            return  Monkey(
                index = index,
                items = mutableListOf<Item>().apply {
                    addAll(
                        monkeyRaw.elementAt(1).split(": ", ", ").drop(1)
                            .map { Item(it.toLong()) }
                    )
                },
                operationEx = { item ->
                    item.worry = when (operationRaw[3]) {
                        "*" -> item.worry.times(operationValue ?: item.worry)
                        "+" -> item.worry.plus(operationValue ?: item.worry)
                        else -> item.worry
                    }
                },
                divisor = divisor,
                testEx = { item -> item.worry % divisor == 0L },
                indexWhenTrue = monkeyRaw.elementAt(4).trim().split(" ")[5].toInt(),
                indexWhenFalse = monkeyRaw.elementAt(5).trim().split(" ")[5].toInt()
            )
        }
    }
}

fun findLevelOfMonkeyBusiness(input: Sequence<String>) {
    val monkeys = input.mapIndexed { i, it -> Monkey.create(inputCleaner(it), index = i) }.toList()

    executeRounds(20, monkeys) {item, monkey ->
        monkey.reduceWorryForItem(item)
    }

    monkeys.printBusinessLevel()
}

fun findLevelOfMonkeyBusinessWithoutReducingWorry(input: Sequence<String>) {
    val monkeys = input.mapIndexed { i, it -> Monkey.create(inputCleaner(it), index = i) }.toList()
    val modulator = monkeys.fold(1L) { acc: Long, monkey: Monkey -> acc * monkey.divisor  }

    executeRounds(10000, monkeys) {item, monkey ->
        monkey.modulate(item, modulator)
    }
    monkeys.printInspectedPerRound(10000)
    monkeys.printBusinessLevel()
}

fun executeRounds(amount: Int, monkeys: List<Monkey>, afterOperation: (Item, Monkey) -> Unit) {
    repeat(amount) {
        monkeys.forEach {
            with(it) {
                items.forEach { item ->
                    operationEx(item)
                    afterOperation(item, this)
                    throwToOtherMonkey(item, monkeys)
                }
                items.clear()
            }
        }
    }
}

fun List<Monkey>.printInspectedPerRound(round: Int) {
    println("Round: $round--------------")
    forEach { println("Monkey ${it.index} inspected ${it.itemsInspected} items") }
    println()
}

fun List<Monkey>.printBusinessLevel() {
    println(
        "\nMonkey business level is: " +
                "${sortedByDescending { it.itemsInspected }
                    .take(2)
                    .map { it.itemsInspected.toLong() }
                    .reduce { acc: Long, i: Long -> acc * i }
                }"
    )
}