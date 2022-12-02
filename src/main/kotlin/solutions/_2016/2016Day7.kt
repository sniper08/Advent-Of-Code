package solutions._2016

enum class Protocol { TLS, SSL }

class IPv7Address(
    val supernet: MutableSet<String> = mutableSetOf(),
    val hypernet: MutableSet<String> = mutableSetOf()
) {
    companion object {
        fun create(iPv7Address: String) = IPv7Address().apply {
            iPv7Address.split("[", "]")
                .forEachIndexed { i, s ->
                    if (i % 2 == 0) {
                        supernet.add(s)
                    } else {
                        hypernet.add(s)
                    }
                }
        }
    }

    fun supportsTLS() = supernet.firstOrNull { it.hasAbba() } != null
            && hypernet.none { it.hasAbba() }

    fun supportsSSL(): Boolean {
        supernet.forEach { snet ->
            var currentIndex = 0

            while (currentIndex + 2 <= snet.lastIndex) {
                val possibleAba = snet.substring(currentIndex, currentIndex + 3)

                if (possibleAba.isAba()) {
                    val babToFind = possibleAba.toBab()
                    val hNetWithBab = hypernet.firstOrNull { it.contains(babToFind) }

                    if (hNetWithBab != null) {
                        return true
                    }
                }

                currentIndex++
            }
        }

        return false
    }
}

fun findIPv7AddressesSupporting(protocol: Protocol, input: Sequence<String>) {
    var count = 0

    input.forEach { ip ->
        val iPv7Address = IPv7Address.create(ip)
        val validation = when (protocol) {
            Protocol.TLS -> iPv7Address.supportsTLS()
            Protocol.SSL -> iPv7Address.supportsSSL()
        }

        if (validation) count++
    }

    println("Total supporting ${protocol.name}: $count")
}

fun String.hasAbba(): Boolean {
    for ((i, leftSideFirst) in withIndex()) {
        if (i + 3 <= lastIndex) {
            val leftSideSecond = get(i + 1)

            if (leftSideFirst == leftSideSecond) continue

            val rightSideFirst = get(i + 2)

            if (leftSideSecond != rightSideFirst) continue

            val rightSideSecond = get(i + 3)

            if (leftSideFirst == rightSideSecond) {
                return true
            }
        } else {
            return false
        }
    }

    return false
}

fun String.isAba(): Boolean {
    if (length == 3) {
        val left = get(0)
        return left != get(1) && left == get(2)
    }

    return false
}

fun String.toBab(): String {
    if (length == 3) {
        val side = get(1)
        return "$side${get(0)}$side"
    }

    return ""
}