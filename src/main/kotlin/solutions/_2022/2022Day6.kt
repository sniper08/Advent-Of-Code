package solutions._2022

fun findStartOfPacketMarketInProtocol(input: String, markerSize: Int = 4) {
    var index = 0
    var startOfPacketMarker: Int? = null

    while (index <= input.lastIndex - (markerSize - 1)) {
        if (input.substring(index, index + markerSize).toSet().size == markerSize) {
            startOfPacketMarker = index + markerSize
            break
        }

        index++
    }

    println("Start of packet marker: $startOfPacketMarker")
}