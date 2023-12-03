import kotlinx.serialization.json.*

fun JsonElement.getArray() = try { jsonArray } catch (e: Exception) { null }
fun JsonElement.getObject() = try { jsonObject } catch (e: Exception) { null }
fun JsonElement.getStringValue() = try { jsonPrimitive.content } catch (e: Exception) { null }
fun JsonElement.getIntValue() = try { jsonPrimitive.intOrNull } catch (e: Exception) { null }

data class Coordinate(val x: Int, val y: Int)

val doNothing = { }