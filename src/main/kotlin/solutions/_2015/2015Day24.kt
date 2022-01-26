package solutions._2015

fun calculateSmallestQuantumElement(input: Sequence<String>) {
    val weights = input
        .toList()
        .reversed()
        .map { it.toInt() }
        .toSet()
    val roomWeight = (weights.sum() / 4).toLong()

    val allFirstRoom = mutableSetOf<Set<Long>>()

    var subListSize = 2

    while (allFirstRoom.isEmpty()) {
        weights.findAllFirstGroup(
            allFirstRoom, subListSize, roomWeight, 0, MutableList(subListSize) { 0L }, 0
        )
        subListSize++
    }

    allFirstRoom
        .map { it to it.reduce(Long::times) }
        .onEach { println("${it.first} - QE: ${it.second}") }
        .minByOrNull { it.second }
        .let { println("Lowest ${it?.first} - QE: ${it?.second}") }
}

fun Set<Int>.findAllFirstGroup(
    all: MutableSet<Set<Long>>,
    subListSize: Int,
    roomWeight: Long,
    indexTemp: Int,
    temp: MutableList<Long>,
    index: Int
) {
    if (indexTemp == subListSize && temp.all { it != 0L }) {
        if (temp.sum() == roomWeight) {
            all.add(temp.toSet())
        }
        return
    }

    if (index >= this.size) return

    temp[indexTemp] = elementAt(index).toLong()
    this.findAllFirstGroup(all, subListSize, roomWeight, indexTemp + 1, temp, index + 1)
    this.findAllFirstGroup(all, subListSize, roomWeight, indexTemp, temp, index + 1)
}

