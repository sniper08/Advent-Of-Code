package solutions._2021

import java.math.BigInteger

val hexaBits = mapOf(
    "0" to "0000",
    "1" to "0001",
    "2" to "0010",
    "3" to "0011",
    "4" to "0100",
    "5" to "0101",
    "6" to "0110",
    "7" to "0111",
    "8" to "1000",
    "9" to "1001",
    "A" to "1010",
    "B" to "1011",
    "C" to "1100",
    "D" to "1101",
    "E" to "1110",
    "F" to "1111"
)

sealed class Packet(
    open val version: Int,
    open val packetTypeID: Int,
    open val versionsTotal: Long,
    open val value: BigInteger,
    open val size: Long
) {
    data class Literal(
        override val version: Int,
        override val size: Long,
        override val value: BigInteger
    ) : Packet(version, 4, version.toLong(), value, size)

    data class Operator(
        override val version: Int,
        override val packetTypeID: Int,
        override val size: Long,
        override val versionsTotal: Long,
        override val value: BigInteger,
        val operators: List<Operator>,
        val literals: List<Literal>
    ) : Packet(version, packetTypeID, versionsTotal, value, size)
}

fun calculateVersionTotal(input: String) : Long {
    val binary = decodeIntoBinary(input)
    val packet = createOperator(binary)

    printOperator(packet)
    println(packet.value)
    return packet.versionsTotal
}

fun printOperator(operator: Packet.Operator, level: Int = 0) {
    operator.let {
        val spaces = CharArray( level + 1) { ' ' }
        print(spaces)
        println(it)
        it.operators.forEach {
            printOperator(it, level + 1)
        }
        println("${spaces.joinToString("")}literals: ")
        it.literals.forEach { println("${spaces.joinToString("")}$it") }
    }
}

fun decodeIntoBinary(input: String): String {
    val binary = StringBuilder()

    for (char in input) {
        binary.append(hexaBits[char.toString()])
    }

    return binary.toString()
}

fun isNextLiteral(input: String) = input.substring(3..5).toInt(2) == 4

fun createLiteral(input: String): Packet.Literal {
    var size = 6L
    val bits = StringBuilder()

    for (x in 6 until input.length step 5) {
        size++
        bits.append(input.substring((x + 1)..(x + 4)))

        if (input[x] == '0') break
    }

    return Packet.Literal(
        version = input.take(3).toInt(2),
        value = bits.toString().toBigInteger(2),
        size = size + bits.length.toLong()
    )
}

fun createOperator(input: String): Packet.Operator {
    val version = input.take(3).toInt(2)
    val packetTypeID = input.substring(3..5).toInt(2)
    var amountCodes = 0L
    var codeString = ""
    var size = 6L
    var versionTotal = 0L
    val lengthTypeID = input[6].digitToInt() ; size++

    if (lengthTypeID == 1) {
        size += 11
        amountCodes = input.substring(7, 7 + 11).toLong(2)
        codeString = input.substring(7 + 11, input.length)
    } else {
        size += 15
        val bitsForCode = input.substring(7, 7 + 15).toLong(2)
        codeString = input.substring(7 + 15, 7 + 15 + bitsForCode.toInt())
    }

    var currentPacket: Packet
    val operators = mutableListOf<Packet.Operator>()
    val literals = mutableListOf<Packet.Literal>()

    var canContinue = true

    while (canContinue) {
        if (isNextLiteral(codeString)) {
            currentPacket = createLiteral(codeString)
            literals.add(currentPacket)
        } else {
            currentPacket = createOperator(codeString)
            operators.add(currentPacket)
        }
        versionTotal += currentPacket.versionsTotal
        size += currentPacket.size

        if (amountCodes > 0 && (operators + literals).size.toLong() == amountCodes) {
            canContinue = false
            continue
        }

        codeString = if (codeString.length > currentPacket.size) {
            codeString.substring(currentPacket.size.toInt(), codeString.length)
        } else {
            ""
        }

        if (codeString.isEmpty()) {
            canContinue = false
        }
    }

    return Packet.Operator(
        version = version,
        packetTypeID = packetTypeID,
        size = size,
        versionsTotal = version + versionTotal,
        value = (operators + literals).findValue(packetTypeID),
        operators = operators,
        literals = literals
    )
}

fun List<Packet>.findValue(packetTypeID: Int): BigInteger = when (packetTypeID) {
    0 -> sumOf { it.value }
    1 -> fold(BigInteger.ONE) { acc, code -> acc * code.value }
    2 -> minOf { it.value }
    3 -> maxOf { it.value }
    5 -> if (first().value > last().value) BigInteger.ONE else BigInteger.ZERO
    6 -> if (first().value < last().value) BigInteger.ONE else BigInteger.ZERO
    7 -> if (first().value == last().value) BigInteger.ONE else BigInteger.ZERO
    else -> BigInteger.ZERO
}
