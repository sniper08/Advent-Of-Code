package solutions._2021

import parser.inputCleaner
import solutions._2021.Operation.SUB
import solutions._2021.Operation.SUM
import kotlin.math.absoluteValue
import kotlin.math.sign

private const val MIN_BEACONS_MUTUAL = 12

enum class Axis { X, Y, Z }
enum class Operation { SUM, SUB }

data class LastInfo(val secondAxisValue: AxisValue, val thirdAxisValue: AxisValue)

sealed class AxisValue(val axis: Axis, open val operation: Operation, open val value: Int) {
    companion object {
        fun fromAxis(axis: Axis, operation: Operation, value: Int): AxisValue {
            return when (axis) {
                Axis.X -> X(operation, value)
                Axis.Y -> Y(operation, value)
                Axis.Z -> Z(operation, value)
            }
        }
    }

    data class X(override val operation: Operation, override val value: Int) : AxisValue(Axis.X, operation, value)
    data class Y(override val operation: Operation, override val value: Int) : AxisValue(Axis.Y, operation, value)
    data class Z(override val operation: Operation, override val value: Int) : AxisValue(Axis.Z, operation, value)
}

data class FoundAxisValue(val sums: Set<AxisValue>, val diffs: Set<AxisValue>) {

    fun findUnique(): AxisValue {
        if (sums.size == 1) return sums.first()
        if (diffs.size == 1) return diffs.first()
        return AxisValue.X(SUM, 0)
    }
}

data class Coordinate3D(val x: Int, val y: Int, val z: Int) {

    fun rotate90onX() = copy(x = x, y = z * -1, z = y)
    fun rotate90onY() = copy(x = z, y = y, z = x * -1)
    fun rotate90onZ() = copy(x = y, y = x * -1, z = z)

    fun flipX() = copy(x = x * -1, y = y, z = z)
    fun flipY() = copy(x = x, y = y * -1, z = z)
    fun flipZ() = copy(x = x, y = y, z = z * -1)

    override fun toString(): String = "$x, $y, $z"
}

data class ScannerPosition(val x: AxisValue.X, val y: AxisValue.Y, val z: AxisValue.Z) {
    companion object {
        fun createFrom(list: List<AxisValue>) = ScannerPosition(
            list.filterIsInstance<AxisValue.X>().first(),
            list.filterIsInstance<AxisValue.Y>().first(),
            list.filterIsInstance<AxisValue.Z>().first()
        )

        val POSITION_0 = ScannerPosition(
            AxisValue.X(SUM, 0),
            AxisValue.Y(SUM, 0),
            AxisValue.Z(SUM, 0)
        )
    }
}

data class Scanner(
    val index: Int,
    var positionTo0: ScannerPosition = ScannerPosition.POSITION_0,
    val beacons: MutableSet<Coordinate3D>
) {
    fun rotate90onX() {
        val temp = beacons.toList()
        beacons.clear()
        beacons.addAll(temp.map { it.rotate90onX() })
    }

    fun rotate90onY() {
        val temp = beacons.toList()
        beacons.clear()
        beacons.addAll(temp.map { it.rotate90onY() })
    }

    fun rotate90onZ() {
        val temp = beacons.toList()
        beacons.clear()
        beacons.addAll(temp.map { it.rotate90onZ() })
    }

    fun flipX() {
        val temp = beacons.toList()
        beacons.clear()
        beacons.addAll(temp.map { it.flipX() })
    }

    fun flipY() {
        val temp = beacons.toList()
        beacons.clear()
        beacons.addAll(temp.map { it.flipY() })
    }

    fun flipZ() {
        val temp = beacons.toList()
        beacons.clear()
        beacons.addAll(temp.map { it.flipZ() })
    }

    fun normalizeTo0() {
        val temp = beacons.toList()
        beacons.clear()
        beacons.addAll(
            temp.map {
                Coordinate3D(
                    x = if (positionTo0.x.operation == SUM) positionTo0.x.value - it.x else positionTo0.x.value + it.x,
                    y = if (positionTo0.y.operation == SUM) positionTo0.y.value - it.y else positionTo0.y.value + it.y,
                    z = if (positionTo0.z.operation == SUM) positionTo0.z.value - it.z else positionTo0.z.value + it.z,
                )
            }
        )
    }

    override fun toString(): String = "---- Scanner $index ----\n" +
            "${beacons.map { it.toString() + "\n" }}"
}

data class Indexes(val firstIndex: Int, val secondIndex: Int) {

    override fun hashCode(): Int = firstIndex

    override fun equals(other: Any?): Boolean {
        return other is Indexes
                && ((other.firstIndex == firstIndex && other.secondIndex == secondIndex)
                || other.firstIndex == firstIndex
                || other.secondIndex == secondIndex)
    }
}

