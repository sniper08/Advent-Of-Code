package solutions._2023

const val ROUND = 'O'
const val CUBE = '#'

data class Platform(val rockArray: Map<Int, MutableSet<Int>>) {

    fun loadOnNorthBeam(): Int {
        val size = rockArray.size

        return rockArray.entries.sumOf {
            it.value.size * (size - it.key)
        }
    }

    override fun toString(): String = rockArray.toString()
}

data class PlatformData(
    val platform: Platform,
    val cubeArrayHorizontal: Map<Int, Set<Int>>,
    val cubeArrayVertical: Map<Int, Set<Int>>
)

data class PlatformAnalysis(
    val loadOnNorthBeam: Int,
    val inSteps: MutableSet<Int>
) {
    override fun toString(): String = "$loadOnNorthBeam -> $inSteps"
}

fun createPlatform(input: Sequence<String>): PlatformData {
    val rockArray = mutableMapOf<Int, MutableSet<Int>>()
    val cubeArrayHorizontal = mutableMapOf<Int, MutableSet<Int>>()
    val cubeArrayVertical = mutableMapOf<Int, MutableSet<Int>>()

    input.forEachIndexed { y, line ->
        rockArray[y] = mutableSetOf()
        cubeArrayHorizontal[y] = mutableSetOf()
        line.forEachIndexed { x, c ->
            when (c) {
                ROUND -> {
                    val inRockArray = rockArray.getOrDefault(y, mutableSetOf()).apply { add(x) }
                    rockArray[y] = inRockArray
                }
                CUBE -> {
                    val inHorizontal = cubeArrayHorizontal.getOrDefault(y, mutableSetOf()).apply { add(x) }
                    cubeArrayHorizontal[y] = inHorizontal

                    val inVertical = cubeArrayVertical.getOrDefault(x, mutableSetOf()).apply { add(y) }
                    cubeArrayVertical[x] = inVertical
                }
            }
        }
    }

    return PlatformData(Platform(rockArray), cubeArrayHorizontal, cubeArrayVertical)
}

fun calculateLoadOnNorthSupportBeams(input: Sequence<String>) {
    val platformData = createPlatform(input)
    val nextNorth = tiltNorth(platformData.platform.rockArray, platformData.cubeArrayVertical)

    println("The load on the north beam is ${nextNorth.loadOnNorthBeam()}")
}

fun calculateLoadOnNorthSupportBeamsAfterCycles(input: Sequence<String>) {
    val platformData = createPlatform(input)

    var last: Platform = platformData.platform
    val allFound = mutableMapOf<String, PlatformAnalysis>()

    var found = false
    var cycle = 1

    while(!found) {
        val nextNorth = tiltNorth(last.rockArray, platformData.cubeArrayVertical)
        val nextWest = tiltWest(nextNorth.rockArray, platformData.cubeArrayHorizontal)
        val nextSouth = tiltSouth(nextWest.rockArray, platformData.cubeArrayVertical)
        last = tiltEast(nextSouth.rockArray, platformData.cubeArrayHorizontal, platformData.cubeArrayVertical.keys.max() + 1)

        val key = last.toString()
        val loadOnNorthBeam = last.loadOnNorthBeam()

        val existing = allFound[key]

        if (existing != null) {
            existing.inSteps.add(cycle)
            found = true
            cycle = existing.inSteps.first()
        } else {
            allFound[key] = PlatformAnalysis(loadOnNorthBeam, mutableSetOf(cycle))
            cycle++
        }
    }

   // allFound.forEach { println(it.value) }

    println("Loop started in cycle $cycle")

    val realCycleToFind = 1000000000
    val sanitizedCycleToFind = realCycleToFind - cycle

    println("Cycle to find $realCycleToFind")
    println("Sanitized Cycle to find $sanitizedCycleToFind")

    val modRange = allFound.size - (cycle - 1)
    val index = (sanitizedCycleToFind % modRange) + (cycle - 1)

    println("The load on the north beam after $realCycleToFind cycles is ${allFound.values.elementAt(index.toInt()).loadOnNorthBeam}")

//    val platformData2 = createPlatform(input)
//    var last2: Platform = platformData2.platform
//    val allFound2 = mutableMapOf<String, PlatformAnalysis>()
//
//    repeat (realCycleToFind) {
//        val nextNorth = tiltNorth(last2.rockArray, platformData2.cubeArrayVertical)
//        val nextWest = tiltWest(nextNorth.rockArray, platformData2.cubeArrayHorizontal)
//        val nextSouth = tiltSouth(nextWest.rockArray, platformData2.cubeArrayVertical)
//        last2 = tiltEast(nextSouth.rockArray, platformData2.cubeArrayHorizontal, platformData2.cubeArrayVertical.keys.max() + 1)
//
//        val key = last2.toString()
//        val loadOnNorthBeam = last2.loadOnNorthBeam()
//
//        val existing = allFound2[key]
//
//        if (existing != null) {
//            existing.inSteps.add(it + 1)
//        } else {
//            allFound2[key] = PlatformAnalysis(loadOnNorthBeam, mutableSetOf(it + 1))
//        }
//    }
//
//    allFound2.forEach { println("${it.value.loadOnNorthBeam}" + "->" + it.value.inSteps.last()) }
}

