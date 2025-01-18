package solutions._2024

import day.Day
import parser.inputCleaner

class Year2024Day24 : Day {

    override val lineJumpsInput: Int = 2

    private val and = "AND"
    private val or = "OR"
    private val xor = "XOR"

    /**
     * Find decimal output of wires starting with a "z"
     */
    override fun part1(input: Sequence<String>): String {
        val knownBitsByWire = createKnownBitsByWire(input = input.first())
        val gatesByWire = createGatesByWire(input = input.last())

        val systemResult = getSystemResult(knownBitsByWire = knownBitsByWire, gatesByWire = gatesByWire)

        return "${systemResult.number}"
    }

    /**
     * Find gates whose output wire is swapped
     */
    override fun part2(input: Sequence<String>): String {
        val knownBitsByWire = createKnownBitsByWire(input = input.first())
        val gatesByWire = createGatesByWire(input = input.last())
        val wiresByGate = gatesByWire
            .map { it.value to it.key }
            .toMap()
            .toMutableMap()

        val systemResultBeforeFixingSwapped = getSystemResult(
            knownBitsByWire = knownBitsByWire.toMutableMap(),
            gatesByWire = gatesByWire.toMutableMap()
        )

        val lastIndex = systemResultBeforeFixingSwapped.binary.lastIndex
        val incorrectOutputWires = mutableSetOf<String>()
        val amountOfSwappedOutputWires = 8

        var lastIndexChecked = 0
        var firstGate = Gate(firstWire = "", secondWire = "", gate = "")

        while (lastIndexChecked <= lastIndex && incorrectOutputWires.size < amountOfSwappedOutputWires) {
            val firstWire: String
            val secondWire: String
            var gateForZVerification: String = xor

            when (lastIndexChecked) {
                0 -> {
                    firstWire = (0).toWireName(prefix = "x")
                    secondWire = (0).toWireName(prefix = "y")
                }
                1 -> {
                    val baseGateVerification = createBaseGatesVerification(
                        firstGate = Gate(
                            firstWire = (0).toWireName(prefix = "x"),
                            secondWire = (0).toWireName(prefix = "y"),
                            gate = and
                        ),
                        secondGate = Gate(
                            firstWire = (1).toWireName(prefix = "x"),
                            secondWire = (1).toWireName(prefix = "y"),
                            gate = xor
                        ),
                        validation = { it.size == 1 },
                        wiresByGate = wiresByGate,
                        gatesByWire = gatesByWire,
                        incorrectOutputWires = incorrectOutputWires
                    )

                    firstWire = baseGateVerification.firstWire
                    secondWire = baseGateVerification.secondWire
                }
                in 2 until lastIndex -> {
                    val firstBaseGatesVerification = createBaseGatesVerification(
                        firstGate = firstGate,
                        secondGate = Gate(
                            firstWire = (lastIndexChecked - 1).toWireName(prefix = "x"),
                            secondWire = (lastIndexChecked - 1).toWireName(prefix = "y"),
                            gate = and
                        ),
                        validation = { it.size > 1 },
                        wiresByGate = wiresByGate,
                        gatesByWire = gatesByWire,
                        incorrectOutputWires = incorrectOutputWires
                    )
                    val secondBaseGateVerification = createBaseGatesVerification(
                        firstGate = Gate(
                            firstWire = firstBaseGatesVerification.firstWire,
                            secondWire = firstBaseGatesVerification.secondWire,
                            gate = or
                        ),
                        secondGate = Gate(
                            firstWire = lastIndexChecked.toWireName(prefix = "x"),
                            secondWire = lastIndexChecked.toWireName(prefix = "y"),
                            gate = xor
                        ),
                        validation = { it.size == 1 },
                        wiresByGate = wiresByGate,
                        gatesByWire = gatesByWire,
                        incorrectOutputWires = incorrectOutputWires
                    )

                    firstWire = secondBaseGateVerification.firstWire
                    secondWire = secondBaseGateVerification.secondWire
                }
                else -> {
                    val baseGateVerification = createBaseGatesVerification(
                        firstGate = firstGate,
                        secondGate = Gate(
                            firstWire = (lastIndexChecked - 1).toWireName(prefix = "x"),
                            secondWire = (lastIndexChecked - 1).toWireName(prefix = "y"),
                            gate = and
                        ),
                        validation = { it.size > 1 },
                        wiresByGate = wiresByGate,
                        gatesByWire = gatesByWire,
                        incorrectOutputWires = incorrectOutputWires
                    )

                    firstWire = baseGateVerification.firstWire
                    secondWire = baseGateVerification.secondWire
                    gateForZVerification = or
                }
            }
            verifyForZ(
                index = lastIndexChecked,
                firstWire = firstWire,
                secondWire = secondWire,
                gateForVerification = gateForZVerification,
                gatesByWire = gatesByWire,
                wiresByGate = wiresByGate,
                incorrectOutputWires = incorrectOutputWires
            )
            firstGate = Gate(
                firstWire = firstWire,
                secondWire = secondWire,
                gate = and
            )

            lastIndexChecked++
        }

        val numberInputInX = knownBitsByWire.findInputFor(prefix = "x").toLong(radix = 2)
        val numberInputInY = knownBitsByWire.findInputFor(prefix = "y").toLong(radix = 2)
        val expectedAddition = numberInputInX + numberInputInY

        val systemResultAfterFixingSwapped = getSystemResult(
            knownBitsByWire = knownBitsByWire,
            gatesByWire = gatesByWire
        )
        val testPassed = systemResultAfterFixingSwapped.number == expectedAddition

        println("Actual = ${systemResultAfterFixingSwapped.number}")
        println("Expected = $expectedAddition")

        println("Passed = $testPassed")

        return incorrectOutputWires
            .sorted()
            .joinToString(",") { it }
    }

