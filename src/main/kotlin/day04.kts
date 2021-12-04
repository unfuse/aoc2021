data class Point(val x: Int, val y: Int) : Comparable<Point> {
    override fun compareTo(other: Point): Int {
        val xCompare = this.x.compareTo(other.x)
        return if (xCompare != 0) xCompare else this.y.compareTo(other.y)
    }

    override fun toString(): String {
        return "($x, $y)"
    }
}

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
    val board: Map<Int, Cell>
    val allCells: Set<Cell>
    var checked: MutableList<Cell> = mutableListOf()

    init {
        var y = 0
        val _board: MutableMap<Int, Cell> = mutableMapOf()
        input.lines().forEach { line ->
            var x = 0
            line.trim().replace(Regex(" +"), ",").split(",").forEach { cell ->
                val num = cell.toInt()
                _board.put(num, Cell(Point(x, y), num))
                x++
            }
            y++
        }
        this.board = _board.toMap()
        this.allCells = _board.values.toSet()
    }

    fun check(num: Int) {
        board[num]?.let { checked.add(it) }
    }

    fun isWinCondition() : Boolean {
        if (checked.isEmpty()) {
            return false
        }
        // Win must occur with last cell to be played
        val lastCell: Cell = checked.last()
        val xList = checked.filter { it.point.x == lastCell.point.x }.sorted()
        val yList = checked.filter { it.point.y == lastCell.point.y }.sorted()

        return xList.size == 5 || yList.size == 5;
    }

    fun score() : Int {
        if (isWinCondition()) {
            val lastCell: Cell = checked.last()
            val unchecked: List<Int> = allCells.filter { !checked.contains(it) }.map { it.num }
            return unchecked.sum() * lastCell.num
        }
        return -1
    }

    override fun toString(): String {
        var string = "\n"
        allCells.sorted().windowed(5, 5).forEach { row ->
            row.forEach { cell ->
                if (cell.num < 10) {
                    string += " "
                }
                string += cell.num
                if (checked.contains(cell)) {
                    string += "*"
                }
                else {
                    string += " "
                }
                string += " "
            }
            string += "\n"
        }
        return string
    }
}

val input = Utils.readFileSplitByNewlines("day04")

// first line contains ordered bingo numbers
val numbers: List<Int> = input[0].split(",").map { it.toInt() }

// remaining lines are boards
var i = 1
val boards: List<Board> = input.subList(1, input.size).map { Board(it, i++) }

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

val winners = playAllNumbers(numbers,boards)
println(winners.first().score())
println(winners.last().score())