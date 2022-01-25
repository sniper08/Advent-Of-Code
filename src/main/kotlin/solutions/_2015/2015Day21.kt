package solutions._2015

interface Item {
    val cost: Int
    val damage: Int
    val armor: Int
}

enum class Weapon(
    override val cost: Int,
    override val damage: Int,
    override val armor: Int
) : Item {
    DAGGER(8, 4, 0),
    SHORTSWORD(10, 5, 0),
    WARHAMMER(25, 6, 0),
    LONGSWORD(40, 7, 0),
    GREATAXE(74, 8, 0)
}

enum class Armor(
    override val cost: Int,
    override val damage: Int,
    override val armor: Int
) : Item {
    LEATHER(13, 0, 1),
    CHAINMAIL(31, 0, 2),
    SPLINTMAIL(53, 0, 3),
    BANDEDMAIL(75, 0, 4),
    PLATEMAIL(102, 0, 5)
}

enum class Ring(
    override val cost: Int,
    override val damage: Int,
    override val armor: Int
) : Item {
    DAMAGE_1(25, 1, 0),
    DAMAGE_2(50, 2, 0),
    DAMAGE_3(100, 3, 0),
    DEFENSE_1(20, 0, 1),
    DEFENSE_2(40, 0, 2),
    DEFENSE_3(80, 0, 3)
}

data class Boss(var hitPoints: Int, val damage: Int, val armor: Int)

fun calculateLowestCostRPG(input: Sequence<String>) {
    val boss = Boss(
        hitPoints = input.first().split(" ").last().toInt(),
        damage = input.elementAt(1).split(" ").last().toInt(),
        armor = input.last().split(" ").last().toInt()
    )

    for (weapon in enumValues<Weapon>()) {
        println("---- For $weapon -----")
        val (winning, losing) = weapon.findAllPurchases().partition { it.willWin(boss) }
        val lowestWinning = winning.minByOrNull { it.sumOf { it.cost } }
        val highestLosing = losing.maxByOrNull { it.sumOf { it.cost } }

        println("Lowest Winning: $lowestWinning - cost ${lowestWinning?.sumOf { it.cost }}")
        println("Highest Losing: $highestLosing - cost ${highestLosing?.sumOf { it.cost }}")
    }
}

fun Set<Item>.willWin(boss: Boss, playerHitPoints: Int = 100): Boolean {
    val totalDamage = sumOf { it.damage }
    val totalArmor = sumOf { it.armor }

    val damageInflicted = if (totalDamage <= boss.armor) 1 else totalDamage - boss.armor
    val damageReceived = if (totalArmor >= boss.damage) 1 else boss.damage - totalArmor

    var turnsToDefeatBoss = boss.hitPoints / damageInflicted
    if (boss.hitPoints % damageInflicted != 0) {
        turnsToDefeatBoss++
    }

    var turnsToLoss = playerHitPoints / damageReceived
    if (playerHitPoints % damageReceived != 0) {
        turnsToLoss++
    }

    return turnsToDefeatBoss - 1 < turnsToLoss
}

fun Weapon.findAllPurchases(): Set<Set<Item>> {
    val allPurchases = mutableSetOf<MutableSet<Item>>()

    for (armor in enumValues<Armor>()) {
        allPurchases.add(mutableSetOf(armor))
    }

    allPurchases.addAllRings()
    return allPurchases.onEach { it.add(this) }
}

fun MutableSet<MutableSet<Item>>.addAllRings() {
    toMutableSet().addAllRingsTo(this)
    enumValues<Ring>().forEach { add(mutableSetOf(it)) }
    filter { it.filterIsInstance<Ring>().isNotEmpty() }
        .toMutableSet()
        .addAllRingsTo(this)
}

fun MutableSet<MutableSet<Item>>.addAllRingsTo(allItems: MutableSet<MutableSet<Item>>) {
    for (items in this) {
        for (ring in enumValues<Ring>()) {
            val clone = items.toMutableSet()
            clone.add(ring)
            allItems.add(clone)
        }
    }
}
