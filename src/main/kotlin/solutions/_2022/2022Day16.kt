package solutions._2022

import java.util.Stack

const val MINUTES_ALONE = 30
const val MINUTES_WITH_ELEPHANT = 26

data class Valve(
    val name: String,
    val rate: Int,
    val leadsTo: List<String>,
    val valves: MutableList<Valve> = mutableListOf()
) {
    fun canOpen() = rate > 0

    override fun toString(): String = "($name, $rate)"
}

data class TimeData(
    var timeElapsed: Int = 0,
    var releasedPressure: Int = 0,
    var pressureToRelease: Int = 0,
    val opened: List<Valve> = mutableListOf(),
    var lastOpened: Valve? = null
) {
    fun normalizeToTimeLimit(timeLimit: Int) {
        if (timeElapsed < timeLimit) {
            val diff = timeLimit - timeElapsed

            timeElapsed += diff
            releasedPressure += pressureToRelease * diff
        }
    }

    fun openLastAndUpdate(timeLimit: Int) {
        if (timeElapsed < timeLimit && lastOpened == null) {
            timeElapsed++
            releasedPressure += pressureToRelease
            pressureToRelease += opened.last().rate
            lastOpened = opened.last()
        }
    }

    fun clone() = TimeData(timeElapsed, releasedPressure, pressureToRelease, opened, lastOpened)
}

data class Path(val from: String, val to: String)

data class PressureCounter(
    val timeData: TimeData,
    val timeDataElephant: TimeData = TimeData(),
    var releasedPressure: Int = 0,
    val toOpen: List<Valve> = mutableListOf()
) {
    fun findNextNoElephant(toFind: Valve, existingPaths: MutableMap<Path, Int>) = findNext(toFind, timeData.timeElapsed, 0, timeData.opened.last(), null, false, MINUTES_ALONE, existingPaths)

    fun findAllNextWithElephant(toFind: Valve, existingPaths: MutableMap<Path, Int>): List<PressureCounter?> {
        val allFound = mutableListOf<PressureCounter?>()
        val nextPerson = findNext(toFind, timeData.timeElapsed, 0, timeData.opened.last(), null, false, MINUTES_WITH_ELEPHANT, existingPaths)

        if (nextPerson != null) {
            allFound.addAll(
                nextPerson.toOpen.map {
                    nextPerson.findNext(it, nextPerson.timeDataElephant.timeElapsed,0, timeDataElephant.opened.last(), null, true, MINUTES_WITH_ELEPHANT, existingPaths)
                }
            )
        } else {
            allFound.add(findNext(toFind, timeDataElephant.timeElapsed, 0, timeDataElephant.opened.last(), null, true, MINUTES_WITH_ELEPHANT, existingPaths))
        }

        return allFound
    }

    private fun findNext(toFind: Valve, timeOnStart: Int, timeSearching: Int, currentParent: Valve, previousParent: Valve?, forElephant: Boolean = false, timeLimit: Int, existingPaths: MutableMap<Path, Int>) : PressureCounter? {
        if (timeOnStart + timeSearching < timeLimit) {
            val existingPathValue = if (previousParent == null) {
                existingPaths[Path(from = currentParent.name, to = toFind.name)]
            } else {
                null
            }

            if (existingPathValue != null) {
                return if (timeOnStart + existingPathValue < timeLimit) {
                    getUpdated(toFind, timeOnStart, existingPathValue - 1, forElephant)
                } else {
                    null
                }
            } else {
                val adjacent = currentParent.valves.firstOrNull { it == toFind }

                val found = if (adjacent != null) {
                    getUpdated(adjacent, timeOnStart, timeSearching, forElephant)
                } else {
                    val allFound = mutableListOf<PressureCounter?>()

                    for (valve in currentParent.valves) {
                        if (valve != previousParent) {
                            allFound.add(findNext(toFind, timeOnStart, timeSearching + 1, valve, currentParent, forElephant, timeLimit, existingPaths))
                        }
                    }

                    allFound.minByOrNull { (if (forElephant) it?.timeDataElephant?.timeElapsed else it?.timeData?.timeElapsed) ?: Int.MAX_VALUE }
                }

                if (found != null && previousParent == null) {
                    existingPaths[Path(from = currentParent.name, to = toFind.name)] =
                        (if (forElephant) found.timeDataElephant.timeElapsed else found.timeData.timeElapsed) - timeOnStart
                }

                return found
           }
        } else {
            return null
        }
    }

    private fun getUpdated(found: Valve, timeOnStart: Int, timeSearching: Int, forElephant: Boolean): PressureCounter {
        val dataToUpdate = (if (forElephant) timeDataElephant else timeData).let {
            TimeData(
                timeElapsed = timeOnStart + timeSearching + 1,
                releasedPressure = it.releasedPressure + (it.pressureToRelease * (timeSearching + 1)),
                pressureToRelease = it.pressureToRelease,
                opened = it.opened.plusElement(found)
            )
        }

        return PressureCounter(
            timeData = if (forElephant) timeData.clone() else dataToUpdate,
            timeDataElephant = if (forElephant) dataToUpdate else timeDataElephant.clone(),
            toOpen = toOpen.toMutableList().apply { remove(found) }
        )
    }

    fun openLastAndUpdate(withElephant: Boolean) = apply {
        val timeLimit = if (withElephant) {
            timeDataElephant.openLastAndUpdate(MINUTES_WITH_ELEPHANT)
            MINUTES_WITH_ELEPHANT
        } else {
            MINUTES_ALONE
        }

        timeData.openLastAndUpdate(timeLimit)
    }

    fun normalizeToTimeLimit(withElephant: Boolean) {
        val timeLimit = if (withElephant) {
            timeDataElephant.normalizeToTimeLimit(MINUTES_WITH_ELEPHANT)
            MINUTES_WITH_ELEPHANT
        } else {
            MINUTES_ALONE
        }

        timeData.normalizeToTimeLimit(timeLimit)
        releasedPressure = timeData.releasedPressure + timeDataElephant.releasedPressure
    }
}

