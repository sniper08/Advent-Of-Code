package solutions._2015

const val GAIN = "gain"

data class Person(val name: String)
data class SittingArrangement(val sitting: Set<Person>, val cost: Int) {

    var realCost = 0

    fun calculateRealCost(people: Map<Person, MutableMap<Person, Int>>) {
        val lastWithFirst = people[sitting.last()]?.get(sitting.first()) ?: 0
        val firstWithLast = people[sitting.first()]?.get(sitting.last()) ?: 0

        realCost =  cost + lastWithFirst + firstWithLast
    }

    fun print() {
        println("$sitting} --- Real Happiness --- $realCost}")
    }

}

fun calculateHappinessInTable(input: Sequence<String>) {
    val peopleMap = mutableMapOf<Person, MutableMap<Person, Int>>().withDefault { mutableMapOf() }

    input.forEach {
        val split = it.replace(".", "").split(" ")
        val multiplier = if (split[2] == GAIN) 1 else -1

        (peopleMap.getOrPut(Person(split[0])) { mutableMapOf() }).apply {
            put(Person(split.last()), split[3].toInt() * multiplier)
        }
    }

    calculateActualList(peopleMap)
    calculateListWithMe(peopleMap)
}

fun calculateActualList(people: Map<Person, MutableMap<Person, Int>>) {
    println("\n--------Starting---------")
    people.forEach { println(it) }

    val allSittingArrangements = people.findSittingArrangements().onEach { it.calculateRealCost(people) }

    println("\n--------Max Happiness Starting ---------")
    println(allSittingArrangements.maxByOrNull { it.realCost }?.print())
}

fun calculateListWithMe(people: Map<Person, MutableMap<Person, Int>>) {
    val me = Person("Me")
    val peopleMapWithMe = people.map {
        it.key to it.value.toMutableMap().apply { put(me, 0) }
    }.toMap().toMutableMap().apply {
        val meMap = mutableMapOf<Person, Int>().apply { people.keys.forEach { put(it, 0) } }
        put(me, meMap)
    }

    println("\n--------Starting With me---------")
    peopleMapWithMe.forEach { println(it) }

    val allSittingArrangementsWithMe = peopleMapWithMe
        .findSittingArrangements()
        .onEach { it.calculateRealCost(peopleMapWithMe) }

    println("\n--------Max Happiness Starting With Me ---------")
    println(allSittingArrangementsWithMe.maxByOrNull { it.realCost }?.print())
}

fun Map<Person, MutableMap<Person, Int>>.findSittingArrangements(): List<SittingArrangement> {
    val sittingArrangements = mutableListOf<SittingArrangement>()

    forEach {
        sittingArrangements.addAll(
            SittingArrangement(setOf(it.key), cost = 0).createAllSittingArrangements(this)
        )
    }

    return sittingArrangements
}

fun SittingArrangement.createAllSittingArrangements(
    people: Map<Person, MutableMap<Person, Int>>
): List<SittingArrangement> {
    val allSittingArrangements = mutableListOf<SittingArrangement>()

    people[sitting.last()]?.forEach { nextPerson ->
        val cloneSet = sitting.toMutableSet()
        if (cloneSet.add(nextPerson.key)) {
            val nextCost = cost + nextPerson.value + (people[nextPerson.key]?.get(sitting.last()) ?: 0)
            val nextSittingArrangement = SittingArrangement(cloneSet, nextCost)

            if (cloneSet.size < people.size) {
                allSittingArrangements.addAll(nextSittingArrangement.createAllSittingArrangements(people))
            } else {
                allSittingArrangements.add(nextSittingArrangement)
            }
        }
    }

    return allSittingArrangements
}