fun calculateBeaconsCount(input: Sequence<String>) {
    val scanners = createScanners(input).toSet()
    val checkedScanners = mutableSetOf<Scanner>()
    var uncheckedScanners = scanners.drop(1).toSet()

    for (unchecked in uncheckedScanners) {
        findPositionRelativeTo0(scanners.elementAt(0), unchecked)
    }

    uncheckedScanners.partition { it.positionTo0 != ScannerPosition.POSITION_0 }.let {
        checkedScanners.addAll(it.first.toSet())
        uncheckedScanners = it.second.toSet()
    }

    while (uncheckedScanners.isNotEmpty()) {
        val firstChecked = checkedScanners.first()
        for (unchecked in uncheckedScanners) {
            findPositionRelativeTo0(firstChecked, unchecked)
        }
        checkedScanners.remove(firstChecked)
        uncheckedScanners.partition { it.positionTo0 != ScannerPosition.POSITION_0 }.let {
            checkedScanners.addAll(it.first.toSet())
            uncheckedScanners = it.second.toSet()
        }
    }

    val allBeacons = mutableSetOf<Coordinate3D>().apply {
        for (scanner in scanners) {
            addAll(scanner.beacons)
        }
    }

    println("\n------ All Beacons: ${allBeacons.size}-------")
    allBeacons.sortedBy { it.x }.forEach { println(it.toString()) }

    findHighestManhattanDistance(scanners.toSet())
}

fun findHighestManhattanDistance(scanners: Set<Scanner>) {
    val manhattanDistances = mutableSetOf<Int>()

    for (i in scanners.indices) {
        val current = scanners.elementAt(i)
        val all = scanners.toMutableSet().apply { remove(current) }
        val x = current.positionTo0.x.value
        val y = current.positionTo0.y.value
        val z = current.positionTo0.z.value

        for (scanner in all) {
            val sX = scanner.positionTo0.x.value
            val sY = scanner.positionTo0.y.value
            val sZ = scanner.positionTo0.z.value

            val dX = if (x.sign == sX.sign) x.absoluteValue - sX.absoluteValue else x.absoluteValue + sX.absoluteValue
            val dY = if (y.sign == sY.sign) y.absoluteValue - sY.absoluteValue else y.absoluteValue + sY.absoluteValue
            val dZ = if (z.sign == sZ.sign) z.absoluteValue - sZ.absoluteValue else z.absoluteValue + sZ.absoluteValue

            manhattanDistances.add(dX.absoluteValue + dY.absoluteValue + dZ.absoluteValue)
        }
    }

    println("\n------ Highest Manhattan Distance: ${manhattanDistances.maxOrNull()}-------")
}

fun findPositionRelativeTo0(checked: Scanner, unChecked: Scanner) {
    var found: Map.Entry<AxisValue, MutableSet<Indexes>>? = null

    axisLoop@ for (axis in enumValues<Axis>()) {
        val differMap = mutableMapOf<AxisValue, MutableSet<Indexes>>()
        val sumMap = mutableMapOf<AxisValue, MutableSet<Indexes>>()
        for ((i, coord0) in checked.beacons.withIndex()) {
            for ((j, coord1) in unChecked.beacons.withIndex()) {
                val sharedIndex = Indexes(i, j)
                val firstValue = when (axis) {
                    Axis.X -> coord0.x
                    Axis.Y -> coord0.y
                    Axis.Z -> coord0.z
                }
                val secondValue = coord1.x

                val diff = AxisValue.fromAxis(axis, SUB, firstValue - secondValue)
                val sum = AxisValue.fromAxis(axis, SUM, firstValue + secondValue)

                differMap[diff]?.add(sharedIndex) ?: run { differMap[diff] = mutableSetOf(sharedIndex) }
                sumMap[sum]?.add(sharedIndex) ?: run { sumMap[sum] = mutableSetOf(sharedIndex) }

                found = (differMap + sumMap).entries.firstOrNull { it.value.size >= MIN_BEACONS_MUTUAL }
                if (found != null) {
                    break@axisLoop
                }
            }
        }
    }

    found?.let { found ->
        val lastInfo = when (found.key.axis) {
            Axis.X -> findRemainingOnX(checked, unChecked, found.value.take(2))
            Axis.Y -> findRemainingOnY(checked, unChecked, found.value.take(2))
            Axis.Z -> findRemainingOnZ(checked, unChecked, found.value.take(2))
        }

        val axisValues = listOf(found.key, lastInfo.secondAxisValue, lastInfo.thirdAxisValue)

        unChecked.positionTo0 = ScannerPosition.createFrom(axisValues)
        unChecked.normalizeTo0()

        println("------------------------------")
        println("Scanner ${checked.index} with Scanner ${unChecked.index}")
        println("Operation found ${found.key.axis.name}:${found.key.value} - ${lastInfo.secondAxisValue.axis.name}:${lastInfo.secondAxisValue.value} - ${lastInfo.thirdAxisValue.axis.name}:${lastInfo.thirdAxisValue.value}")
        println("Scanner ${unChecked.index} Relative To 0 --- X: ${unChecked.positionTo0.x.value} - Y: ${unChecked.positionTo0.y.value} - Z: ${unChecked.positionTo0.z.value}")
        found.value.forEach {
            println(
                checked.beacons.elementAt(it.firstIndex)
                    .toString() + "     " + unChecked.beacons.elementAt(it.secondIndex).toString()
            )
        }
    }
}

