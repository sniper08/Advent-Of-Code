package solutions._2015

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collect
import java.nio.charset.StandardCharsets.UTF_8
import java.security.MessageDigest
import kotlin.coroutines.CoroutineContext
import kotlin.math.max
import kotlin.math.pow

@OptIn(InternalCoroutinesApi::class)
class MD5finder: CoroutineScope {

    companion object {
        const val MAX_PER_SEARCH = 50000L
    }

    private val md5 = MessageDigest.getInstance("MD5")

    private val printFlow = MutableSharedFlow<String>()

    private val currentJobs = mutableSetOf<Job>()

    init {
        launch {
            printFlow.collect { println(it) }
        }
    }

    fun findMinMD5Num(input: String, padding: Int) = runBlocking {
        val maxPerSearch = 10L * (10.0.pow(padding - 1)).toLong()
        repeat(20) {
            currentJobs.add(
                async {
                    findFrom(input, run = it.toLong(), maxPerSearch, padding)
                }
            )
        }
        currentJobs.forEach { it.join() }
        currentJobs.clear()
    }

    private suspend fun findFrom(
        input: String,
        run: Long,
        maxPerSearch: Long,
        padding: Int,
    ) {
        val start = run * maxPerSearch
        val paddingString = CharArray(padding) { '0' }.joinToString("")
        var increment = start

        while (increment < start + maxPerSearch) {
            val newInput = input + increment.toString()
            printFlow.emit(newInput)
            val hash = md5.createMD5Hash(newInput)

            if (hash.startsWith(paddingString)) {
                println("Min number: $increment")
                currentJobs.forEach { it.cancel() }
            } else {
                increment++
            }
        }
    }

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO
}

fun calculateLowestNumberMD5(input: String) {
    MD5finder().findMinMD5Num(input, padding = 5)
}

fun MessageDigest.createMD5Hash(input: String) = digest(input.toByteArray(UTF_8)).joinToString("") { "%02x".format(it) }
