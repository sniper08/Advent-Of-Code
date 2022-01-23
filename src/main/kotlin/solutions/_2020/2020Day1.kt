package solutions._2020

fun calculateExpenseMultiply(expenses: Sequence<String>) = expenses
    .map { it.toInt() }
    .mapIndexed { i, it -> it to expenses.toMutableList().apply { removeAt(i) } }
    .first { it.second.find { expense -> expense.toInt() + it.first == 2020 } != null }
    .let {
        val indexOfSecond = it.second.indexOfFirst { expense -> expense.toInt() + it.first == 2020 }

        it.first * it.second[indexOfSecond].toInt()
    }

fun calculateExpenseMultiply3(expenses: Sequence<String>) = expenses
    .map { it.toInt() }
    .sorted()
    .toList()
    .let {
        var x = 0
        var y = x + 1
        var z = it.size - 1

        while(y in (x + 1) until z) {
            val sum = it[x] + it[y] + it[z]

            when {
                sum > 2020 -> { y = x + 1; z-- }
                sum < 2020 -> {
                    if (z - y > 1) {
                        y++
                    } else {
                        x++ ; y = x + 1; z = it.size - 1
                    }
                }
                sum == 2020 -> return@let it[x] * it[y] * it[z]
            }
        }

        it.first() // should not happen
    }