fun calculateMostPressureReleased(input: Sequence<String>, withElephant: Boolean) {
    val originalValves = createValves(input)
    val allPaths = mutableMapOf<Path, Int>()
    val allFound = mutableListOf(
        PressureCounter(
            timeData = TimeData(opened = listOf(originalValves.first { it.name == "AA" })),
            timeDataElephant = if (withElephant) TimeData(opened = listOf(originalValves.first { it.name == "AA" })) else TimeData(),
            toOpen = originalValves.filter { it.canOpen() }
        )
    )

    val highest: PressureCounter? = allFound.findHighest(null, allPaths, withElephant)

    println()
    println(highest)
}

fun List<PressureCounter>.findHighest(previousHighest: PressureCounter?, allPaths: MutableMap<Path, Int>, withElephant: Boolean): PressureCounter? {
    val stack = Stack<PressureCounter>().apply { addAll(this@findHighest) }
    var highest: PressureCounter? = null

    while (stack.isNotEmpty()) {
        var innerCompleted: PressureCounter? = null
        val current = stack.pop()

        if (current.toOpen.isNotEmpty()) {
            val allFound = mutableListOf<PressureCounter>()

            current.toOpen.forEach { valve ->
                val found = if (withElephant) {
                    current.findAllNextWithElephant(valve, allPaths)
                } else {
                    listOf(current.findNextNoElephant(valve, allPaths))
                }

                if (found.all { it == null }) {
                    innerCompleted = current
                } else {
                    allFound.addAll(found.filterNotNull().onEach { it.openLastAndUpdate(withElephant) })
                }
            }

            if (allFound.isNotEmpty()) {
                innerCompleted = allFound.findHighest(highest, allPaths, withElephant)
            }
        } else {
            innerCompleted = current
        }

        innerCompleted?.let { inner ->
            inner.normalizeToTimeLimit(withElephant)
            println("LAST = ${inner.releasedPressure} ----- HIGHEST = ${highest?.releasedPressure} ---- Person = ${highest?.timeData?.releasedPressure} --- Elephant = ${highest?.timeDataElephant?.releasedPressure} " +
                    "--- ElephantTime = ${highest?.timeDataElephant?.timeElapsed} ---- PersonTime = ${highest?.timeData?.timeElapsed}")
            if (highest == null) {
                highest = inner
            } else {
                val highestPressure = highest?.releasedPressure ?: 0

                if (inner.releasedPressure > highestPressure) {
                    highest = inner
                }
            }
        }
    }

    val previousHighestPressure = previousHighest?.releasedPressure ?: 0
    val highestPressure = highest?.releasedPressure ?: 0

    return if (previousHighestPressure > highestPressure) {
        previousHighest
    } else {
        highest
    }
}

fun createValves(input: Sequence<String>) : List<Valve> =
    input
        .toList()
        .map {
            val raw = it.split(" ", "=", "; ", ", ")
            Valve(name = raw[1], rate = raw[5].toInt(), leadsTo = raw.subList(10, raw.size))
        }.let { originalValves ->
            originalValves.onEach { valve ->
                for (name in valve.leadsTo) {
                    valve.valves.add(originalValves.first { it.name == name })
                }
            }
        }



