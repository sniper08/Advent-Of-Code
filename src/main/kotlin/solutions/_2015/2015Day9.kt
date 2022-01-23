package solutions._2015

import java.util.*

data class City(val name: String)
data class RouteDijsktra(val set: Set<City>)
data class RouteCost(val route: RouteDijsktra, val cost: Int)

data class Route(val set: MutableSet<City>, var cost: Int = 0) {
    fun clone() = Route(mutableSetOf<City>().apply { addAll(set) }, cost)
}

fun calculateCityShortestRoute(input: Sequence<String>) {
    val cities = mutableMapOf<City, MutableMap<City, Int>>().withDefault { mutableMapOf() }

    input.forEach {
        val split = it.split(" ")
        val firstCity = City(split[0])
        val secondCity = City(split[2])

        cities[firstCity] = cities.getValue(firstCity).apply { put(secondCity, split[4].toInt()) }
        cities[secondCity] = cities.getValue(secondCity).apply { put(firstCity, split[4].toInt()) }
    }

    val allRoutes = mutableListOf<Route>()

    cities.forEach {
        val longestRoutes = findAllRoutesPerCity(Route(mutableSetOf(it.key)), cities)
        allRoutes.addAll(longestRoutes)
    }

    println(allRoutes.minByOrNull { it.cost })
    println(allRoutes.maxByOrNull { it.cost })
}

fun findShortestRoutePerCity(city: City, cities: Map<City, MutableMap<City, Int>>): Map.Entry<String, Int> {
    val pq = PriorityQueue<RouteCost> { a, b ->
        when {
            (a?.cost ?: 0) < (b?.cost ?: 0) -> -1
            (a?.cost ?: 0) > (b?.cost ?: 0) -> 1
            else -> 0
        }
    }.apply { add(RouteCost(RouteDijsktra(setOf(city)), cost = 0)) }
    val visited = mutableSetOf<RouteCost>()
    val costs = mutableMapOf<String, Int>().withDefault { Int.MAX_VALUE }

    while (pq.isNotEmpty()) {
        val current = pq.poll()
        visited.add(current)
        cities.getOrDefault(current.route.set.last(), emptyMap()).forEach { route ->
            val nextRoute = RouteDijsktra(current.route.set + setOf(route.key))
            val isVisited = visited.any { it.route.set == nextRoute.set }

            if (!isVisited) {
                val nextCost = current.cost + route.value
                val nextRouteIdentifier = nextRoute.set.joinToString()
                if (costs.getValue(nextRouteIdentifier) > nextCost) {
                    costs[nextRouteIdentifier] = nextCost
                    pq.add(RouteCost(nextRoute, nextCost))
                }
            }
        }
    }

    return costs.filter { it.key.split(",").size == cities.size }.minByOrNull { it.value }!!
}

// Performs better than Dijkstra but list is short
fun findAllRoutesPerCity(route: Route, cities: Map<City, MutableMap<City, Int>>): List<Route> {
    val allRoutes = mutableListOf<Route>()

    cities[route.set.last()]!!.entries.sortedByDescending { it.value }.forEach { nextCity ->
        val cloneRoute = route.clone()
        if (cloneRoute.set.add(nextCity.key)) {
            cloneRoute.cost += nextCity.value
            if (cloneRoute.set.size == cities.size) {
                allRoutes.add(cloneRoute)
            } else {
                allRoutes.addAll(findAllRoutesPerCity(cloneRoute, cities))
            }
        }
    }

    return allRoutes
}
