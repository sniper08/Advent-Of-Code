package solutions._2023

import ANSI_GREEN
import ANSI_PURPLE
import ANSI_RED
import ANSI_RESET
import LetterDirection.*
import LongUpdateCoordinate
import solutions._2023.SegmentOrientation.H
import solutions._2023.SegmentOrientation.V
import kotlin.math.abs

enum class SegmentOrientation { V, H }

typealias Lagoon = Array<Array<LagoonTile>>

fun Lagoon.print() = forEach {
    println(
        it.joinToString("") { tile ->
            when {
                tile.isEdge -> "$ANSI_GREEN#$ANSI_RESET"
                tile.isBeginOrEndSegment -> "$ANSI_PURPLE#$ANSI_RESET"
                tile.isDelimiting -> "$ANSI_RED#$ANSI_RESET"
                else -> "."
            }
        }
    )
}
fun Lagoon.clear() = flatten().forEach {
    it.isEdge = false
    it.isDelimiting = false
    it.isBeginOrEndSegment = false
}
fun Lagoon.dig(segments: Set<Segment>) = segments.forEach {
    when (it.orientation) {
        H -> {
            for (x in it.from.x..it.to.x) {
                get(it.from.y.toInt())[x.toInt()].isEdge = true
            }
        }
        V -> {
            for (y in it.from.y..it.to.y) {
                get(y.toInt())[it.from.x.toInt()].isEdge = true
            }
        }
    }
}

fun Lagoon.digDelimiting(segments: Set<Segment>) = segments.forEach {
    when (it.orientation) {
        H -> {
            for (x in it.from.x..it.to.x) {
                get(it.from.y.toInt())[x.toInt()].isDelimiting = true
            }
        }
        V -> {
            for (y in it.from.y..it.to.y) {
                get(y.toInt())[it.from.x.toInt()].isDelimiting = true
            }
        }
    }
    get(it.from.y.toInt())[it.from.x.toInt()].isBeginOrEndSegment = true
    get(it.to.y.toInt())[it.to.x.toInt()].isBeginOrEndSegment = true
}

data class LagoonTile(
    var isEdge: Boolean,
    var isDelimiting: Boolean = false,
    var isBeginOrEndSegment:Boolean = false
)

data class Segment(
    val orientation: SegmentOrientation,
    val from: LongUpdateCoordinate,
    val to: LongUpdateCoordinate,
    var evaluated: Boolean = false
)

