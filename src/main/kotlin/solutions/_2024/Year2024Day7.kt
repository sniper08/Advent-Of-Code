package solutions._2024

import day.Day

class Year2024Day7 : Day {

    override val year: Int = 2024
    override val day: Int = 7

    /**
     * Sum of the test values of equation that can be proved with + or * as operators
     */
    override fun part1(input: Sequence<String>): String {
        val equations = createEquations(input)

        val sumOfValuesFromEquationsThatCanBeProved = equations.sumOf { equation ->
            val canBeProven = equation.canBeProven(withConcatenation = false)

//            println()
//            equation.evaluations.forEach {
//                println(it)
//            }

            if (canBeProven) equation.testValue else 0L
        }

        return "$sumOfValuesFromEquationsThatCanBeProved"
    }

    /**
     * Sum of the test values of equation that can be proved with + or * or ||(concatenation) as operators
     */
    override fun part2(input: Sequence<String>): String {
        val equations = createEquations(input)

        val sumOfValuesFromEquationsThatCanBeProvedWithConcatenation = equations.sumOf { equation ->
            val canBeProven = equation.canBeProven(withConcatenation = true)

//            println()
//            equation.evaluations.forEach {
//                println(it)
//            }

            if (canBeProven) equation.testValue else 0L
        }

        return "$sumOfValuesFromEquationsThatCanBeProvedWithConcatenation"
    }

    private fun createEquations(input: Sequence<String>) =
        input.map { rawEquation ->
            val split = rawEquation.split(": ")

            Equation(
                testValue = split[0].toLong(),
                components = split[1]
                    .split(" ")
                    .map { it.toLong() }
            )
        }

    private data class Equation(
        val testValue: Long,
        val components: List<Long>
    ) {
        val evaluations = mutableListOf<Evaluation>()

        fun canBeProven(withConcatenation: Boolean): Boolean {
            val firstComponent = components.elementAt(0)

            evaluations.add(
                Evaluation(
                    value = firstComponent,
                    operation = "$firstComponent"
                )
            )

            for (component in components.drop(1)) {
                val currentEvaluations = mutableListOf<Evaluation>().apply { addAll(evaluations) }
                evaluations.clear()

                for (evaluation in currentEvaluations) {
                    evaluations.add(
                        Evaluation(
                            value = evaluation.value + component,
                            operation = evaluation.operation + " + $component"
                        )
                    )
                    evaluations.add(
                        Evaluation(
                            value = evaluation.value * component,
                            operation = evaluation.operation + " * $component"
                        )
                    )
                    if (withConcatenation) {
                        val concatenation = evaluation.value.toString() + component.toString()

                        evaluations.add(
                            Evaluation(
                                value = concatenation.toLong(),
                                operation = evaluation.operation + " || $component"
                            )
                        )
                    }
                }
            }

            return evaluations.any { it.value == testValue }
        }
    }

    private data class Evaluation(
        val value: Long,
        val operation: String
    )
}