fun findRemainingOnZ(checked: Scanner, unChecked: Scanner, comparables: List<Indexes>) : LastInfo {
    unChecked.rotate90onY()
    unChecked.flipZ()
    val uniqueOperation = findOperations(checked, unChecked, comparables, Axis.X).findUnique()

    return if (uniqueOperation.value == 0) {
        unChecked.rotate90onZ()
        unChecked.flipY()
        LastInfo(
            secondAxisValue = findOperations(checked, unChecked, comparables, Axis.X).findUnique(),
            thirdAxisValue = findOperations(checked, unChecked, comparables, Axis.Y).findUnique()
        )

    } else {
        LastInfo(
            secondAxisValue = uniqueOperation,
            thirdAxisValue = findOperations(checked, unChecked, comparables, Axis.Y).findUnique()
        )
    }
}

fun findRemainingOnY(checked: Scanner, unChecked: Scanner, comparables: List<Indexes>) : LastInfo {
    unChecked.rotate90onZ()
    unChecked.flipY()

    val uniqueOperation = findOperations(checked, unChecked, comparables, Axis.X).findUnique()

    return if (uniqueOperation.value == 0) {
        unChecked.rotate90onY()
        unChecked.flipZ()
        LastInfo(
            secondAxisValue = findOperations(checked, unChecked, comparables, Axis.X).findUnique(),
            thirdAxisValue = findOperations(checked, unChecked, comparables, Axis.Z).findUnique()
        )
    } else {
        LastInfo(
            secondAxisValue = uniqueOperation,
            thirdAxisValue = findOperations(checked, unChecked, comparables, Axis.Z).findUnique()
        )
    }
}

fun findRemainingOnX(checked: Scanner, unChecked: Scanner, comparables: List<Indexes>) : LastInfo {
    val uniqueOperation = findOperations(checked, unChecked, comparables, Axis.Y).findUnique()

    return if (uniqueOperation.value == 0) {
        unChecked.rotate90onX()
        unChecked.flipY()
        LastInfo(
            secondAxisValue = findOperations(checked, unChecked, comparables, Axis.Y).findUnique(),
            thirdAxisValue = findOperations(checked, unChecked, comparables, Axis.Z).findUnique()
        )
    } else {
        LastInfo(
            secondAxisValue = uniqueOperation,
            thirdAxisValue = findOperations(checked, unChecked, comparables, Axis.Z).findUnique()
        )
    }
}

fun findOperations(checked: Scanner, unChecked: Scanner, indexes: List<Indexes>, axis: Axis): FoundAxisValue {
    val sums = mutableSetOf<AxisValue>()
    val diffs = mutableSetOf<AxisValue>()

    indexes.forEach { foundIndex ->
        var firstValue = checked.beacons.elementAt(foundIndex.firstIndex).x
        var secondValue = unChecked.beacons.elementAt(foundIndex.secondIndex).x

        when (axis) {
            Axis.Y -> {
                firstValue = checked.beacons.elementAt(foundIndex.firstIndex).y
                secondValue = unChecked.beacons.elementAt(foundIndex.secondIndex).y
            }
            Axis.Z -> {
                firstValue = checked.beacons.elementAt(foundIndex.firstIndex).z
                secondValue = unChecked.beacons.elementAt(foundIndex.secondIndex).z
            }
            else -> { }
        }

        sums.add(AxisValue.fromAxis(axis, SUM, firstValue + secondValue))
        diffs.add(AxisValue.fromAxis(axis, SUB, firstValue - secondValue))
    }

    return FoundAxisValue(sums, diffs)
}

fun createScanners(input: Sequence<String>) = input.mapIndexed { index, scanner ->
    val coordinates = inputCleaner(scanner)
        .drop(1)
        .map {
            it.split(',').let { coordinate ->
                Coordinate3D(
                    coordinate[0].toInt(),
                    coordinate[1].toInt(),
                    coordinate[2].toInt()
                )
            }
        }
    Scanner(index, beacons = coordinates.toMutableSet())
}.toList()



