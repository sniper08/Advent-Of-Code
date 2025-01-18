package solutions._2024

import day.Day

class Year2024Day23 : Day {

    /**
     * Find count of 3-computer lan parties that contain at least one computer which name starts with "t"
     */
    override fun part1(input: Sequence<String>): String {
        val connections = createConnections(input = input)
        val lanParties = mutableSetOf<LanParty>()

        connections.forEach {
            val firstComputer = it.key
            val possibleNextComputers = it.value

            for (secondComputer in possibleNextComputers) {
                for (possibleThirdComputer in (possibleNextComputers - secondComputer)) {
                    if (connections.getValue(possibleThirdComputer).contains(secondComputer)) {
                        lanParties.add(
                            LanParty(
                                computers = setOf(firstComputer, secondComputer, possibleThirdComputer)
                            )
                        )
                    }
                }
            }
        }

        val possibleNumberWhereChiefHistorianMightBe = lanParties.count { it.contains("t") }

        return "$possibleNumberWhereChiefHistorianMightBe"
    }

    /**
     * Find the largest lan party possible
     */
    override fun part2(input: Sequence<String>): String {
        val connections = createConnections(input = input)

        val alreadyCheckedLanParties = mutableSetOf<LanParty>()
        var largestLanParty = LanParty(computers = setOf())

        var counter = 1
        for (connection in connections) {
            val startComputer = connection.key

            for (connectedComputer in connection.value) {
                val checkedLanParty = LanParty(computers = setOf(startComputer, connectedComputer))

                if (alreadyCheckedLanParties.add(checkedLanParty)) {
                    val foundLargestPerCheckedLanParty = findLargestLanParty(
                        connections = connections,
                        lanParty = LanParty(computers = setOf(startComputer, connectedComputer)),
                        alreadyCheckedLanParties = alreadyCheckedLanParties
                    )

                    if (foundLargestPerCheckedLanParty.computers.size > largestLanParty.computers.size) {
                        largestLanParty = foundLargestPerCheckedLanParty
                    }
                }
            }
            //println("${counter++}: $startComputer DONE $largestLanParty")
        }

        return largestLanParty.toString()
    }

    private fun createConnections(input: Sequence<String>): Map<String, Set<String>> {
        val connections = mutableMapOf<String, MutableSet<String>>()

        input.forEach { rawConnection ->
            val split = rawConnection.split("-")

            connections.getOrPut(key = split.first(), defaultValue = { mutableSetOf() })
                .add(split.last())
            connections.getOrPut(key = split.last(), defaultValue = { mutableSetOf() })
                .add(split.first())
        }

        return connections.mapValues { it.value.toSet() }
    }

    private fun findLargestLanParty(
        connections: Map<String, Set<String>>,
        lanParty: LanParty,
        alreadyCheckedLanParties: MutableSet<LanParty>
    ): LanParty {
        var largestLanParty = lanParty

        val lastConnectedComputer = lanParty.computers.last()
        val possibleNextComputers = connections.getValue(lastConnectedComputer) - lanParty.computers

        for (possibleNextComputer in possibleNextComputers) {
            val checkedLanParty = LanParty(computers = lanParty.computers + possibleNextComputer)

            if (alreadyCheckedLanParties.add(checkedLanParty)) {
                var isConnected = true
                for (alreadyConnectedComputer in lanParty.computers - lastConnectedComputer) {
                    isConnected = connections.getValue(alreadyConnectedComputer).contains(possibleNextComputer)

                    if (isConnected) {
                        alreadyCheckedLanParties.add(
                            LanParty(computers = setOf(alreadyConnectedComputer, possibleNextComputer))
                        )
                    } else {
                        break
                    }
                }

                if (isConnected) {
                    val foundLargestPerCheckedLanParty = findLargestLanParty(
                        connections = connections,
                        lanParty = checkedLanParty,
                        alreadyCheckedLanParties = alreadyCheckedLanParties
                    )

                    if (foundLargestPerCheckedLanParty.computers.size > largestLanParty.computers.size) {
                        largestLanParty = foundLargestPerCheckedLanParty
                    }
                }
            }
        }

        return largestLanParty
    }

    data class LanParty(
        val computers: Set<String>,
    ) {
        private val sorted = computers.sorted()

        fun contains(value: String) = computers.any { it.startsWith(value) }

        override fun equals(other: Any?): Boolean {
            if (other !is LanParty) return false

            return other.sorted == sorted
        }

        override fun toString(): String = sorted.joinToString(",") { it }

        override fun hashCode(): Int {
            return toString().hashCode()
        }
    }
}