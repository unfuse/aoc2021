data class Cell(val point: Point, val num: Int) : Comparable<Cell> {
    override fun compareTo(other: Cell): Int {
        return this.point.compareTo(other.point)
    }

    override fun toString(): String {
        return this.num.toString()
    }
}

class Board(input: String, id: Int) {
    val id: Int = id
    private val board: Map<Int, Cell>
    private val allCells: Set<Cell>
    private val checked: MutableList<Cell> = mutableListOf()

    init {
        var y = 0
        val _board: MutableMap<Int, Cell> = mutableMapOf()
        input.lines().forEach { line ->
            var x = 0
            line.trim().replace(Regex(" +"), ",").split(",").forEach { cell ->
                val num = cell.toInt()
                _board[num] = Cell(Point(x, y), num)
                x++
            }
            y++
        }
        this.board = _board.toMap()
        this.allCells = _board.values.toSet()
    }

    fun check(num: Int) {
        board[num]?.let(checked::add)
    }

    fun isWinCondition() : Boolean {
        if (checked.isEmpty()) {
            return false
        }
        // Win must occur with last cell to be played
        val lastCell: Cell = checked.last()
        val xList = checked.filter { it.point.x == lastCell.point.x }
        val yList = checked.filter { it.point.y == lastCell.point.y }

        return xList.size == 5 || yList.size == 5;
    }

    fun score() : Int {
        if (isWinCondition()) {
            return checked.last().num * allCells.filterNot(checked::contains).sumOf { it.num }
        }
        return -1
    }

    override fun toString(): String {
        var string = "\n"
        allCells.sorted().windowed(5, 5).forEach { row ->
            row.forEach { cell ->
                if (cell.num < 10) { string += " " }
                string += cell.num
                string += if (checked.contains(cell)) { "*" } else { " " }
                string += " "
            }
            string += "\n"
        }
        return string
    }
}

val input = Utils.readFileSplitByNewlines("day04")

// first line contains ordered bingo numbers
val numbers: List<Int> = input.first().split(",").map { it.toInt() }

// remaining lines are boards
var i = 1
val boards: List<Board> = input.drop(1).map { Board(it, i++) }

fun playAllNumbers(numbers: List<Int>, boards: List<Board>) : List<Board> {
    val winners: MutableList<Board> = mutableListOf()
    var curBoards = boards.toMutableList()
    for (num in numbers) {
        val nextBoards = curBoards.toMutableList()
        for (board in curBoards) {
            board.check(num)
            if (board.isWinCondition()) {
                // stop playing a board if it wins to preserve the score
                nextBoards.remove(board)
                winners.add(board)
            }
        }
        curBoards = nextBoards
    }

    return winners.toList()
}

val winners = playAllNumbers(numbers, boards)
println(winners.first().score())
println(winners.last().score())