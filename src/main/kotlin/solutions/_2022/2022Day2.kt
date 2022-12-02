package solutions._2022

interface Strategy {
    val a: Int
    val b: Int
    val c: Int
    val value: Int

    val elfPlay get() = mapOf(
        "A" to a,
        "B" to b,
        "C" to c
    )
}
enum class MyPlay(override val a: Int, override val b: Int, override val c: Int, override val value: Int) : Strategy {
    X(3, 0, 6, 1),
    Y(6, 3,0,2),
    Z(0, 6, 3, 3);
}

enum class Result(override val a: Int, override val b: Int, override val c: Int, override val value: Int) : Strategy {
    X(3, 1, 2, 0),
    Y(1, 2,3,3),
    Z(2, 3, 1, 6);
}
fun calculateTotalPlayValue(strategy: Strategy, elfPlay: String) = (strategy.elfPlay[elfPlay] ?: 0) + strategy.value
fun calculatePointsRockPaperScissors(input: Sequence<String>) {
    val valueStrategy = input.sumOf {
        it.split(" ").let { play ->
            calculateTotalPlayValue(
                strategy = MyPlay.values().first { myPlay -> myPlay.name == play.last() },
                elfPlay = play.first()
            )
        }
    }

    println("The strategy value is: $valueStrategy")
}

fun calculatePointsRockPaperScissorsImproved(input: Sequence<String>) {
    val valueStrategy = input.sumOf {
        it.split(" ").let { play ->
            calculateTotalPlayValue(
                strategy = Result.values().first { myPlay -> myPlay.name == play.last() },
                elfPlay = play.first()
            )
        }
    }

    println("The strategy value is: $valueStrategy")
}