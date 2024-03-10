package solutions._2023

import org.jgrapht.alg.StoerWagnerMinimumCut
import org.jgrapht.graph.DefaultEdge
import org.jgrapht.graph.SimpleGraph

fun calculateDisconnectedGroupSizes(input: Sequence<String>) {
    val graph = SimpleGraph<String, DefaultEdge>(DefaultEdge::class.java)
    input.forEach {
        val split = it.split(": ")
        val parent = split[0]
        graph.addVertex(parent)

        val edges = split[1].split(" ")
        for (edge in edges) {
            graph.addVertex(edge)
            graph.addEdge(parent, edge)
        }
    }

    val stoerWagnerMinimumCut = StoerWagnerMinimumCut(graph)
    val minCut = stoerWagnerMinimumCut.minCut().size

    val mul = (graph.vertexSet().size - minCut) * minCut
    println(minCut)
    println(mul)
}



