package solutions._2024

import Coordinate
import LinearDirection.*
import day.Day
import utils.Cost
import utils.Grid
import utils.GridElement
import java.util.*

class Year2024Day21 : Day {

    /**
     * Calculate the sum of complexities per input after decoding 2 layers
     */
    override fun part1(input: Sequence<String>): String {
        return "${calculateSumOfComplexities(input = input, layers = 2)}"
    }

    /**
     * Calculate the sum of complexities per input after decoding 25 layers
     */
    override fun part2(input: Sequence<String>): String {
        return "${calculateSumOfComplexities(input = input, layers = 25)}"
    }

    private fun calculateSumOfComplexities(input: Sequence<String>, layers: Int): Long {
        val numPad = NumPad()
        val directionPad = DirectionPad()

        return input.sumOf { output ->
            println()
            println(output)

            val firstLayer = numPad.produceInputsFor(output = output)

            //  println("First Layer")
            var currentLayer = firstLayer.map {
                val map = it.toMap()
//                println(map.cost())
//                println(map.printingString())
                map
            }


            repeat(layers) {
                val nextLayer = currentLayer.map { output ->
                    directionPad.produceInputFor(output = output)
                }
                val lowestCostInLayer = nextLayer.minOf { output -> output.cost() }
                val sanitizedLayer = nextLayer
                    .toMutableList()
                    .apply {
                        removeIf { output -> output.cost() > lowestCostInLayer }
                    }

                currentLayer = sanitizedLayer
//                println()
//                println("Layer ${it + 2}")
//                println()
//                currentLayer.forEach { output ->
//                    println(output.cost())
//                    println(output.printingString())
//                }
            }

            val lowestCostInLayer = currentLayer.minOf { outputInLayer -> outputInLayer.cost() }
            val outputNumValue = output.substring(startIndex = 0, endIndex = output.lastIndex).toLong()
            val complexity = lowestCostInLayer * outputNumValue

            println("$lowestCostInLayer * $outputNumValue = $complexity")

            complexity
        }
    }

    private fun Map<KeyPad.KeyPair, Long>.cost() = values.sum()
    private fun Map<KeyPad.KeyPair, Long>.printingString()= entries.joinToString("\n") { it.toString() }

    abstract class KeyPad {
        data class Key(val value: String, override val coordinate: Coordinate) : GridElement
        data class InputRoute(val keys: List<Key>, val input: String) {
            override fun toString(): String = "From ${keys.first().value} to ${keys.last().value} -> $input"
        }
        data class KeyPair(val from: String, val to: String) {
            override fun toString(): String = "($from, $to)"
        }
        data class CheapestMapping(val keyPairs: List<KeyPair>)
        data class Input(val keyPairs: List<KeyPair>, val futureCost: Int) {

            fun toMap() = keyPairs
                .groupingBy { it }
                .eachCount()
                .mapValues { it.value.toLong() }

            override fun toString(): String = "$keyPairs -> $futureCost\n${toMap().entries.joinToString("\n") { it.toString() }}"
        }

        protected abstract val keysYSize: Int
        protected abstract val keysXSize: Int
        protected abstract val keyValueCreator: (Coordinate) -> String
        protected abstract val forbiddenCoordinate: Coordinate
        protected abstract val interestedInFutureCost: Boolean

        protected val keys: Grid<Key> by lazy {
            Grid<Key>(
                ySize = keysYSize,
                xSize = keysXSize
            ) { coordinate ->
                Key(
                    value = keyValueCreator(coordinate),
                    coordinate = coordinate
                )
            }
        }

        private val keyPairsMapToSize = mutableMapOf<KeyPair, Int>()
        private val keyPairsMapTo by lazy {
            val completedInputRoutes = createAllInputRoutes()
            val tempKeyPairsMapTo =  completedInputRoutes.groupBy(
                keySelector = { KeyPair(from = it.keys.first().value, to = it.keys.last().value) },
                valueTransform = { inputRoute ->
                    val completeInput = "A" + inputRoute.input + "A"
                    completeInput
                        .windowed(2, 1)
                        .map {
                            KeyPair(from = it.first().toString(), to = it.last().toString())
                        }
                }
            )

            if (interestedInFutureCost) {
                tempKeyPairsMapTo
                    .toMutableMap()
                    .apply {
                        val uniqueMapping = listOf(listOf(KeyPair(from = "A", to = "A")))
                        put(KeyPair(from = "A", to = "A"), uniqueMapping)
                        put(KeyPair(from = NORTH.arrow.toString(), to = NORTH.arrow.toString()), uniqueMapping)
                        put(KeyPair(from = WEST.arrow.toString(), to = WEST.arrow.toString()), uniqueMapping)
                        put(KeyPair(from = EAST.arrow.toString(), to = EAST.arrow.toString()), uniqueMapping)
                        put(KeyPair(from = SOUTH.arrow.toString(), to = SOUTH.arrow.toString()), uniqueMapping)

                        forEach {
                            keyPairsMapToSize[it.key] = it.value.maxOf { mapping -> mapping.size }
                        }
                    }
            } else {
                tempKeyPairsMapTo
            }
        }

