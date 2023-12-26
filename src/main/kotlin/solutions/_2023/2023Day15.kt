package solutions._2023

fun calculateSumOfResultInitializationSequence(input: String) {
    val newLineAscii = '\n'.code
    val sum = input
        .split(",")
        .sumOf { sequence -> sequence.boxNumber() }
    println("The sum of the sequences' results is $sum")
}

data class Lens(val label: String, var focalLength: Int) {
    override fun toString(): String = "[$label $focalLength]"
}

fun String.boxNumber(): Int {
    var value = 0

    for (c in this) {
        value += c.code
        value *= 17
        value = value.rem(256)
    }
    return value
}

fun calculateFocusingPowerOfLensConfiguration(input: String) {
    val boxes = mutableMapOf<Int, MutableSet<Lens>>()

    input
        .split(",")
        .forEach { sequence ->
            if (sequence.contains('=')) {
                val split = sequence.split("=")
                val label = split[0]
                val boxNumber = label.boxNumber()
                val focalLength = split[1].toInt()

                val box = boxes.getOrDefault(boxNumber, mutableSetOf())
                val lens = box.firstOrNull { it.label == label }

                if (lens != null) {
                    lens.focalLength = focalLength
                } else {
                    box.add(Lens(label, focalLength))
                    boxes[boxNumber] = box
                }
            } else {
                val label = sequence.dropLast(1)
                val boxNumber = label.boxNumber()

                boxes[boxNumber]?.removeIf { it.label == label }
            }
        }

    boxes.forEach { println(it) }

    val focusingPower = boxes.entries.sumOf { box ->
        val first = box.key + 1
        var totalBox = 0

        box.value.forEachIndexed { i, lens ->
            val second = i + 1
            val third = lens.focalLength

            totalBox += first * second * third
        }
        totalBox
    }

    println("The total focusing power is $focusingPower")
}