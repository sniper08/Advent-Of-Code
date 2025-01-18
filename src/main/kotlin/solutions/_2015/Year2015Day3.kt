package solutions._2015

import Coordinate
import day.Day

class Year2015Day3 : Day {

    /**
     * Houses Visited With Santa
     */
    override fun part1(input: Sequence<String>): String {
        val instruction = input.first()
        val visited = mutableMapOf<Coordinate, Int>().apply { put(Coordinate(0, 0), 1) }

        var x = 0
        var y = 0

        for (direction in instruction) {
            visited.moveToNextHouse(direction, x, y).also {
                x = it.x
                y = it.y
            }
        }

        return getNumberOfHouseVisitedOnce(visited = visited)
    }

    override fun part2(input: Sequence<String>): String {
        val instruction = input.first()
        val visited = mutableMapOf<Coordinate, Int>().apply { put(Coordinate(0, 0), 1) }

        var santaX = 0
        var santaY = 0
        var roboX = 0
        var roboY = 0
        var santaTurn = true

        for (direction in instruction) {
            visited.moveToNextHouse(
                direction,
                if (santaTurn) santaX else roboX,
                if (santaTurn) santaY else roboY
            ).also {
                if (santaTurn) {
                    santaX = it.x
                    santaY = it.y
                } else {
                    roboX = it.x
                    roboY = it.y
                }
            }
            santaTurn = !santaTurn
        }

        return getNumberOfHouseVisitedOnce(visited = visited)
    }

    private fun MutableMap<Coordinate, Int>.moveToNextHouse(direction: Char, currentX: Int, currentY: Int): Coordinate {
        val north = '^'
        val south = 'v'
        val east = '>'
        val west = '<'

        var x = currentX
        var y = currentY

        when (direction) {
            north -> y--
            south -> y++
            east -> x++
            west -> x--
        }

        val nextHouse = Coordinate(x, y)
        val current = getOrDefault(nextHouse, 0)

        put(nextHouse, current + 1)
        return nextHouse
    }

    private fun getNumberOfHouseVisitedOnce(visited: MutableMap<Coordinate, Int>): String = "${visited.values.count { it >= 1 }}"
}
