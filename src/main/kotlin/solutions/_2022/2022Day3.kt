package solutions._2022

val LOWERCASE = 'a'..'z'
val UPPERCASE = 'A'..'Z'

fun calculatePrioritiesSum(input: Sequence<String>) {
    val sum = input.sumOf { ruckSack ->
        val compartmentSize = ruckSack.length / 2
        var misplaced: Char? = null
        var positionInLeft = 0

        while(misplaced == null && positionInLeft < compartmentSize) {
            val toFind = ruckSack[positionInLeft]
            var positionInRight = compartmentSize

            while (positionInRight <= ruckSack.lastIndex) {
                if (toFind == ruckSack[positionInRight]) {
                    misplaced = toFind
                    break
                }
                positionInRight++
            }
            positionInLeft++
        }

        calculatePriorityValue(misplaced).also {
            println("Misplaced: $misplaced - $it")
        }
    }

    println("The priorities sum is: $sum")
}

data class TypeFound(var type: Char? = null)
fun calculatePrioritiesSumByType(input: Sequence<String>) {
    val sum = input
        .chunked(3)
        .sumOf { group ->
            val firstRuckSack = group[0]
            var indexFirstRuckSack = 0
            val typeFound = TypeFound()

            while (typeFound.type == null && indexFirstRuckSack <= firstRuckSack.length) {
                val toFind = firstRuckSack[indexFirstRuckSack]
                var found = findTypeInRuckSack(toFind, typeFound, ruckSack = group[1], updateFinder = false)

                if (!found) {
                    indexFirstRuckSack++
                    continue
                }

                found = findTypeInRuckSack(toFind, typeFound, ruckSack = group[2], updateFinder = true)

                if (found) break

                indexFirstRuckSack++
            }

            calculatePriorityValue(typeFound.type).also {
                println("Misplaced: ${typeFound.type} - $it")
            }
        }

    println("The priorities sum is: $sum")
}

fun findTypeInRuckSack(toFind: Char, typeFound: TypeFound, ruckSack: String, updateFinder: Boolean): Boolean {
    for (typeInOther in ruckSack) {
        if (typeInOther == toFind) {
            if (updateFinder) {
                typeFound.type = toFind
            }
            return true
        }
    }

    return false
}

fun calculatePriorityValue(type: Char?) = type?.let {
    val positionLowercase = LOWERCASE.indexOf(type)

    if (positionLowercase > -1) {
        positionLowercase + 1
    } else {
        UPPERCASE.indexOf(type) + 1 + LOWERCASE.count()
    }
} ?: 0