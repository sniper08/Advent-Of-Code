package solutions._2023

data class Race(val time: Long, val record: Long = 0)

fun calculateWayToBeatRecordProductForRaces(input: Sequence<String>) {
    val times = input
        .first()
        .split(":  ", "  ")
        .mapNotNull { try { it.trim().toLong() } catch (e: Exception) { null } }

    val records = input
        .last()
        .split(":  ", "  ")
        .mapNotNull { try { it.trim().toLong() } catch (e: Exception) { null } }

    val product = times
        .mapIndexed { i, time -> Race(time, records[i]) }
        .fold(1L) { acc, race ->
            val lowerLimit = (1..race.time).first { it * (race.time - it) > race.record }
            val ways = (race.time - lowerLimit) - lowerLimit + 1
            println("$race -> $ways")
            acc * ways
        }

    println("The products of the way to beat the races' records is $product")
}

fun calculateWayToBeatRecordForRace(input: Sequence<String>) {
    val time = input
        .first()
        .split(":  ", "  ")
        .drop(1)
        .fold("") { acc, split -> acc + split.trim() }
        .toLong()

    val record = input
        .last()
        .split(":  ", "  ")
        .drop(1)
        .fold("") { acc, split -> acc + split.trim() }
        .toLong()

    val ways = Race(time, record)
        .let {
            println(it)
            val lowerLimit = (1..time).first { num -> num * (time - num) > record }
            (time - lowerLimit) - lowerLimit + 1
        }

    println("There are $ways to beat the record")
}