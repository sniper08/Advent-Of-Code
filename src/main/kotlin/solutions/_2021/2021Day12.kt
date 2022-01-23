package solutions._2021

const val START = "start"
const val END = "end"

class Destination(val from: String) {
    val to = mutableSetOf<String>()
    var visited: Boolean = false

    fun add(connection: String) {
        to.add(connection)
    }
}

fun createDestinations(input: Sequence<String>): Map<String, Destination> {
    val destinations = mutableMapOf<String, Destination>().apply {
        put(START, Destination(START))
    }
    input.toList()
        .map { it.split('-') }
        .forEach {
            val from = it[0]
            val to = it[1]

            when {
                from == START -> destinations[START]?.add(to)
                from != END -> {
                    if (!destinations.containsKey(from)) {
                        destinations[from] = Destination(from)
                    }
                    if (to != START) {
                        destinations[from]?.add(to)
                    }
                    if (to != END) {
                        if (!destinations.containsKey(to)) {
                            destinations[to] = Destination(to)
                        }
                        destinations[to]?.add(from)
                    }
                }
                else -> {
                    if (!destinations.containsKey(to)) {
                        destinations[to] = Destination(to)
                    }
                    destinations[to]?.add(from)
                }
            }
        }
    return destinations
}

fun calculateRoutes(input: Sequence<String>): Int {
    val destinations = createDestinations(input)
    val routes = mutableListOf<List<String>>()

    destinations[START]?.to?.forEach { connection ->
        val route = mutableListOf<String>()
        route.add(START)

        routes.addAll(findRoutesCorrected(connection, route, routes, destinations))
    }

    return routes.count { it.last() == END }
}

fun findRoutes(
    connection: String,
    startRoute: List<String>,
    routes: MutableList<List<String>>,
    destinations: Map<String, Destination>
) : List<List<String>> {
    val connectionRoute = mutableListOf<String>().apply { addAll(startRoute) }
    val connectionRoutes = mutableListOf<List<String>>()
    val endingRoutes = mutableListOf<List<String>>()

    if (connection == END) {
        connectionRoute.add(connection)
        endingRoutes.add(connectionRoute)
    } else {
        destinations[connection]?.to?.let { destinationsForConnection ->
            if (connection.first().isUpperCase()) {
                connectionRoute.add(connection)
            } else {
                if (!startRoute.contains(connection)) {
                    connectionRoute.add(connection)
                }
            }

            if (startRoute != connectionRoute) {
                if (routes.isNotEmpty()) {
                    routes.removeFirst()
                }
                repeat(destinationsForConnection.size) { connectionRoutes.add(connectionRoute) }
                for (destination in destinationsForConnection) {
                    endingRoutes.addAll(findRoutes(destination, connectionRoute, connectionRoutes, destinations))
                }
            }
        }

        routes.addAll(connectionRoutes)
    }
    return endingRoutes
}

fun findRoutesCorrected(
    connection: String,
    startRoute: List<String>,
    routes: MutableList<List<String>>,
    destinations: Map<String, Destination>
) : List<List<String>> {
    val connectionRoute = mutableListOf<String>().apply { addAll(startRoute) }
    val connectionRoutes = mutableListOf<List<String>>()
    val endingRoutes = mutableListOf<List<String>>()

    if (connection == END) {
        connectionRoute.add(connection)
        endingRoutes.add(connectionRoute)
    } else {
        destinations[connection]?.to?.let { destinationsForConnection ->
            if (connection.first().isUpperCase()) {
                connectionRoute.add(connection)
            } else {
                val lowerCaseExisting =
                    connectionRoute.filter { it != START && it != END && it.all { it.isLowerCase() } }
                        .groupingBy { it }
                        .eachCount()

                when {
                    lowerCaseExisting[connection] == null -> connectionRoute.add(connection)
                    lowerCaseExisting[connection] == 1 -> {
                        if (lowerCaseExisting.none { it.value == 2 }) {
                            connectionRoute.add(connection)
                        }
                    }
                }
            }

            if (startRoute != connectionRoute) {
                repeat(destinationsForConnection.size) { connectionRoutes.add(connectionRoute) }
                for (destination in destinationsForConnection) {
                    endingRoutes.addAll(findRoutesCorrected(destination, connectionRoute, connectionRoutes, destinations))
                }
            }
        }

        routes.addAll(connectionRoutes)
    }
    return endingRoutes
}


