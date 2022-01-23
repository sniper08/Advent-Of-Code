package solutions._2015

import parser.inputCleaner

const val START_MOLECULE = "e"

fun calculateNextMolecules(input: Sequence<String>) {
    val initialMolecule = input.last()
    val transitions = inputCleaner(input.first()).map {
        val split = it.split(" => ")
        split.first() to split.last()
    }.groupBy({it.first}) { it.second }

    transitions.forEach(::println)
    println("Initial Molecule: $initialMolecule")

    val nextMolecules = mutableSetOf<String>()

    for (transition in transitions) {
        nextMolecules.addAll(findNextMoleculesForTransition(initialMolecule, transition))
    }

    nextMolecules.forEach(::println)
    println("Distinct replacements: ${nextMolecules.count()}")
}

fun findNextMoleculesForTransition(initialMolecule: String, transition: Map.Entry<String, List<String>>): Set<String> {
    val nextMolecules = mutableSetOf<String>()

    val indexes = initialMolecule.findIndexesFor(transition.key)

    println("Matcher: ${transition.key} in ${indexes.joinToString()}")

    if (indexes.isNotEmpty()) {
        for (replacement in transition.value) {
            for (index in indexes) {
                val nextMolecule = initialMolecule.replaceRange(index, index + transition.key.length, replacement)
                nextMolecules.add(nextMolecule)
            }
        }
    }

    return nextMolecules
}

fun String.findIndexesFor(other: String) : List<Int> {
    val indexes = mutableListOf<Int>()
    var nextIndex = indexOf(other)
    var nextSubStringStart = nextIndex + other.length

    while (nextIndex in 0..lastIndex) {
        indexes.add(nextIndex)
        nextIndex = substring(nextSubStringStart).indexOf(other)
        if (nextIndex != -1) {
            nextIndex += nextSubStringStart
        }
        nextSubStringStart = nextIndex + other.length
    }

    return indexes
}

data class Transition(val matcher: String, val replace: String)

fun calculateStepsMedicine(input: Sequence<String>) {
    var medicine = input.last().replaceChemistry()
    val transitions = inputCleaner(input.first()).map {
        val split = it.split(" => ")
        Transition(split.first(), split.last().replaceChemistry())
    }.toList()

    println("Size ${medicine.length}")
    println(medicine)

    var steps = 0

    while (medicine != START_MOLECULE) {
        val allIndexes = transitions
            .associateWith { medicine.findIndexesFor(it.replace).toList() }
            .filter { it.value.isNotEmpty() }
        val replace = allIndexes.maxByOrNull { it.value.maxOrNull() ?: 0 }
        val maxIndex = replace!!.value.maxOrNull() ?: 0

        medicine = medicine.replaceRange(maxIndex, maxIndex + replace.key.replace.length, replace.key.matcher)
        steps++

        println("\nReplaced: ${replace.key.replace} => ${replace.key.matcher}")
        println("Size ${medicine.length}")
        println(medicine)
    }

    println("Steps $steps")
}

fun String.replaceChemistry() = replace("Rn","(")
    .replace("Ar",")")
    .replace("Y", ",")

