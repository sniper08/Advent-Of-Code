package solutions._2021

private const val NUMBER_OF_DAYS = 80
private const val DAYS_TO_CREATE = 7
private const val JUST_ADDED_DAYS_TO_CREATE = 9

class LanternFishAquarium {

    fun calculateLanternFishTotal(input: String) : Long {
        val initialLanternFishList = getLanternFishList(input)
        val additionsPerDay = calculateInitialAdditions(initialLanternFishList)
        var totalCount: Long = initialLanternFishList.size.toLong()

        for (day in 0 until NUMBER_OF_DAYS) {
            val additionsToday = additionsPerDay[day]
            totalCount += additionsToday

            if (additionsToday > 0) {
                predictCreationDays(
                    initialLanternFishDaysLeft = day + JUST_ADDED_DAYS_TO_CREATE,
                    additions = additionsToday,
                    additionsPerDay = additionsPerDay
                )
            }
        }

        return totalCount
    }

    private fun getLanternFishList(input: String) = input
        .split(',')
        .map { it.toInt() }

    private fun calculateInitialAdditions(initialLanternFishList: List<Int>) : MutableList<Long> {
        val additionsPerDay = MutableList(NUMBER_OF_DAYS) { 0L }
        initialLanternFishList.forEach {
            predictCreationDays(
                initialLanternFishDaysLeft = it,
                additions = 1,
                additionsPerDay = additionsPerDay
            )
        }

        return additionsPerDay
    }

    private fun predictCreationDays(
        initialLanternFishDaysLeft: Int,
        additions: Long,
        additionsPerDay: MutableList<Long>
    ) {
        var indexNextAddition = initialLanternFishDaysLeft

        while (indexNextAddition < additionsPerDay.size) {
            additionsPerDay[indexNextAddition] += additions
            indexNextAddition += DAYS_TO_CREATE
        }
    }
}