        private val mappingsFutureCost by lazy {
            if (interestedInFutureCost) {
                buildMap<List<KeyPair>, Int> {
                    keyPairsMapTo.forEach {
                        it.value.forEach { mapping ->
                            val futureCost = mapping.sumOf { keyPair ->
                                keyPairsMapToSize.getValue(keyPair)
                            }
                            put(mapping, futureCost)
                        }
                    }
                }
            } else {
                emptyMap()
            }
        }

        protected val keyPairMapsToCheapestMapping by lazy {
            if (interestedInFutureCost) {
                keyPairsMapTo.mapValues {
                    val mappings = it.value

                    if (mappings.size == 1) {
                        CheapestMapping(keyPairs = mappings.first())
                    } else {
                        findCheapestMapping(
                            first = mappings.first(),
                            second = mappings.last()
                        )
                    }
                }
            } else {
                emptyMap()
            }
        }

        private fun createAllInputRoutes(): List<InputRoute> {
            val completedInputRoutes = mutableListOf<InputRoute>()
            val allKeys = keys
                .flatten()
                .filter { it.coordinate != forbiddenCoordinate }
                .map { it.coordinate }

            for (initCoordinate in allKeys) {
                for (destinationCoordinate in allKeys.toMutableList().apply { remove(initCoordinate) }) {
                    val controlGrid = Grid(ySize = keysYSize, xSize = keysXSize) { Cost(cost = Long.MAX_VALUE) }
                    controlGrid.getElement(coordinate = initCoordinate).cost = 0

                    val pq = PriorityQueue<InputRoute> { a, b ->
                        val aCost = controlGrid.getElement(coordinate = a.keys.last().coordinate).cost
                        val bCost = controlGrid.getElement(coordinate = b.keys.last().coordinate).cost

                        when {
                            aCost < bCost -> -1
                            aCost > bCost -> 1
                            else -> 0
                        }
                    }.apply {
                        add(
                            InputRoute(
                                keys = listOf(
                                    keys.getElement(coordinate = initCoordinate)
                                ),
                                input = ""
                            )
                        )
                    }

                    while (pq.isNotEmpty()) {
                        val currentRoute = pq.poll()
                        val lastKey = currentRoute.keys.last()
                        val lastCost = controlGrid.getElement(lastKey.coordinate).cost
                        val completedCost = controlGrid.getElement(coordinate = destinationCoordinate).cost

                        for (nextKey in keys.findLinearNeighbours(coordinate = lastKey.coordinate).values) {
                            if (nextKey == null || nextKey.coordinate == forbiddenCoordinate) continue

                            val nextCost = lastCost + 1
                            val currentNextCost = controlGrid.getElement(coordinate = nextKey.coordinate)

                            if (currentNextCost.cost >= nextCost) {
                                currentNextCost.cost = nextCost
                                val yDiff = nextKey.y - lastKey.y
                                val xDiff = nextKey.x - lastKey.x
                                val nextMoveDirection = when {
                                    yDiff < 0 -> NORTH
                                    yDiff > 0 -> SOUTH
                                    xDiff < 0 -> WEST
                                    xDiff > 0 -> EAST
                                    else -> error("Illegal Move")
                                }
                                val nextInputRoute = InputRoute(
                                    keys = currentRoute.keys + nextKey,
                                    input = currentRoute.input + nextMoveDirection.arrow
                                )

                                if (nextKey.coordinate == destinationCoordinate && nextCost <= completedCost) {
                                    completedInputRoutes.add(nextInputRoute)
                                    continue
                                }

                                pq.add(nextInputRoute)
                            }
                        }
                    }
                }
            }

            return completedInputRoutes
        }

