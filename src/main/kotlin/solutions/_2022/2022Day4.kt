package solutions._2022

fun findAssignmentContainedInAnotherCount(input: Sequence<String>) {
    val count = input.sumOf {
        val assignmentPair = it.split(",")
        val first = assignmentPair.first().split("-").map { it.toInt() }
        val second = assignmentPair.last().split("-").map { it.toInt() }

        val firstInSecond = first.first() >= second.first() && first.last() <= second.last()
        val secondInFirst = second.first() >= first.first() && second.last() <= first.last()

        if (firstInSecond || secondInFirst) 1L else 0
    }

    println("Assignments that contain another: $count")
}

fun findOverlappingAssignments(input: Sequence<String>) {
    val count = input.sumOf {
        val assignmentPair = it.split(",")
        val (firstStart, firstEnd) = createLimits(assignmentPair.first())
        val (secondStart, secondEnd) = createLimits(assignmentPair.last())

        if (secondStart in firstStart..firstEnd || firstStart in secondStart..secondEnd) 1L else 0
    }

    println("Assignments that overlap: $count")
}

fun createLimits(string: String) = string.split("-")
    .map { it.toInt() }
    .let { Pair(it.first(), it.last()) }