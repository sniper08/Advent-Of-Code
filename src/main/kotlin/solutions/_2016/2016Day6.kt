package solutions._2016

fun findErrorCorrectedMessage(input: Sequence<String>) {
    val all = input.toList()
    var message = ""

    for (i in all.first().indices) {
        val column = all.map { it[i] }
        message += column.toSet()
            .map { it to column.count { c -> c == it } }
            .minByOrNull { it.second }?.first
    }

    println("Error corrected message -> $message ")
}