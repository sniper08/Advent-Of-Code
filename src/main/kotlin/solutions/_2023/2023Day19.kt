package solutions._2023

import parser.inputCleaner
import solutions._2023.PartType.*
import solutions._2023.WorkFlowDestination.*
import kotlin.math.pow

const val JSON_OPEN_OBJECT = '{'
const val JSON_CLOSE_OBJECT = '}'

enum class PartType { X, M, A, S;

    companion object {
        fun from(c: Char) = when (c.lowercase()) {
            X.name.lowercase() -> X
            M.name.lowercase() -> M
            A.name.lowercase() -> A
            S.name.lowercase() -> S
            else -> throw Exception("Not valid")
        }
    }
}

sealed class WorkFlowDestination(open val destinationName: String) {
    data object Accepted : WorkFlowDestination("A") {
        override fun toString(): String = super.toString()
    }
    data object Rejected : WorkFlowDestination("R") {
        override fun toString(): String = super.toString()
    }
    data class Continue(override val destinationName: String) : WorkFlowDestination(destinationName) {
        override fun toString(): String = super.toString()
    }

    companion object {
        fun from(name: String) = when(name) {
            Accepted.destinationName -> Accepted
            Rejected.destinationName -> Rejected
            else -> Continue(name)
        }
    }

    override fun toString(): String = "[$destinationName]"
}

data class MachinePart(
    val x: Long,
    val m: Long,
    val a: Long,
    val s: Long,
) {
    fun rating() = x + m + a + s
}

interface Rule {
    val partType: PartType?
    val destination: WorkFlowDestination
    val comparisonValue: Long
    fun evaluate(value: Long): Boolean
}

data class GreaterThan(
    override val partType: PartType?,
    override val comparisonValue: Long,
    override val destination: WorkFlowDestination
) : Rule {
    override fun evaluate(value: Long) = value > comparisonValue
}

data class LowerThan(
    override val partType: PartType?,
    override val comparisonValue: Long,
    override val destination: WorkFlowDestination
) : Rule {
    override fun evaluate(value: Long) = value < comparisonValue
}

data class EmptyRule(override val destination: WorkFlowDestination) : Rule {
    override val comparisonValue: Long = 0
    override val partType: PartType? = null

    override fun evaluate(value: Long) = true
}

data class Workflow(
    val destination: WorkFlowDestination,
    val rules: List<Rule>,
    var needsEvaluation: Boolean = false,
    val ranges: MutableList<LongRange> = MutableList(4) { 0L..0 }
) {
    fun nextWorkFlowDestination(part: MachinePart): WorkFlowDestination =
        rules.first { rule ->
            when (rule.partType) {
                X -> rule.evaluate(part.x)
                M -> rule.evaluate(part.m)
                A -> rule.evaluate(part.a)
                S -> rule.evaluate(part.s)
                else -> true
            }
        }.destination

    fun reset() {
        needsEvaluation = false
        ranges.clear()
    }

    override fun toString(): String = if (ranges.isEmpty()) {
        "DONE"
    } else {
        "${ranges.joinToString(" / ") { it.toString() }} = ${ranges.totalRating()}"
    }
}

fun List<LongRange>.totalRating() = fold(1.0) { acc, longRange ->
    acc * longRange.count().toDouble()
}.toLong()

fun MutableList<LongRange>.replaceRangeAtIndex(
    rangeIndex: Int,
    newRange: LongRange,
    reference: List<LongRange>
) = apply {
    this[0] = if (rangeIndex == 0) newRange else reference[0]
    this[1] = if (rangeIndex == 1) newRange else reference[1]
    this[2] = if (rangeIndex == 2) newRange else reference[2]
    this[3] = if (rangeIndex == 3) newRange else reference[3]
}

fun createWorkFlows(input: Sequence<String>) = buildMap<WorkFlowDestination, Workflow> {
    input.forEach {
        val firstSplit = it.split(JSON_OPEN_OBJECT)
        val destination = Continue(firstSplit[0])

        val secondSplit = firstSplit[1].dropLast(1).split(",")
            .map { rule -> rule.split(":") }
        val workflow = Workflow(
            destination = destination,
            rules = secondSplit.map { rule ->
                if (rule.size == 1) {
                    EmptyRule(WorkFlowDestination.from(rule[0]))
                } else {
                    val partType = PartType.from(rule[0].first())
                    val comparisonValue = rule[0].drop(2).toLong()
                    val nextDestination = WorkFlowDestination.from(rule[1])

                    when (rule[0][1]) {
                        '<' -> LowerThan(partType, comparisonValue, nextDestination)
                        '>' -> GreaterThan(partType, comparisonValue, nextDestination)
                        else -> throw Exception("Operation not valid")
                    }
                }
            }
        )

        put(destination, workflow)
    }
}

fun createParts(input: Sequence<String>) = input
    .toList()
    .map {
        val trimmed = it.drop(1).dropLast(1)
        val split = trimmed.split(",")
            .map { value -> value.split("=") }

        MachinePart(
            x = split[0][1].toLong(),
            m = split[1][1].toLong(),
            a = split[2][1].toLong(),
            s = split[3][1].toLong()
        )
    }

