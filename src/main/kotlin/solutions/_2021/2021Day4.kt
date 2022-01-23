package solutions._2021

data class NumberOnBoard(val number: Int, var marked: Boolean = false)

fun getDraftNumbers(draftNumbersString: String) = draftNumbersString
    .split(',')
    .map { it.toInt() }

fun getBoards(boards: List<String>) = boards
    .drop(1)
    .mapIndexed { position, boardString -> BingoBoard.create(boardString, position) }

fun calculateBingoFinalScore(input: Sequence<String>) : Int {
    val numbers = getDraftNumbers(input.first())
    val boards = getBoards(input.toList())

    var winningBoardIndex = -1
    var draft = 0

    while (winningBoardIndex == -1) {
        val number = numbers[draft++]

        for (board in boards) {
            if (board.hasWonWithNumber(number, draft)) {
                winningBoardIndex = board.position
                break
            }
        }
    }

    return boards[winningBoardIndex].calculateUnmarkedSum() * numbers[draft - 1]
}

fun calculateLastBingoBoardWin(input: Sequence<String>): Int {
    val numbers = getDraftNumbers(input.first())
    val boards = getBoards(input.toList())

    var draft = 0
    var notWinningBoards = boards

    while (notWinningBoards.size > 1 || !notWinningBoards.first().hasWon) {
        val number = numbers[draft++]

        for (board in notWinningBoards) {
            if (board.hasWonWithNumber(number, draft)) {
                board.hasWon = true
            }
        }

        if(notWinningBoards.size > 1){
            notWinningBoards = notWinningBoards.filterNot { it.hasWon }
        }
    }

    return notWinningBoards.first().calculateUnmarkedSum() * numbers[draft - 1]
}

class BingoBoard private constructor(
    val numbersOnBoard: List<NumberOnBoard>,
    val position: Int,
    var hasWon: Boolean
) {

    companion object {
        private const val MIN_DRAFT_COUNT = 5

        fun create(boardString: String, position: Int) = BingoBoard(
            numbersOnBoard = boardString
                .split('\n')
                .flatMap {
                    it.windowed(size = 2, step = 3) { value -> NumberOnBoard(value.trim().toString().toInt()) }
                },
            position = position,
            hasWon = false
        )
    }

    fun hasWonWithNumber(number: Int, draft: Int): Boolean {
        val indexMarked = markNumber(number)

        return if (draft < MIN_DRAFT_COUNT || indexMarked == -1) {
            false
        } else {
            hasBoardWon(indexMarked)
        }
    }

    fun calculateUnmarkedSum() = numbersOnBoard.sumOf { if (it.marked) 0 else it.number }

    private fun markNumber(number: Int) : Int = numbersOnBoard.indexOfFirst { numberOnBoard ->
        (numberOnBoard.number == number).also { if (it) numberOnBoard.marked = true }
    }

    private fun hasBoardWon(indexMarked: Int) : Boolean {
        val rowIndex = indexMarked / MIN_DRAFT_COUNT
        val yOffSet = indexMarked - (MIN_DRAFT_COUNT * rowIndex)

        return hasWonWithX(rowIndex, indexMarked) || hasWonWithY(yOffSet, indexMarked)
    }

    private fun hasWonWithX(rowIndex: Int, indexMarked: Int): Boolean {
        for (i in 0 until MIN_DRAFT_COUNT) {
            val x = i + (rowIndex * MIN_DRAFT_COUNT)
            if (x == indexMarked) continue
            if (numbersOnBoard[x].marked.not()) return false
        }

        return true
    }

    private fun hasWonWithY(yOffSet: Int, indexMarked: Int): Boolean {
        for (i in 0 until MIN_DRAFT_COUNT) {
            val y = (i * MIN_DRAFT_COUNT) + yOffSet
            if (y == indexMarked) continue
            if (numbersOnBoard[y].marked.not()) return false
        }

        return true
    }
}