fun calculateCubicMetersInLagoon(input: Sequence<String>, inHex: Boolean = false) {
    var maxX = 0L
    var maxY = 0L
    var minX = 0L
    var minY = 0L

    var currentXSize = 0L
    var currentYSize = 0L

    val edgeSegments = mutableSetOf<Segment>()
    var edgeCount = 0

    input.forEach {
        val split = it.split(" ")
        var direction = split[0]
        var steps = split[1].toInt()

        if (inHex) {
            val hex = split.last().drop(2).dropLast(1)
            direction = when(hex.last().digitToInt()) {
                0 -> R.name.uppercase()
                1 -> D.name.uppercase()
                2 -> L.name.uppercase()
                3 -> U.name.uppercase()
                else -> throw Exception("Not valid")
            }
            steps = hex.take(5).toInt(16)
        }
        edgeCount += steps

        when (direction) {
            U.name.uppercase() -> {
                edgeSegments.add(
                    Segment(
                        orientation = V,
                        from = LongUpdateCoordinate(y = currentYSize - steps, x = currentXSize),
                        to = LongUpdateCoordinate(y = currentYSize, x = currentXSize)
                    )
                )
                currentYSize -= steps
            }
            D.name.uppercase() -> {
                edgeSegments.add(
                    Segment(
                        orientation = V,
                        from = LongUpdateCoordinate(y = currentYSize, x = currentXSize),
                        to = LongUpdateCoordinate(y = currentYSize + steps, x = currentXSize)
                    )
                )
                currentYSize += steps
            }
            L.name.uppercase() -> {
                edgeSegments.add(
                    Segment(
                        orientation = H,
                        from = LongUpdateCoordinate(y = currentYSize, x = currentXSize - steps),
                        to = LongUpdateCoordinate(y = currentYSize, x = currentXSize)
                    )
                )
                currentXSize -= steps
            }
            R.name.uppercase() -> {
                edgeSegments.add(
                    Segment(
                        orientation = H,
                        from = LongUpdateCoordinate(y = currentYSize, x = currentXSize),
                        to = LongUpdateCoordinate(y = currentYSize, x = currentXSize + steps)
                    )
                )
                currentXSize += steps
            }
        }

        if (currentXSize > maxX) maxX = currentXSize
        if (currentXSize < minX) minX = currentXSize
        if (currentYSize > maxY) maxY = currentYSize
        if (currentYSize < minY) minY = currentYSize
    }

    edgeSegments.forEach {
        it.from.y = abs(minY - it.from.y)
        it.from.x = abs(minX - it.from.x)
        it.to.y = abs(minY - it.to.y)
        it.to.x = abs(minX - it.to.x)
    }

    val edgeVerticals = edgeSegments.filter { it.orientation == V }.toMutableList().also { sortVerticals(it) }
    val edgeHorizontals = edgeSegments.filter { it.orientation == H }.toMutableList().also { sortHorizontals(it) }

    val delimitingSegments = mutableSetOf<Segment>()

    val edge = edgeSegments.filter { it.orientation == V && it.from.x == 0L }.sortedWith { a, b ->
        when {
            a.from.y < b.from.y -> 1
            b.from.y < a.from.y -> -1
            else -> 0
        }
    }
        .first()
        .also { it.evaluated = true }

    var current = edgeSegments.first { !it.evaluated && edge.from == it.from }

    var wasHorizontalGoingRight = true
    var wasVerticalGoingUp = true

    while (current.from != edge.to) {
        current.evaluated = true

        when (current.orientation) {
            H -> {
                if (wasHorizontalGoingRight) {
                    val nextVertical = edgeVerticals.first { !it.evaluated && current.to == it.to || current.to == it.from }
                    val nextVerticalGoingUp = current.to == nextVertical.to

                    if (wasVerticalGoingUp) {
                        if (nextVerticalGoingUp) {
                            addRightHorizontal(edgeVerticals, current, delimitingSegments)
                        }
                    } else {
                        if (!nextVerticalGoingUp) {
                            addLeftHorizontal(edgeVerticals, current, delimitingSegments)
                        } else {
                            addLeftHorizontal(edgeVerticals, current, delimitingSegments)
                            addRightHorizontal(edgeVerticals, current, delimitingSegments)
                        }
                    }
                    wasVerticalGoingUp = nextVerticalGoingUp
                    current = nextVertical
                } else {
                    val nextVertical = edgeVerticals.first { !it.evaluated && current.from == it.to || current.from == it.from }
                    val nextVerticalGoingUp = current.from == nextVertical.to

                    if (wasVerticalGoingUp) {
                        if (nextVerticalGoingUp) {
                            addRightHorizontal(edgeVerticals, current, delimitingSegments)
                        } else {
                            addLeftHorizontal(edgeVerticals, current, delimitingSegments)
                            addRightHorizontal(edgeVerticals, current, delimitingSegments)
                        }
                    } else {
                        if (!nextVerticalGoingUp) {
                            addLeftHorizontal(edgeVerticals, current, delimitingSegments)
                        }
                    }
                    wasVerticalGoingUp = nextVerticalGoingUp
                    current = nextVertical
                }
            }

            V -> {
                if (wasVerticalGoingUp) {
                    val nextHorizontal = edgeHorizontals.first { !it.evaluated && current.from == it.from || current.from == it.to }
                    val nextHorizontalGoingRight = current.from == nextHorizontal.from

                    if (wasHorizontalGoingRight) {
                        if (nextHorizontalGoingRight) {
                            addLowerVertical(edgeHorizontals, current, delimitingSegments)
                        } else {
                            addUpperVertical(edgeHorizontals, current, delimitingSegments)
                            addLowerVertical(edgeHorizontals, current, delimitingSegments)
                        }
                    } else {
                        if (!nextHorizontalGoingRight) {
                            addUpperVertical(edgeHorizontals, current, delimitingSegments)
                        }
                    }
                    wasHorizontalGoingRight = nextHorizontalGoingRight
                    current = nextHorizontal
                } else {
                    val nextHorizontal = edgeHorizontals.first { !it.evaluated && current.to == it.from || current.to == it.to }
                    val nextHorizontalGoingRight = current.to == nextHorizontal.from

                    if (wasHorizontalGoingRight) {
                        if (nextHorizontalGoingRight) {
                            addLowerVertical(edgeHorizontals, current, delimitingSegments)
                        }
                    } else {
                        if (!nextHorizontalGoingRight) {
                            addUpperVertical(edgeHorizontals, current, delimitingSegments)
                        } else {
                            addUpperVertical(edgeHorizontals, current, delimitingSegments)
                            addLowerVertical(edgeHorizontals, current, delimitingSegments)
                        }
                    }
                    wasHorizontalGoingRight = nextHorizontalGoingRight
                    current = nextHorizontal
                }
            }
        }
    }

    if (current.orientation == H) {
        // This has to be true
        addRightHorizontal(edgeVerticals, current, delimitingSegments)
    }

    val (horizontalEdges, verticalEdges) = edgeSegments.partition { it.orientation == H }
    val (delimitingHorizontals, delimitingVerticals) = delimitingSegments.partition { it.orientation == H }

    val allHorizontals = horizontalEdges + delimitingHorizontals
    val sanitizedHorizontals = mutableSetOf<Segment>()

    allHorizontals.forEach { horizontal ->
        val xRange = horizontal.from.x..horizontal.to.x
        val delimiting = delimitingVerticals
            .filter { vertical -> vertical.from.x in xRange && horizontal.from.y in (vertical.from.y..vertical.to.y) }
            .toMutableList()
            .also { sortVerticals(it) }
        if (delimiting.isEmpty()) {
            sanitizedHorizontals.add(horizontal)
        } else {
            var nextHorizontal = horizontal

            delimiting.forEach { delimitingVertical ->
                val left = Segment(orientation = H, from = nextHorizontal.from, to = LongUpdateCoordinate(y = nextHorizontal.from.y, x = delimitingVertical.from.x))
                if (left.from != left.to) sanitizedHorizontals.add(left)

                nextHorizontal = Segment(orientation = H, from = LongUpdateCoordinate(y = nextHorizontal.from.y, x = delimitingVertical.from.x), to = nextHorizontal.to)
            }

            if (nextHorizontal.from != nextHorizontal.to) sanitizedHorizontals.add(nextHorizontal)
        }
    }

    val allVerticals = verticalEdges + delimitingVerticals
    val sanitizedVerticals = mutableSetOf<Segment>()

    allVerticals.forEach { vertical ->
        val yRange = vertical.from.y..vertical.to.y
        val delimiting = delimitingHorizontals
            .filter { horizontal -> horizontal.from.y in yRange && vertical.from.x in (horizontal.from.x..horizontal.to.x) }
            .toMutableList()
            .also { sortHorizontals(it) }
        if (delimiting.isEmpty()) {
            sanitizedVerticals.add(vertical)
        } else {
            var nextVertical = vertical

            delimiting.forEach { delimitingHorizontal ->
                val top = Segment(orientation = V, from = nextVertical.from, to = LongUpdateCoordinate(y = delimitingHorizontal.from.y, x = nextVertical.from.x))
                if (top.from != top.to) sanitizedVerticals.add(top)

                nextVertical = Segment(orientation = V, from = LongUpdateCoordinate(y = delimitingHorizontal.from.y, x = nextVertical.from.x), to = nextVertical.to)
            }

            if (nextVertical.from != nextVertical.to) sanitizedVerticals.add(nextVertical)
        }
    }

    val totalWidth = abs(maxX - minX) + 1
    val totalHeight = abs(maxY - minY) + 1
    val totalArea = totalWidth * totalHeight

    println("The width is $totalWidth")
    println("The height is $totalHeight")
    println("The total area is $totalArea")
    println("Edge size $edgeCount")

//    val lagoon = Lagoon(abs(maxY - minY).toInt() + 1) { y ->
//        Array(abs(maxX - minX).toInt() + 1) { LagoonTile(isEdge = false) }
//    }

//   // lagoon.dig(edgeSegments)
//    lagoon.digDelimiting(sanitizedHorizontals + sanitizedVerticals)
//    lagoon.print()

    var totalDug = 0L

    val horizontalsList = sanitizedHorizontals.toMutableList().also { sortHorizontals(it) }
    val verticalsList = sanitizedVerticals.toMutableList().also { sortVerticals(it) }

    while (horizontalsList.isNotEmpty() && verticalsList.isNotEmpty()) {
        var roundDug = 0L
        val top = horizontalsList.first()
        val right = verticalsList.first { top.to == it.from }
        val bottom = horizontalsList.first { right.to == it.to }
        val left = verticalsList.first { top.from == it.from && bottom.from == it.to }

        val width = top.to.x - top.from.x
        val height = left.to.y - left.from.y

        roundDug += width * height

        horizontalsList.remove(top)
        verticalsList.remove(left)

        val nextHorizontalTop = horizontalsList.firstOrNull { top.to == it.from }
        val nextVerticalLeft = verticalsList.firstOrNull { left.to == it.from }
        val nextVerticalRight = verticalsList.firstOrNull { right.to == it.from }


        if (nextHorizontalTop == null) {
            verticalsList.remove(right)
            roundDug += right.to.y - right.from.y
        }

        when {
            (nextVerticalLeft == null && nextVerticalRight == null)
                    || (nextVerticalRight != null && nextVerticalLeft == null)
                    || nextVerticalRight == null -> {
                horizontalsList.remove(bottom)
                roundDug += bottom.to.x - bottom.from.x
            }
            nextVerticalLeft != null -> {
                val nextBottom = horizontalsList.firstOrNull { nextVerticalLeft.to == it.from && nextVerticalRight.to == it.to }

                if (nextBottom == null) {
                    horizontalsList.remove(bottom)
                    roundDug += bottom.to.x - bottom.from.x
                }
            }
        }

        totalDug += roundDug
        sortHorizontals(horizontalsList)
        sortVerticals(verticalsList)

        println()
        println("Left Segments = ${horizontalsList.size + verticalsList.size}")
        println("Round Dug = $roundDug")
        println("Total Dug = $totalDug")
    }

    println("Total Dug Corrected = ${totalDug + 1}")

//    lagoon.clear()
//    lagoon.digDelimiting(horizontalsList.toSet() + verticalsList.toSet())
//    lagoon.print()
}