fun calculateMachinePartsRatings(input: Sequence<String>) {
    val workflows = createWorkFlows(inputCleaner(input.first()))
    val parts = createParts(inputCleaner(input.last()))
    val initialWorkflow = workflows.getValue(Continue("in"))

    val acceptedParts = mutableSetOf<MachinePart>()

    parts.forEach { part ->
        var nextDestination = initialWorkflow.destination
        var nextWorkFlow: Workflow? = initialWorkflow

        val workflowDestinations = mutableListOf<WorkFlowDestination>()

        while (nextDestination is Continue && nextWorkFlow != null) {
            nextDestination = nextWorkFlow.nextWorkFlowDestination(part)
            nextWorkFlow = workflows[nextDestination]
            workflowDestinations.add(nextDestination)
        }

        if (nextDestination is Accepted){
            acceptedParts.add(part)
        }
        println("$part -> ${workflowDestinations.joinToString(separator = " -> ") { it.toString() }}")
    }

    val totalSum = acceptedParts.sumOf { it.rating() }
    println("The total sum of ratings is $totalSum")
}

fun calculateAcceptedRatingCombinations(input: Sequence<String>) {
    val threshold = 4000
    val totalPossible = threshold.toDouble().pow(4).toLong()
    val workflows = createWorkFlows(inputCleaner(input.first()))
    workflows.getValue(Continue("in")).also {
        it.needsEvaluation = true
        it.ranges[0] = 1L..threshold
        it.ranges[1] = it.ranges[0]
        it.ranges[2] = it.ranges[0]
        it.ranges[3] = it.ranges[0]
    }

    var accepted = 0L
    var rejected = 0L

    var needEvaluationWorkflows = workflows.values.filter { it.needsEvaluation }

    while (needEvaluationWorkflows.isNotEmpty()) {
        needEvaluationWorkflows.forEach { workflow ->
            workflow.rules
                .forEach { rule ->
                    when (rule) {
                        is GreaterThan -> {
                            val rangeIndex = when (rule.partType) {
                                X -> 0
                                M -> 1
                                A -> 2
                                S -> 3
                                else -> -1
                            }

                            if (rangeIndex > -1) {
                                val range = workflow.ranges[rangeIndex]
                                val lowerRange = range.first..rule.comparisonValue
                                val higherRange = (rule.comparisonValue + 1)..range.last

                                when (rule.destination) {
                                    is Accepted -> {
                                        accepted += workflow.ranges.toMutableList()
                                            .replaceRangeAtIndex(rangeIndex, higherRange, workflow.ranges)
                                            .totalRating()

                                        workflow.ranges[rangeIndex] = lowerRange
                                    }
                                    is Rejected -> {
                                        rejected += workflow.ranges.toMutableList()
                                            .replaceRangeAtIndex(rangeIndex, higherRange, workflow.ranges)
                                            .totalRating()

                                        workflow.ranges[rangeIndex] = lowerRange
                                    }
                                    is Continue -> {
                                        workflows.getValue(rule.destination).let {
                                            it.needsEvaluation = true
                                            it.ranges.replaceRangeAtIndex(rangeIndex, higherRange, workflow.ranges)
                                        }
                                        workflow.ranges[rangeIndex] = lowerRange
                                    }
                                }
                            }
                        }
                        is LowerThan -> {
                            val rangeIndex = when (rule.partType) {
                                X -> 0
                                M -> 1
                                A -> 2
                                S -> 3
                                else -> -1
                            }

                            if (rangeIndex > -1) {
                                val range = workflow.ranges[rangeIndex]
                                val lowerRange = range.first..<rule.comparisonValue
                                val higherRange = rule.comparisonValue..range.last

                                when (rule.destination) {
                                    is Accepted -> {
                                        accepted += workflow.ranges.toMutableList()
                                            .replaceRangeAtIndex(rangeIndex, lowerRange, workflow.ranges)
                                            .totalRating()

                                        workflow.ranges[rangeIndex] = higherRange
                                    }
                                    is Rejected -> {
                                        rejected += workflow.ranges.toMutableList()
                                            .replaceRangeAtIndex(rangeIndex, lowerRange, workflow.ranges)
                                            .totalRating()

                                        workflow.ranges[rangeIndex] = higherRange
                                    }
                                    is Continue -> {
                                        workflows.getValue(rule.destination).let {
                                            it.needsEvaluation = true
                                            it.ranges.replaceRangeAtIndex(rangeIndex, lowerRange, workflow.ranges)
                                        }
                                        workflow.ranges[rangeIndex] = higherRange
                                    }
                                }
                            }
                        }
                        is EmptyRule -> {
                            when (rule.destination) {
                                is Accepted -> accepted += workflow.ranges.totalRating()
                                is Rejected -> rejected += workflow.ranges.totalRating()
                                is Continue -> {
                                    workflows.getValue(rule.destination).let {
                                        it.needsEvaluation = true
                                        it.ranges[0] = workflow.ranges[0]
                                        it.ranges[1] = workflow.ranges[1]
                                        it.ranges[2] = workflow.ranges[2]
                                        it.ranges[3] = workflow.ranges[3]
                                    }
                                }
                            }
                            workflow.reset()
                        }
                    }
                }
        }
        needEvaluationWorkflows = workflows.values.filter { it.needsEvaluation }
        println()
        workflows.forEach { println(it) }
        println()
        println("ACCEPTED -> $accepted")
        println("REJECTED -> $rejected")
        println("CONTROL -> ${accepted + rejected}")
        println("POSSIBLE -> $totalPossible")
    }
}