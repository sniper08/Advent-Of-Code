package solutions._2015

sealed class Register(var value: Int = 0) {
    object A: Register(value = 1)
    object B: Register()
    object None: Register()
}

enum class TuringKey(val text: String) {
    HALF("hlf"),
    TRIPLE("tpl"),
    INCREASE("inc"),
    JUMP("jmp"),
    JUMP_EVEN("jie"),
    JUMP_ONE("jio");

    companion object {
        fun from(value: String) = when(value) {
            HALF.text -> HALF
            TRIPLE.text -> TRIPLE
            INCREASE.text -> INCREASE
            JUMP.text -> JUMP
            JUMP_EVEN.text -> JUMP_EVEN
            JUMP_ONE.text -> JUMP_ONE
            else -> HALF // should never happen
        }
    }
}

data class TuringInstruction(val key: TuringKey, val register: Register, val value: Int) {

    fun apply(index: Int): Int =
        when (key) {
            TuringKey.HALF -> {
                register.value /= 2
                index + 1
            }
            TuringKey.TRIPLE -> {
                register.value *= 3
                index + 1
            }
            TuringKey.INCREASE -> {
                register.value++
                index + 1
            }
            TuringKey.JUMP -> index + value
            TuringKey.JUMP_EVEN -> {
                if (register.value % 2 == 0) {
                    index + value
                } else {
                    index + 1
                }
            }
            TuringKey.JUMP_ONE -> {
                if (register.value == 1) {
                    index + value
                } else {
                    index + 1
                }
            }
        }
}

fun calculateTuringLock(input: Sequence<String>) {
    val instructions = input
        .toList()
        .map {
            val split = it.replace(",", "").split(" ")
            val register = when (split[1]) {
                "a" -> Register.A
                "b" -> Register.B
                else -> Register.None
            }
            val value = if (split.first().startsWith("j")) split.last().toInt() else 0
            TuringInstruction(TuringKey.from(split.first()), register, value)
        }

    var indexInstruction = 0

    while (indexInstruction in instructions.indices) {
        indexInstruction = instructions[indexInstruction].apply(indexInstruction)
    }

    println("a = ${Register.A.value}")
    println("b = ${Register.B.value}")
}