    private fun createKnownBitsByWire(input: String): MutableMap<String, Int> {
        val knownBitsByWire = mutableMapOf<String, Int>()

        inputCleaner(input = input)
            .forEach { rawInitialWire ->
                val split = rawInitialWire.split(": ")

                knownBitsByWire[split[0]] = split[1].toInt()
            }

        return knownBitsByWire
    }

    private fun createGatesByWire(input: String): MutableMap<String, Gate> {
        val gatesByWire = mutableMapOf<String, Gate>()

        inputCleaner(input = input).forEach { rawInitialWire ->
            val split = rawInitialWire.split(" -> ", " ")

            gatesByWire[split[3]] = Gate(
                firstWire = split[0],
                secondWire = split[2],
                gate = split[1]
            )
        }

        return gatesByWire
    }

    private fun getSystemResult(
        knownBitsByWire: MutableMap<String, Int>,
        gatesByWire: MutableMap<String, Gate>
    ): NumberRepresentation {
        while (gatesByWire.isNotEmpty()) {
            for (unknownWire in gatesByWire.toMutableMap()) {
                val firstInput = knownBitsByWire[unknownWire.value.firstWire] ?: continue
                val secondInput = knownBitsByWire[unknownWire.value.secondWire] ?: continue

                knownBitsByWire[unknownWire.key] = when (unknownWire.value.gate) {
                    and -> firstInput and secondInput
                    or -> firstInput or secondInput
                    xor -> firstInput xor secondInput
                    else -> continue
                }
                gatesByWire.remove(unknownWire.key)
            }
        }

        val outputInZ = knownBitsByWire
            .entries
            .filter { it.key.startsWith("z") }
            .sortedBy { it.key }
            .reversed()
            .joinToString("") {
                it.value.toString()
            }

        return NumberRepresentation(binary = outputInZ)
    }

    private fun verifyForZ(
        index: Int,
        firstWire: String,
        secondWire: String,
        gatesByWire: MutableMap<String, Gate>,
        wiresByGate: MutableMap<Gate, String>,
        incorrectOutputWires: MutableSet<String>,
        gateForVerification: String = xor
    ) {
        val desiredZ = index.toWireName(prefix = "z")
        val desiredGate = Gate(firstWire = firstWire, secondWire = secondWire, gate = gateForVerification)
        val found = gatesByWire.getValue(desiredZ)

        if (desiredGate != found) {
            swap(
                incorrectWire = desiredZ,
                incorrectGate = found,
                correctWire = wiresByGate.getValue(desiredGate),
                correctGate = desiredGate,
                gatesByWire = gatesByWire,
                wiresByGate = wiresByGate,
                incorrectOutputWires = incorrectOutputWires
            )
        }
    }

