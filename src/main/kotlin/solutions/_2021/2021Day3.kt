package solutions._2021

fun calculatePowerConsumption(input: Sequence<String>): Int{
    val inputList = input.toList()
    val one = '1'
    val zero = '0'

    var gamma = ""
    var epsilon = ""

    for (i in 0 until inputList.first().length) {
        val oneCount = inputList.count { it[i] == one }

        if (oneCount > inputList.size / 2) {
            gamma += one
            epsilon += zero
        } else {
            gamma += zero
            epsilon += one
        }
    }

    return gamma.toInt(2) * epsilon.toInt(2)
}

fun calculateLifeSupportRating(input: Sequence<String>): Int{
    val inputList = input.toList()
    val one = '1'
    val zero = '0'

    val (oxygenFirst, co2ScrubberFirst) = inputList.partition { it[0] == one }.let {
        if (it.first.size >= it.second.size) {
            it
        } else {
            it.copy(first = it.second, second = it.first)
        }
    }

    val oxygen = getMeasure(oxygenFirst, one) { if (first.size >= second.size) first else second }
    val co2scrubber = getMeasure(co2ScrubberFirst, zero) { if (first.size <= second.size) first else second }

    return oxygen * co2scrubber
}

fun getMeasure(
    firstMeasure: List<String>,
    charToFind: Char,
    filter: Pair<List<String>, List<String>>.() -> List<String>
) : Int {
    var index = 1
    var measures: List<String> = firstMeasure

    while(measures.size > 1) {
        measures = measures.partition { it[index] == charToFind }.filter()
        index++
    }

    return measures.first().toInt(2)
}
