package solutions._2015

import java.util.*

const val HIT_POINTS = 50
const val MANA = 500

enum class Effect(val value: Int, val turns: Int) {
    SHIELD(7, 6),
    POISON(3, 6),
    RECHARGE(101, 5)
}

enum class Spell(
    val mana: Int,
    val damage: Int = 0,
    val heal: Int = 0,
    val effect: Effect? = null
) {
    MAGIC_MISSILE(53, 4),
    DRAIN(73, 2, heal = 2),
    SHIELD(113, effect = Effect.SHIELD),
    POISON(173, effect = Effect.POISON),
    RECHARGE(229, effect = Effect.RECHARGE)
}

data class WizardSimulator(
    var hitPoints: Int = HIT_POINTS,
    var mana: Int = MANA,
    var armor: Int = 0,
    var bossHitPoints: Int,
    val bossDamage: Int,
    var shieldActive: Int = 0,
    var poisonActive: Int = 0,
    var rechargeActive: Int = 0,
    var manaSpent: Int = 0
) {
    companion object {
        fun createFrom(wizardSimulator: WizardSimulator) = WizardSimulator(
            wizardSimulator.hitPoints,
            wizardSimulator.mana,
            wizardSimulator.armor,
            wizardSimulator.bossHitPoints,
            wizardSimulator.bossDamage,
            wizardSimulator.shieldActive,
            wizardSimulator.poisonActive,
            wizardSimulator.rechargeActive,
            wizardSimulator.manaSpent
        )
    }

    fun generateNextPossible(hardDifficulty: Boolean): Set<WizardSimulator> {
        val next = mutableSetOf<WizardSimulator>()

        if (hardDifficulty) {
            hitPoints--
        }

        if (hitPoints > 0) {
            for (spell in enumValues<Spell>()) {
                val afterTurn = createFrom(this)
                if (afterTurn.cantCast(spell)) continue
                afterTurn.rechargeIfPossible()

                if (mana >= spell.mana) {
                    afterTurn.playerTurn(spell)
                    afterTurn.bossTurn()
                    next.add(afterTurn)
                }
            }
        }

        return next
    }

    fun hasWon() = bossHitPoints <= 0
    fun hasLost() = hitPoints <= 0

    private fun bossTurn() {
        shieldIfPossible()
        rechargeIfPossible()
        poisonIfPossible()

        if (bossHitPoints > 0) {
            hitPoints -= bossDamage - armor
        }
    }

    private fun playerTurn(spell: Spell) {
        shieldIfPossible()
        poisonIfPossible()

        bossHitPoints -= spell.damage
        hitPoints += spell.heal
        when (val effect = spell.effect) {
            Effect.SHIELD -> shieldActive = effect.turns
            Effect.POISON -> poisonActive = effect.turns
            Effect.RECHARGE -> rechargeActive = effect.turns
        }
        mana -= spell.mana
        manaSpent += spell.mana
    }

    private fun cantCast(spell: Spell) = spell == Spell.SHIELD && shieldActive > 1
        || spell == Spell.POISON && poisonActive > 1
        || spell == Spell.RECHARGE && rechargeActive > 1

    private fun rechargeIfPossible() {
        if (rechargeActive > 0) {
            mana += Effect.RECHARGE.value
            rechargeActive--
        }
    }

    private fun poisonIfPossible() {
        if (poisonActive > 0) {
            bossHitPoints -= Effect.POISON.value
            poisonActive--
        }
    }

    private fun shieldIfPossible() {
        if (shieldActive > 0) {
            armor = Effect.SHIELD.value
            shieldActive--
        } else {
            armor = 0
        }
    }
}

fun calculateLowestManaSpent(input: Sequence<String>) {
    val starting = WizardSimulator(
        bossHitPoints = input.first().split(" ").last().toInt(),
        bossDamage = input.last().split(" ").last().toInt()
    )

    val pq = PriorityQueue<WizardSimulator> { a, b ->
        when {
            (a?.manaSpent ?: 0) < (b?.manaSpent ?: 0) -> -1
            (a?.manaSpent ?: 0) > (b?.manaSpent ?: 0) -> 1
            else -> 0
        }
    }.apply { addAll(starting.generateNextPossible(hardDifficulty = true)) }

    val winning = mutableSetOf<WizardSimulator>()

    while (winning.isEmpty()) {
        val current = pq.poll()
        current.generateNextPossible(hardDifficulty = true).forEach {
            when {
                it.hasWon() -> winning.add(it)
                it.hasLost() -> { /* skip */ }
                else -> pq.add(it)
            }
        }
    }

    println(winning.first().manaSpent)
}




