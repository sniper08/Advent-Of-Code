package solutions._2015

enum class MFCSAM(val key: String, val value: Int) {
    CHILDREN("children", 3),
    CATS("cats", 7),
    SAMOYEDS("samoyeds", 2),
    POMERANIANS("pomeranians", 3),
    AKITAS("akitas", 0),
    VIZSLAS("vizslas", 0),
    GOLDFISH("goldfish", 5),
    TREES("trees", 3),
    CARS("cars", 2),
    PERFUMES("perfumes", 1)
}

data class AuntSue(
    val number: Int,
    val children: Int?,
    val cats: Int?,
    val samoyeds: Int?,
    val pomeranians: Int?,
    val akitas: Int?,
    val vizslas: Int?,
    val goldfish: Int?,
    val trees: Int?,
    val cars: Int?,
    val perfumes: Int?
) {
    companion object {
        fun createFrom(input: String): AuntSue {
            val split = input.replace(",","")
                .replace(":","")
                .split(" ")

            return AuntSue(
                number = split[1].toInt(),
                children = split.findValue(MFCSAM.CHILDREN.key),
                cats = split.findValue(MFCSAM.CATS.key),
                samoyeds = split.findValue(MFCSAM.SAMOYEDS.key),
                pomeranians = split.findValue(MFCSAM.POMERANIANS.key),
                akitas = split.findValue(MFCSAM.AKITAS.key),
                vizslas = split.findValue(MFCSAM.VIZSLAS.key),
                goldfish = split.findValue(MFCSAM.GOLDFISH.key),
                trees = split.findValue(MFCSAM.TREES.key),
                cars = split.findValue(MFCSAM.CARS.key),
                perfumes = split.findValue(MFCSAM.PERFUMES.key)
            )
        }
    }

    fun containsOrUnknown(mfcsam: MFCSAM) =
        when(mfcsam) {
            MFCSAM.CHILDREN -> children == MFCSAM.CHILDREN.value || children == null
            MFCSAM.CATS -> cats == MFCSAM.CATS.value || cats == null
            MFCSAM.SAMOYEDS -> samoyeds == MFCSAM.SAMOYEDS.value || samoyeds == null
            MFCSAM.POMERANIANS -> pomeranians == MFCSAM.POMERANIANS.value || pomeranians == null
            MFCSAM.AKITAS -> akitas == MFCSAM.AKITAS.value || akitas == null
            MFCSAM.VIZSLAS -> vizslas == MFCSAM.VIZSLAS.value || vizslas == null
            MFCSAM.GOLDFISH -> goldfish == MFCSAM.GOLDFISH.value || goldfish == null
            MFCSAM.TREES -> trees == MFCSAM.TREES.value || trees == null
            MFCSAM.CARS -> cars == MFCSAM.CARS.value || cars == null
            MFCSAM.PERFUMES -> perfumes == MFCSAM.PERFUMES.value || perfumes == null
        }

    fun containsGreaterThanOrUnknown(mfcsam: MFCSAM) =
        when (mfcsam) {
            MFCSAM.CATS -> cats == null || cats > MFCSAM.CATS.value
            MFCSAM.TREES -> trees == null || trees > MFCSAM.TREES.value
            else -> false
        }

    fun containsLessThanOrUnknown(mfcsam: MFCSAM) =
        when (mfcsam) {
            MFCSAM.POMERANIANS -> pomeranians == null || pomeranians < MFCSAM.POMERANIANS.value
            MFCSAM.GOLDFISH -> goldfish == null || goldfish < MFCSAM.GOLDFISH.value
            else -> false
        }
}

fun List<String>.findValue(string: String): Int? {
    val index = indexOf(string)

    return if (index > -1) {
        get(index + 1).toInt()
    } else {
        null
    }
}

fun findWhichAunt(input: Sequence<String>) {
    var final = input.toList().map { AuntSue.createFrom(it) }

    for (scanned in MFCSAM.values()) {
        final = final.filter { it.containsOrUnknown(scanned) }
    }

    final.forEach { println(it) }
}

fun findWhichAuntEnhanced(input: Sequence<String>) {
    var final = input.toList().map { AuntSue.createFrom(it) }

    for (scanned in MFCSAM.values()) {
        final = final.filter {
            when (scanned) {
                MFCSAM.CATS, MFCSAM.TREES -> it.containsGreaterThanOrUnknown(scanned)
                MFCSAM.POMERANIANS, MFCSAM.GOLDFISH -> it.containsLessThanOrUnknown(scanned)
                else -> it.containsOrUnknown(scanned)
            }
        }
    }

    final.forEach { println(it) }
}