        private fun findCheapestMapping(
            first: List<KeyPair>,
            second: List<KeyPair>,
            firstInputs: List<Input> = emptyList(),
            secondInputs: List<Input> = emptyList()
        ): CheapestMapping {
            val firstInputsToEvaluate = if (firstInputs.isEmpty()) {
                produceInputsFor(output = first)
            } else {
                firstInputs.flatMap { input ->
                    produceInputsFor(output = input.keyPairs)
                }
            }
            val secondInputsToEvaluate = if (secondInputs.isEmpty()) {
                produceInputsFor(output = second)
            } else {
                secondInputs.flatMap { input ->
                    produceInputsFor(output = input.keyPairs)
                }
            }

            val firstMinFutureCost = firstInputsToEvaluate.minOf { it.futureCost }
            val secondMinFutureCost = secondInputsToEvaluate.minOf { it.futureCost }

            return when {
                firstMinFutureCost < secondMinFutureCost -> CheapestMapping(keyPairs = first)
                secondMinFutureCost < firstMinFutureCost -> CheapestMapping(keyPairs = second)
                else -> findCheapestMapping(
                    first = first,
                    second = second,
                    firstInputs = firstInputsToEvaluate,
                    secondInputs = secondInputsToEvaluate
                )
            }
        }

        protected fun produceInputsFor(output: List<KeyPair>): List<Input> {
            val expectedInputs = mutableListOf<Input>()

            for (nextKeyPair in output) {
                val mappings = keyPairsMapTo.getValue(nextKeyPair)

                if (expectedInputs.isEmpty()) {
                    expectedInputs.addAll(
                        mappings.map {
                            val futureMappingCost = if (interestedInFutureCost) mappingsFutureCost.getValue(it) else 0
                            Input(keyPairs = it, futureCost = futureMappingCost)
                        }
                    )
                } else {
                    for (expectedInput in expectedInputs.toList()) {
                        expectedInputs.remove(expectedInput)

                        for (mapping in mappings) {
                            val futureMappingCost = if (interestedInFutureCost) mappingsFutureCost.getValue(mapping) else 0
                            expectedInputs.add(
                                Input(
                                    keyPairs = expectedInput.keyPairs + mapping,
                                    futureCost = expectedInput.futureCost + futureMappingCost
                                )
                            )
                        }
                    }
                }
            }

            return expectedInputs
        }

        fun printLayout() {
            keys.print { it.value }
        }
    }

    class NumPad : KeyPad() {

        override val keysYSize: Int = 4
        override val keysXSize: Int = 3
        override val keyValueCreator: (Coordinate) -> String = { coordinate ->
            if (coordinate.y < 3) {
                val yValue = (3 downTo 1).elementAt(coordinate.y)
                val xValue = (2 downTo 0).elementAt(coordinate.x)

                ((yValue * 3) - xValue).toString()
            } else {
                when (coordinate.x) {
                    0 -> " "
                    1 -> "0"
                    else -> "A"
                }
            }
        }
        override val forbiddenCoordinate = Coordinate(y = keys.yLastIndex(), x = 0)
        override val interestedInFutureCost: Boolean = false

        fun produceInputsFor(output: String): List<Input> {
            val completeOutput = "A$output"
            val encodedOutput = completeOutput
                .windowed(2, 1)
                .map {
                    KeyPair(from = it.first().toString(), to = it.last().toString())
                }

            return produceInputsFor(output = encodedOutput)
        }
    }

    class DirectionPad : KeyPad() {

        override val keysYSize: Int = 2
        override val keysXSize: Int = 3
        override val keyValueCreator: (Coordinate) -> String = { coordinate ->
            if (coordinate.y == 0) {
                when (coordinate.x) {
                    0 -> " "
                    1 -> NORTH.arrow
                    else -> "A"
                }.toString()
            } else {
                when (coordinate.x) {
                    0 -> WEST.arrow
                    1 -> SOUTH.arrow
                    else -> EAST.arrow
                }.toString()
            }
        }
        override val forbiddenCoordinate = Coordinate(y = 0, x = 0)
        override val interestedInFutureCost: Boolean = true

        fun produceInputFor(output: Map<KeyPair, Long>): Map<KeyPair, Long> {
            val expectedInput = mutableMapOf<KeyPair, Long>()

            output.forEach { outputValue ->
                val amount = outputValue.value
                val nextMapping = keyPairMapsToCheapestMapping.getValue(outputValue.key).keyPairs

                for (keyPair in nextMapping) {
                    val existing = expectedInput[keyPair] ?: 0
                    expectedInput[keyPair] = existing + amount
                }
            }

            return expectedInput
        }
    }
}