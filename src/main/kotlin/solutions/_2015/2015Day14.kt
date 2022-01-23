package solutions._2015

data class Reindeer(val name: String, val speed: Int, val stamina: Int, val rest: Int) {
    private val interval = stamina + rest
    private val maxDistanceInterval = speed * stamina

    var currentDistance = 0
    var leftToMove = stamina
    var leftToRest = 0
    var points = 0

    fun calculateTotalDistanceIn(seconds: Int): Int {
        var completed = seconds / interval
        val lastSplitInterval = seconds.rem(interval)
        var distanceInLastSplit = 0

        if (lastSplitInterval > stamina) {
            completed++
        } else {
            distanceInLastSplit = lastSplitInterval * speed
        }

        return (completed * maxDistanceInterval) + distanceInLastSplit
    }

    fun move() {
        if (leftToMove > 0) {
            currentDistance += speed
            leftToMove--
            if (leftToMove == 0) { leftToRest = rest }
            return
        }
        if (leftToRest > 0) {
            leftToRest--
            if (leftToRest == 0) { leftToMove = stamina }
        }
    }

    fun award() {
        points++
    }
}

fun calculateFastestReindeer(input: Sequence<String>) {
    val reindeers = input
        .map {
            val split = it.split(" ")
            Reindeer(name = split[0], speed = split[3].toInt(), stamina = split[6].toInt(), rest = split[13].toInt())
        }
        .toList()
        .onEach { println(it) }

    val seconds = 2503

    println()
    calculateOldSystem(reindeers, seconds)
    println()
    calculateNewSystem(reindeers, seconds)
}

fun calculateOldSystem(reindeers: List<Reindeer>, seconds: Int) {
    val afterRace = reindeers.map { it.name to it.calculateTotalDistanceIn(seconds) }

    afterRace.forEach { println(it) }

    val winner = afterRace.maxByOrNull { it.second }
    println("The Winner is: ${winner?.first} with a distance of ${winner?.second} in $seconds seconds")
}

fun calculateNewSystem(reindeers: List<Reindeer>, seconds: Int) {
    repeat(seconds) {
        reindeers.forEach { it.move() }
        val maxValue = reindeers.maxByOrNull { it.currentDistance }?.currentDistance ?: 0

        reindeers.filter { it.currentDistance == maxValue }.forEach { it.award() }
    }

    reindeers.forEach { println("${it.name} - distance: ${it.currentDistance} - points: ${it.points}") }

    val winner = reindeers.maxByOrNull { it.points }
    println("The Winner is: ${winner?.name} with ${winner?.points} points")
}

