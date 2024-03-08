import kotlinx.serialization.json.*

enum class DirectionArrow(val arrow: Char) {
    NORTH('^'),
    SOUTH('v'),
    WEST('<'),
    EAST('>');

    companion object {
        fun from(value: Char) = when (value) {
            NORTH.arrow -> NORTH
            WEST.arrow -> WEST
            EAST.arrow -> EAST
            SOUTH.arrow -> SOUTH
            else -> throw Exception("Invalid char value")
        }
    }
}

fun JsonElement.getArray() = try { jsonArray } catch (e: Exception) { null }
fun JsonElement.getObject() = try { jsonObject } catch (e: Exception) { null }
fun JsonElement.getStringValue() = try { jsonPrimitive.content } catch (e: Exception) { null }
fun JsonElement.getIntValue() = try { jsonPrimitive.intOrNull } catch (e: Exception) { null }

data class Coordinate(val x: Int, val y: Int) {
    override fun toString(): String = "($y,$x)"
}

data class UpdateCoordinate(var x: Int, var y: Int) {
    override fun toString(): String = "($y,$x)"
}

data class LongUpdateCoordinate(var x: Long, var y: Long) {
    override fun toString(): String = "($y,$x)"
}

enum class CDirection { NORTH, SOUTH, WEST, EAST }

enum class Direction { R, L, U, D }

val doNothing = { }

const val ANSI_RESET = "\u001B[0m"
const val ANSI_BLACK = "\u001B[30m"
const val ANSI_RED = "\u001B[31m"
const val ANSI_GREEN = "\u001B[32m"
const val ANSI_YELLOW = "\u001B[33m"
const val ANSI_BLUE = "\u001B[34m"
const val ANSI_PURPLE = "\u001B[35m"
const val ANSI_CYAN = "\u001B[36m"
const val ANSI_WHITE = "\u001B[37m"

const val ANSI_BLACK_BACKGROUND = "\u001B[40m"
const val ANSI_RED_BACKGROUND = "\u001B[41m"
const val ANSI_GREEN_BACKGROUND = "\u001B[42m"
const val ANSI_YELLOW_BACKGROUND = "\u001B[43m"
const val ANSI_BLUE_BACKGROUND = "\u001B[44m"
const val ANSI_PURPLE_BACKGROUND = "\u001B[45m"
const val ANSI_CYAN_BACKGROUND = "\u001B[46m"
const val ANSI_WHITE_BACKGROUND = "\u001B[47m"