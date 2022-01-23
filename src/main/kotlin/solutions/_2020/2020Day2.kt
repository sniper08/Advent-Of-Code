package solutions._2020

fun countValidPasswords(passwords: Sequence<String>) = passwords
    .map { it.split(" ") }
    .map { Triple(it[0].split("-"), it[1], it[2]) }
    .count { password ->
        val characterCount = password.third.count { it == password.second[0] }

        characterCount in password.first[0].toInt()..password.first[1].toInt()
    }

fun countValidPasswordsCorrected(passwords: Sequence<String>) = passwords
    .map { it.split(" ") }
    .map { Triple(it[0].split("-"), it[1], it[2]) }
    .count { (limits, character, password) ->
        val min = limits[0].toInt()
        val max = limits[1].toInt()

        val char = character[0]
        val first = password[min - 1]
        val second = password[max - 1]

        first == char && second != char || first != char && second == char
    }