private fun tiltNorth(
    rockArray: Map<Int, MutableSet<Int>>,
    cubeArrayVertical: Map<Int, Set<Int>>
): Platform {
    for (y in 1..<rockArray.keys.size) {
        val key = rockArray.keys.elementAt(y)
        val rocks = rockArray.getValue(key)
        val toRemove = mutableSetOf<Int>()

        for (x in 0..< rocks.size) {
            val rock = rocks.elementAt(x)
            val cubeVertical = cubeArrayVertical[rock]
            val lastCubeVertical = cubeVertical?.takeWhile { it < key }?.lastOrNull() ?: -1

            val lastIndex = when {
                lastCubeVertical == -1 -> {
                    val lastRockVertical = rockArray.entries.takeWhile { it.value.contains(rock) }.lastOrNull()?.key ?: -1
                    if (lastRockVertical + 1 < key) lastRockVertical + 1 else null
                }

                lastCubeVertical + 1 == key -> null
                lastCubeVertical + 1 < key -> {
                    val lastRockVertical = rockArray.entries.drop(lastCubeVertical + 1).takeWhile { it.value.contains(rock) }.lastOrNull()?.key ?: -1

                    if (lastRockVertical < 0) {
                        lastCubeVertical + 1
                    } else {
                        if (lastRockVertical + 1 < key) lastRockVertical + 1 else null
                    }
                }

                else -> null
            }
            if (lastIndex != null) {
                rockArray[lastIndex]?.add(rock)
                toRemove.add(rock)
            }
        }
        rocks.removeAll(toRemove)
    }
    return Platform(
        buildMap {
            rockArray.forEach {
                put(it.key, it.value.sorted().toMutableSet())
            }
        }
    )
}

private fun tiltWest(
    rockArray: Map<Int, MutableSet<Int>>,
    cubeArrayHorizontal: Map<Int, Set<Int>>
): Platform {
    for (y in 0..< rockArray.keys.size) {
        val key = rockArray.keys.elementAt(y)
        val rocks = rockArray.getValue(key)
        val iterable = rocks.toList()
        val cubeHorizontal = cubeArrayHorizontal[y]

        for (rock in iterable) {
            val lastCubeHorizontal = cubeHorizontal?.takeWhile { it < rock }?.lastOrNull() ?: -1

            val lastIndex = when {
                lastCubeHorizontal == -1 -> {
                    val lastRockHorizontal = rocks.sorted().takeWhile { it < rock }.lastOrNull() ?: -1
                    if (lastRockHorizontal + 1 < rock) lastRockHorizontal + 1 else null
                }
                lastCubeHorizontal + 1 == rock -> null
                lastCubeHorizontal + 1 < rock -> {
                    val lastRockHorizontal = rocks.sorted().dropWhile { it <= lastCubeHorizontal }.takeWhile { it < rock }.lastOrNull() ?: -1

                    if (lastRockHorizontal < 0) {
                        lastCubeHorizontal + 1
                    } else {
                        if (lastRockHorizontal + 1 < rock) lastRockHorizontal + 1 else null
                    }
                }
                else -> null
            }
            if (lastIndex != null) {
                rocks.add(lastIndex)
                rocks.remove(rock)
            }
        }
    }
    return Platform(
        buildMap {
            rockArray.forEach {
                put(it.key, it.value.sorted().toMutableSet())
            }
        }
    )
}