private fun addRightHorizontal(
    edgeVerticals: MutableList<Segment>,
    current: Segment,
    delimitingSegments: MutableSet<Segment>
) {
    edgeVerticals.firstOrNull { current.to.x < it.from.x && current.to.y in (it.from.y..it.to.y) }
        ?.let { lockedVertical ->
            delimitingSegments.add(
                Segment(
                    orientation = H,
                    from = current.to,
                    to = LongUpdateCoordinate(y = current.to.y, x = lockedVertical.from.x)
                )
            )
        }
}

private fun addLeftHorizontal(
    edgeVerticals: MutableList<Segment>,
    current: Segment,
    delimitingSegments: MutableSet<Segment>
) {
    edgeVerticals.lastOrNull { current.from.x > it.from.x && current.from.y in (it.from.y..it.to.y) }
        ?.let { lockedVertical ->
            delimitingSegments.add(
                Segment(
                    orientation = H,
                    from = LongUpdateCoordinate(y = current.from.y, x = lockedVertical.from.x),
                    to = current.from
                )
            )
        }
}

private fun addUpperVertical(
    edgeHorizontals: MutableList<Segment>,
    current: Segment,
    delimitingSegments: MutableSet<Segment>
) {
    edgeHorizontals.lastOrNull { current.from.y > it.from.y && current.to.x in (it.from.x..it.to.x) }
        ?.let { lockedHorizontal ->
            delimitingSegments.add(
                Segment(
                    orientation = V,
                    from = LongUpdateCoordinate(y = lockedHorizontal.from.y, x = current.from.x),
                    to = current.from
                )
            )
        }
}

private fun addLowerVertical(
    edgeHorizontals: MutableList<Segment>,
    current: Segment,
    delimitingSegments: MutableSet<Segment>
) {
    edgeHorizontals.firstOrNull { current.to.y < it.from.y && current.to.x in (it.from.x..it.to.x) }
        ?.let { lockedHorizontal ->
            delimitingSegments.add(
                Segment(
                    orientation = V,
                    from = current.to,
                    to = LongUpdateCoordinate(y = lockedHorizontal.to.y, x = current.to.x)
                )
            )
        }
}

private fun sortVerticals(segments: MutableList<Segment>) {
    segments.sortWith { a, b ->
        when {
            a.from.x > b.from.x -> 1
            b.from.x > a.from.x -> -1
            else -> {
                when {
                    a.from.y > b.from.y -> 1
                    b.from.y > a.from.y -> -1
                    else -> 0
                }
            }
        }
    }
}

private fun sortHorizontals(segments: MutableList<Segment>) {
    segments.sortWith { a, b ->
        when {
            a.from.y > b.from.y -> 1
            b.from.y > a.from.y -> -1
            else -> {
                when {
                    a.from.x > b.from.x -> 1
                    b.from.x > a.from.x -> -1
                    else -> 0
                }
            }
        }
    }
}