    private fun swap(
        incorrectWire: String,
        incorrectGate: Gate,
        correctWire: String,
        correctGate: Gate,
        gatesByWire: MutableMap<String, Gate>,
        wiresByGate: MutableMap<Gate, String>,
        incorrectOutputWires: MutableSet<String>
    ) {
        incorrectOutputWires.add(incorrectWire)
        incorrectOutputWires.add(correctWire)

        gatesByWire[incorrectWire] = correctGate
        gatesByWire[correctWire] = incorrectGate

        wiresByGate[incorrectGate] = correctWire
        wiresByGate[correctGate] = incorrectWire
    }

    private fun createBaseGatesVerification(
        firstGate: Gate,
        secondGate: Gate,
        validation: (List<Gate>) -> Boolean,
        wiresByGate: MutableMap<Gate, String>,
        gatesByWire: MutableMap<String, Gate>,
        incorrectOutputWires: MutableSet<String>
    ): BaseGatesVerification {
        var firstWire = wiresByGate.getValue(firstGate)
        var secondWire = wiresByGate.getValue(secondGate)

        val allGatesWithFirstWire = wiresByGate.keys.filter { it.contains(wire = firstWire) }
        val allGatesWithSecondWire = wiresByGate.keys.filter { it.contains(wire = secondWire) }

        when {
            validation(allGatesWithFirstWire) -> {
                firstWire = swapAndFindCorrectWire(
                    incorrectGate = firstGate,
                    incorrectWire = firstWire,
                    correctPartnerInputWire = secondWire,
                    correctUsingPartnerInputWire = allGatesWithSecondWire.first(),
                    wiresByGate = wiresByGate,
                    gatesByWire = gatesByWire,
                    incorrectOutputWires
                )
            }
            validation(allGatesWithSecondWire) -> {
                secondWire = swapAndFindCorrectWire(
                    incorrectGate = secondGate,
                    incorrectWire = secondWire,
                    correctPartnerInputWire = firstWire,
                    correctUsingPartnerInputWire = allGatesWithFirstWire.first(),
                    wiresByGate = wiresByGate,
                    gatesByWire = gatesByWire,
                    incorrectOutputWires = incorrectOutputWires
                )
            }
        }

        return BaseGatesVerification(firstWire = firstWire, secondWire = secondWire)
    }

    private fun swapAndFindCorrectWire(
        incorrectGate: Gate,
        incorrectWire: String,
        correctPartnerInputWire: String,
        correctUsingPartnerInputWire: Gate,
        wiresByGate: MutableMap<Gate, String>,
        gatesByWire: MutableMap<String, Gate>,
        incorrectOutputWires: MutableSet<String>
    ): String {
        val correctWire = if (correctPartnerInputWire == correctUsingPartnerInputWire.firstWire) {
            correctUsingPartnerInputWire.secondWire
        } else {
            correctUsingPartnerInputWire.firstWire
        }

        swap(
            incorrectWire = incorrectWire,
            incorrectGate = incorrectGate,
            correctWire = correctWire,
            correctGate = gatesByWire.getValue(correctWire),
            gatesByWire = gatesByWire,
            wiresByGate = wiresByGate,
            incorrectOutputWires = incorrectOutputWires
        )

        return correctWire
    }

    private fun Int.toWireName(prefix: String) = prefix + if (this < 10) { "0$this" } else { "$this" }

    private fun Map<String, Int>.findInputFor(prefix: String) = entries
        .filter { it.key.startsWith(prefix) }
        .reversed()
        .joinToString("") { it.value.toString() }

    data class Gate(
        val firstWire: String,
        val secondWire: String,
        val gate: String,
    ) {
        private val list = listOf(firstWire, secondWire, gate).sorted()

        fun contains(wire: String) = firstWire == wire || secondWire == wire

        override fun equals(other: Any?): Boolean {
            if (other !is Gate) return false

            return list == other.list
        }
        override fun hashCode(): Int = list.hashCode()

        override fun toString() = "$firstWire $gate $secondWire ->"
    }

    data class BaseGatesVerification(
        val firstWire: String,
        val secondWire: String,
    )

    data class NumberRepresentation(
        val binary: String,
        val number: Long = binary.toLong(radix = 2)
    )
}