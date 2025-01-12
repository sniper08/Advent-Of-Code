package solutions._2024

import day.Day
import kotlin.math.abs

class Year2024Day2 : Day {

    /**
     * Find safe reports
     */
    override fun part1(input: Sequence<String>): String {
        val reports = input.map {
            Report.create(rawReport = it)
        }

        val safeReports = reports.sumOf { report ->
            if (report.isItSafe()) 1 else 0L
        }

        return "$safeReports"
    }

    /**
     * Find safe reports allowing to remove one bad level
     */
    override fun part2(input: Sequence<String>): String {
        val reports = input.map {
            Report.create(rawReport = it)
        }

        val safeReports = reports.sumOf { report ->
            var safe = report.isItSafe()
            var indexToRemove = 0

            while (!safe && indexToRemove <= report.levels.lastIndex) {
                val modifiedReport = Report(
                    levels = report.levels
                        .toMutableList()
                        .apply { removeAt(indexToRemove) }
                )

                safe = modifiedReport.isItSafe()
                indexToRemove++
            }

            if (safe) 1 else 0L
        }

        return "$safeReports"
    }

    data class Report(
        val levels: List<Int>
    ) {
        companion object {
            fun create(rawReport: String): Report {
                val split = rawReport.split(" ")

                return Report(
                    levels = split.map { it.toInt() }
                )
            }
        }

        fun isItSafe(): Boolean {
            var increasing = false

            for ((i, level) in levels.withIndex()) {
                val nextLevel = levels.getOrNull(i + 1) ?: break
                val diff = level - nextLevel

                when {
                    abs(diff) !in 1..3 -> return false
                    i > 0 && increasing != diff > 0 -> return false
                    else -> increasing = diff > 0
                }
            }

            return true
        }
     }
}
