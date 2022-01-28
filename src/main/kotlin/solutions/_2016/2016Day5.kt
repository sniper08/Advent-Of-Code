package solutions._2016

import kotlinx.coroutines.runBlocking
import solutions._2015.createMD5Hash
import java.security.MessageDigest

fun findMD5Password(input: String) = runBlocking {
    val md5 = MessageDigest.getInstance("MD5")
    val password = mutableListOf<Char>()

    var increment = 0

    while (password.size < 8) {
        val inputToHash = input + increment.toString()
        val hash = md5.createMD5Hash(inputToHash)

        if (hash.startsWith("00000")) {
            password.add(hash[5])
        }
        increment++
    }

    println(increment - 1)
    println("The password is ${password.joinToString("")}")
}

fun findMD5PasswordImproved(input: String) = runBlocking {
    val md5 = MessageDigest.getInstance("MD5")
    val password = MutableList(8) { '_' }

    var increment = 0

    while (password.contains('_')) {
        val inputToHash = input + increment.toString()
        val hash = md5.createMD5Hash(inputToHash)

        if (hash.startsWith("00000")) {
            val index = try { hash[5].digitToInt() } catch (e : Exception) { null }
            if (index != null && index < 8 && password[index] == '_') {
                password[index] = hash[6]
            }
        }
        increment++
    }

    println(increment - 1)
    println("The password is ${password.joinToString("")}")
}