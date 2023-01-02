package solutions._2022

const val TIME_LIMIT_PART_1 = 24
const val TIME_LIMIT_PART_2 = 32

data class RobotCost(val oreCost: Int, val clayCost: Int = 0, val obsidianCost: Int = 0)

data class BluePrint(
    val index: Int,
    val oreRobotCost: RobotCost,
    val clayRobotCost: RobotCost,
    val obsidianRobotCost: RobotCost,
    val geodeRobotCost: RobotCost
)

interface Material {
    val robots: Int
    val amount: Int

    fun nextAmount() = amount + robots
}

data class Ore(override val robots: Int, override val amount: Int): Material
data class Clay(override val robots: Int, override val amount: Int): Material
data class ObsidianR(override val robots: Int, override val amount: Int): Material
data class Geode(override val robots: Int, override val amount: Int): Material

data class RocksCollection(
    val minutesElapsed: Int = 2,
    val ore: Ore = Ore(robots = 1, amount = 2),
    val clay: Clay = Clay(robots = 0, amount = 0),
    val obsidian: ObsidianR = ObsidianR(robots = 0, amount = 0),
    val geode: Geode = Geode(robots = 0, amount = 0)
) {
    fun createNextBasedOn(bluePrint: BluePrint, timeLimit: Int) : List<RocksCollection> {
        val next = mutableListOf<RocksCollection>()

        if (minutesElapsed < timeLimit) {
            val nextMinutes = minutesElapsed + 1
            val newOre = Ore(ore.robots, ore.nextAmount())
            val newClay = Clay(clay.robots, clay.nextAmount())
            val newObsidian = ObsidianR(obsidian.robots, obsidian.nextAmount())
            val newGeode = Geode(geode.robots, geode.nextAmount())

            next.add(RocksCollection(nextMinutes, newOre, newClay, newObsidian, newGeode))

            if (ore.amount >= bluePrint.geodeRobotCost.oreCost && obsidian.amount >= bluePrint.geodeRobotCost.obsidianCost) {
                next.add(RocksCollection(nextMinutes, Ore(newOre.robots, newOre.amount - bluePrint.geodeRobotCost.oreCost), newClay, ObsidianR(newObsidian.robots, newObsidian.amount - bluePrint.geodeRobotCost.obsidianCost), Geode(newGeode.robots + 1, newGeode.amount)))
            } else {
                val t = timeLimit - minutesElapsed
                val canCreateObsidianRobot = (obsidian.robots * t) + obsidian.amount < t * bluePrint.geodeRobotCost.obsidianCost
                if (ore.amount >= bluePrint.obsidianRobotCost.oreCost && clay.amount >= bluePrint.obsidianRobotCost.clayCost && canCreateObsidianRobot) {
                    next.add(RocksCollection(nextMinutes, Ore(newOre.robots, newOre.amount - bluePrint.obsidianRobotCost.oreCost), Clay(newClay.robots, newClay.amount - bluePrint.obsidianRobotCost.clayCost), ObsidianR(newObsidian.robots + 1, newObsidian.amount), newGeode))
                }

                val canCreateClayRobot = (clay.robots * t) + clay.amount < t * bluePrint.obsidianRobotCost.clayCost
                if (ore.amount >= bluePrint.clayRobotCost.oreCost && canCreateClayRobot) {
                    next.add(RocksCollection(nextMinutes, Ore(newOre.robots, newOre.amount - bluePrint.clayRobotCost.oreCost), Clay(newClay.robots + 1, newClay.amount), newObsidian, newGeode))
                }

                val maxOreNeeded = listOf(bluePrint.oreRobotCost.oreCost, bluePrint.clayRobotCost.oreCost, bluePrint.obsidianRobotCost.oreCost, bluePrint.geodeRobotCost.oreCost).max()
                val canCreateOreRobot = (ore.robots * t) + ore.amount < t * maxOreNeeded
                if (ore.amount >= bluePrint.oreRobotCost.oreCost && canCreateOreRobot) {
                    next.add(RocksCollection(nextMinutes, Ore(ore.robots + 1, newOre.amount - bluePrint.oreRobotCost.oreCost), newClay, newObsidian, newGeode))
                }
            }
        }

        return next
    }

    fun onLimit() = RocksCollection(minutesElapsed + 1, Ore(ore.robots, ore.nextAmount()), Clay(clay.robots, clay.nextAmount()), ObsidianR(obsidian.robots, obsidian.nextAmount()), Geode(geode.robots, geode.nextAmount()))
}

fun findQualityLevels(input: Sequence<String>) {
    val bluePrints = input
        .toList()
        .map {
            val raw = it.split(" ", ": ", ". ")
            BluePrint(
                index = raw[1].toInt(),
                oreRobotCost = RobotCost(oreCost = raw[6].toInt()),
                clayRobotCost = RobotCost(oreCost = raw[12].toInt()),
                obsidianRobotCost = RobotCost(oreCost = raw[18].toInt(), clayCost = raw[21].toInt()),
                geodeRobotCost = RobotCost(oreCost = raw[27].toInt(), obsidianCost = raw[30].toInt())
            )
        }
//
//    val qualityLevelSum = bluePrints.sumOf { blueprint ->
//        val highestGeodes = blueprint.highestGeode(TIME_LIMIT_PART_1)
//        println("Index = ${blueprint.index} -- $highestGeodes")
//        (highestGeodes?.geode?.amount ?: 0) * blueprint.index
//    }
//
//    //val qualityLevelSum = bluePrints[1].highestGeode(TIME_LIMIT_PART_1)
//    println("Sum of all = $qualityLevelSum")

    val qualityMultiply = bluePrints.take(3).fold(1) { acc, blueprint ->
        val highestGeodes = blueprint.highestGeode(TIME_LIMIT_PART_2)
        (highestGeodes?.geode?.amount ?: 0) * acc
    }

    println("Multiply = $qualityMultiply")
}

fun BluePrint.highestGeode(timeLimit: Int): RocksCollection? {
    val currentSet = mutableSetOf(RocksCollection())
    var highestGeodes: RocksCollection? = null

    while (currentSet.isNotEmpty()) {
        val current = currentSet.elementAt(0).also { currentSet.remove(it) }

        if (current.minutesElapsed + 1 == timeLimit) {
            val onLimit = current.onLimit()
            val highest = highestGeodes?.geode?.amount ?: 0

            if (onLimit.geode.amount > highest) {
                highestGeodes = onLimit
            }
        } else {
            for (next in current.createNextBasedOn(this, timeLimit)) {
                currentSet.add(next)
            }
        }
    }

    return highestGeodes
}




