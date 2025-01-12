package solutions._2024

import day.Day
import solutions._2024.Year2024Day9.Package.Empty
import solutions._2024.Year2024Day9.Package.File

class Year2024Day9 : Day {

    /**
     * Checksum of file idNumber * id in array after moving idNumbers one by one
     */
    override fun part1(input: Sequence<String>): String {
//        var currentIndex = 0
//        val fileBlockArrangement = mutableListOf<Package>()
//        input.first()
//            .forEachIndexed { index, rawChar ->
//                val size = rawChar.digitToInt().toLong()
//
//                if (index % 2 == 0) {
//                    repeat(size.toInt()) {
//                        fileBlockArrangement.add(
//                            File(id = index / 2, index = currentIndex, size = 1)
//                        )
//                        currentIndex++
//                    }
//                } else {
//                    repeat(size.toInt()) {
//                        fileBlockArrangement.add(
//                            Empty(index = currentIndex, size = 1)
//                        )
//                        currentIndex++
//                    }
//                }
//            }
//
//        var lastFile = fileBlockArrangement.last { it is File } as File
//        var firstEmpty = fileBlockArrangement.firstOrNull { it is Empty }
//
//        while (firstEmpty != null && firstEmpty.index < lastFile.index) {
//            fileBlockArrangement.addFile(index = firstEmpty.index, fromFile = lastFile)
//            fileBlockArrangement.removeLastFileArAddEmpty(file = lastFile)
//
//            lastFile = fileBlockArrangement.last { it is File } as File
//            firstEmpty = fileBlockArrangement.firstOrNull { it is Empty }
//        }
//
//        return "${fileBlockArrangement.checkSum()}"
        val fileBlockArrangement = mutableListOf<Long>()
        input.first()
            .forEachIndexed { id, rawChar ->
                val digit = rawChar.digitToInt().toLong()

                if (id % 2 == 0) {
                    repeat(digit.toInt()) {
                        fileBlockArrangement.add((id / 2).toLong())
                    }
                } else {
                    repeat(digit.toInt()) {
                        fileBlockArrangement.add(-1)
                    }
                }
            }

        var indexOfLastDigit = fileBlockArrangement.indexOfLast { it >= 0 }
        var indexOfFirstEmpty = fileBlockArrangement.indexOfFirst { it < 0 }

        while (indexOfLastDigit > indexOfFirstEmpty) {
            fileBlockArrangement[indexOfFirstEmpty] = fileBlockArrangement.removeAt(indexOfLastDigit)

            indexOfLastDigit = fileBlockArrangement.indexOfLast { it >= 0 }
            indexOfFirstEmpty = fileBlockArrangement.indexOfFirst { it < 0 }
        }

        val checkSum = fileBlockArrangement.foldIndexed(0L) { index, acc, fileId ->
            if (fileId >= 0L) {
                acc + (index * fileId)
            } else {
                acc
            }
        }

        return "$checkSum"
    }

    /**
     * Checksum of file idNumber * id in array after moving files
     */
    override fun part2(input: Sequence<String>): String {
        val fileBlockArrangement = input
            .first()
            .mapIndexed { index, rawChar ->
                val size = rawChar.digitToInt().toLong()

                if (index % 2 == 0) {
                    File(id = index / 2, index = index, size = size)
                } else {
                    Empty(index = index, size = size)
                }
            }
            .toMutableList()

        fileBlockArrangement
            .filterIsInstance<File>()
            .reversed()
            .forEach { file ->
                val firstEmptyCanHoldFile = fileBlockArrangement.firstOrNull { it is Empty && it.size >= file.size }

                if (firstEmptyCanHoldFile != null && firstEmptyCanHoldFile.index < file.index) {
                    val diff = firstEmptyCanHoldFile.size - file.size
                    val nextToFirstEmptyCanHoldFile = fileBlockArrangement[firstEmptyCanHoldFile.index + 1]

                    fileBlockArrangement.addFile(index = firstEmptyCanHoldFile.index, fromFile = file)
                    fileBlockArrangement.removeLastFileOrAddEmpty(file = file)

                    when {
                        diff > 0 && nextToFirstEmptyCanHoldFile is Empty -> nextToFirstEmptyCanHoldFile.size += diff
                        diff > 0 -> {
                            val indexToAdd = firstEmptyCanHoldFile.index + 1

                            fileBlockArrangement.add(
                                index = indexToAdd,
                                element = Empty(index = indexToAdd, size = diff)
                            )

                            fileBlockArrangement
                                .drop(indexToAdd + 1)
                                .forEach { it.index++ }
                        }
                    }
                }
            }

        return "${fileBlockArrangement.checkSum()}"
    }

    private sealed class Package {
        abstract var index: Int
        abstract val size: Long

        class Empty(
            override var index: Int,
            override var size: Long
        ) : Package() {
            override fun toString(): String = "."
        }

        data class File(
            val id: Int,
            override var index: Int,
            override var size: Long
        ) : Package() {
            override fun toString(): String = "$id"
        }
    }

    private fun MutableList<Package>.addFile(index: Int, fromFile: File) {
        this[index] = File(
            id = fromFile.id,
            index = index,
            size = fromFile.size
        )
    }

    private fun MutableList<Package>.removeLastFileOrAddEmpty(file: File) {
        if (file.index == lastIndex) {
            removeLast()
        } else {
            this[file.index] = Empty(index = file.index, size = file.size)
        }
    }

    private fun List<Package>.checkSum(): Long {
        var currentIndex = 0

        return this.fold(0L) { acc, slot ->
            when (slot) {
                is Empty -> {
                    currentIndex += slot.size.toInt()
                    acc
                }
                is File -> {
                    val slotCheckSum = (1..slot.size).fold(0L) { slotAcc, _ ->
                        slotAcc + (currentIndex * slot.id)
                            .also { currentIndex++ }
                    }
                    acc + slotCheckSum
                }
            }
        }
    }

    private fun List<Package>.print() {
        println(
            joinToString("") { slot ->
                String(
                    chars = CharArray(slot.size.toInt()) {
                        slot.toString().first()
                    }
                )
            }
        )
    }
}