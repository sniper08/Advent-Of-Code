package solutions._2022

data class File(val name: String, val size: Long)
data class Dir(
    val name: String,
    val dirs: MutableList<Dir> = mutableListOf(),
    val files: MutableList<File> = mutableListOf(),
    val parent: Dir? = null
) {
    fun calculateSize(): Long = files.sumOf { it.size } + dirs.sumOf { it.calculateSize() }
    override fun toString(): String = name
}

fun calculateAllDirSizesAtMost100000(input: Sequence<String>) {
    val mainDir = Dir(name = "/")
    fillMainDir(mainDir, input)

    val dirsAtMost = extractDirsMatching(dir = mainDir, atMost = true, amount = 100000)
    println("$dirsAtMost")
    println("Sum of dirs at most ${dirsAtMost.sumOf { it.size }}")
}

fun findDirToDelete(input: Sequence<String>) {
    val mainDir = Dir(name = "/")
    fillMainDir(mainDir, input)

    val unusedSpace = 70000000 - mainDir.calculateSize()
    val toFreeUp = 30000000 - unusedSpace
    val candidates = extractDirsMatching(dir = mainDir, atMost = false, toFreeUp)

    println("Unused space = $unusedSpace")
    println("To free up = $toFreeUp")
    println("$candidates")
    println("The viable candidate is ${candidates.minBy { it.size }}")
}

fun fillMainDir(mainDir: Dir, input: Sequence<String>) {
    var currentDir = mainDir

    input.forEach {
        val list = it.split(" ")
        val indicator = list[0]
        val name = list[1]

        when (indicator) {
            "$" -> {
                if (name == "cd") {
                    when (val instruction = list[2]) {
                        "/" -> currentDir = mainDir
                        ".." -> currentDir.parent?.let { parent -> currentDir = parent }
                        else -> {
                            currentDir.dirs
                                .firstOrNull { subDir -> subDir.name == instruction }
                                ?.let { subDir ->
                                    currentDir = subDir
                                }
                        }
                    }
                }
            }
            "dir" -> currentDir.dirs.add(Dir(name = name, parent = currentDir))
            else -> currentDir.files.add(File(name = name, size = indicator.toLong()))
        }
    }
}
data class DirSize(val dir: Dir, val size: Long)
fun extractDirsMatching(dir: Dir, atMost: Boolean, amount: Long): List<DirSize> {
    val found = mutableListOf<DirSize>()

    val dirSize = dir.calculateSize()
    val validation = if (atMost) dirSize <= amount else dirSize >= amount

    if (validation) {
        found.add(DirSize(dir, dirSize))
    }

    dir.dirs.forEach {
        found.addAll(extractDirsMatching(it, atMost, amount))
    }

    return found
}