private fun tiltSouth(
    rockArray: Map<Int, MutableSet<Int>>,
    cubeArrayVertical: Map<Int, Set<Int>>
): Platform {
    for (y in rockArray.keys.size - 2 downTo 0) {
        val key = rockArray.keys.elementAt(y)
        val rocks = rockArray.getValue(key)
        val toRemove = mutableSetOf<Int>()

        for (x in 0..< rocks.size) {
            val rock = rocks.elementAt(x)
            val cubeVertical = cubeArrayVertical[rock]
            val lastCubeVertical = cubeVertical?.reversed()?.takeWhile { it > key }?.lastOrNull() ?: rockArray.size

            val lastIndex = when {
                lastCubeVertical == rockArray.size -> {
                    val lastRockVertical = rockArray.entries.reversed().takeWhile { it.value.contains(rock) }.lastOrNull()?.key ?: rockArray.size
                    if (lastRockVertical - 1 > key) lastRockVertical - 1 else null
                }
                lastCubeVertical - 1 == key -> null
                lastCubeVertical - 1 > key -> {
                    val lastRockVertical = rockArray.entries.reversed().drop((rockArray.size - 1) - (lastCubeVertical - 1)).takeWhile { it.value.contains(rock) }.lastOrNull()?.key ?: rockArray.size

                    if (lastRockVertical == rockArray.size) {
                        lastCubeVertical - 1
                    } else {
                        if (lastRockVertical - 1 > key) lastRockVertical - 1 else null
                    }
                }
                else -> null
            }
            if (lastIndex != null) {
                rockArray[lastIndex]?.add(rock)
                toRemove.add(rock)
            }
        }
        rocks.removeAll(toRemove)
    }
    return Platform(
        buildMap {
            rockArray.forEach {
                put(it.key, it.value.sorted().toMutableSet())
            }
        }
    )
}

private fun tiltEast(
    rockArray: Map<Int, MutableSet<Int>>,
    cubeArrayHorizontal: Map<Int, Set<Int>>,
    highestX: Int
): Platform {
    for (y in 0..< rockArray.keys.size) {
        val key = rockArray.keys.elementAt(y)
        val rocks = rockArray.getValue(key)
        val iterable = rocks.toList().reversed()
        val cubeHorizontal = cubeArrayHorizontal[y]?.reversed()

        for (rock in iterable) {
            val lastCubeHorizontal = cubeHorizontal?.takeWhile { it > rock }?.lastOrNull() ?: highestX

            val lastIndex = when {
                lastCubeHorizontal == highestX -> {
                    val lastRockHorizontal = rocks.sorted().reversed().takeWhile { it > rock }.lastOrNull() ?: highestX
                    if (lastRockHorizontal - 1 > rock) lastRockHorizontal - 1 else null
                }
                lastCubeHorizontal - 1 == rock -> null
                lastCubeHorizontal - 1 > rock -> {
                    val lastRockHorizontal = rocks.sorted().reversed().dropWhile { it >= lastCubeHorizontal }.takeWhile { it > rock }.lastOrNull() ?: highestX

                    if (lastRockHorizontal == highestX) {
                        lastCubeHorizontal - 1
                    } else {
                        if (lastRockHorizontal - 1 > rock) lastRockHorizontal - 1 else null
                    }
                }
                else -> null
            }
            if (lastIndex != null) {
                rocks.add(lastIndex)
                rocks.remove(rock)
            }
        }
    }
    return Platform(
        buildMap {
            rockArray.forEach {
                put(it.key, it.value.sorted().toMutableSet())
            }
        }
    )
}