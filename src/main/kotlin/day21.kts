import Utils.Companion.toward

data class Player(val id: Int, var space: Int, var score : Int = 0) {
    fun progressSpaces(spaces: Int) {
        val actualSpaces = spaces % 10

        if (actualSpaces > 0) {
            actualSpaces.toward(1).forEach { _ ->
                if (++space == 11) space = 1
            }
        }
        score += space
    }

    override fun toString(): String {
        return "Player $id at $space with $score"
    }
}

class Die(private var pip: Int = 0, private val upper: Int = 100) {
    var totalRolls : Long = 0

    fun roll(n: Int): Int {
        return n.toward(1).sumOf { rollOnce() }
    }

    private fun rollOnce() : Int {
        totalRolls++
        return if (pip == upper) {
            pip = 1
            pip
        } else {
            ++pip
        }
    }

    override fun toString(): String {
        return "Die showing $pip with $totalRolls"
    }
}
val match = Regex("""Player (\d+) starting position: (\d+)""")
val input : List<Pair<Int, Int>> = Utils.readFileAsList("day21") { line ->
    val (num, pos) = match.find(line)?.destructured!!
    Pair(num.toInt(), pos.toInt())
}
val players : List<Player> = input.map { Player(it.first, it.second) }

println(players)

var currentPlayerIndex = 0
val die = Die()
while(true) {
    val curPlayer = players[currentPlayerIndex]
    curPlayer.progressSpaces(die.roll(3))
    if (curPlayer.score >= 1000) break
    currentPlayerIndex = (currentPlayerIndex + 1) % players.size
}

println(players.minOf { it.score } * die.totalRolls)

/**
 * The universe of roll sums is 27 large, but there are only 7 distinct outcomes:
 * 1x 3 (1 + 1 + 1)x1
 * 3x 4 (1 + 1 + 2)x3
 * 6x 5 (1 + 1 + 3)x3 | (2 + 2 + 1)x3
 * 7x 6 (1 + 2 + 3)x6 | (2 + 2 + 2)x1
 * 6x 7 (2 + 2 + 3)x3 | (3 + 3 + 1)x3
 * 3x 8 (3 + 3 + 2)x3
 * 1x 9 (3 + 3 + 3)x1
 *
 * A board state is a combination of
 *   whose turn it is
 *   position of each player
 *   score of each player
 *
 * Each "universe" move translates from one bucket to another, and the initial states are seeded by the starting positions
 * Each translation multiplies the number of universes using that bucket in the next game loop
 *
 * In each Player State universe we want to track the number of game instances with the same state
 *   and if we encounter duplicates add their multipliers together to track fewer iterations
 *
 * Play until all games are over, count winners and answer question
 */

data class BoardState(val turn: Int, val firstPos: Int, val secondPos: Int, val firstScore: Int, val secondScore: Int) {
    fun next(moveSpaces: Int) : BoardState {
        return if (turn > 0) {
            val nextSpace = firstPos + moveSpaces
            val actualNextSpace = if (nextSpace > 10) nextSpace % 10 else nextSpace
            BoardState(-1, actualNextSpace, secondPos, firstScore + actualNextSpace, secondScore)
        } else {
            val nextSpace = secondPos + moveSpaces
            val actualNextSpace = if (nextSpace > 10) nextSpace % 10 else nextSpace
            BoardState(1, firstPos, actualNextSpace, firstScore, secondScore + actualNextSpace)
        }
    }

    fun diff() : Int {
        return firstScore - secondScore
    }

    fun keepsPlaying() : Boolean {
        return firstScore < 21 && secondScore < 21
    }
}

data class Transistor(val mult: Long, val spaces: Int)
val allTransistors = listOf(
    Transistor(1, 3),
    Transistor(3, 4),
    Transistor(6, 5),
    Transistor(7, 6),
    Transistor(6, 7),
    Transistor(3, 8),
    Transistor(1, 9),
)

var gameMap = mutableMapOf(Pair(BoardState(1, input.first().second, input.last().second, 0, 0), 1L))

var done = false
while (!done) {
    done = true
    val nextGameMap = mutableMapOf<BoardState, Long>()
    gameMap.forEach { (boardState, mult) ->
        if (boardState.keepsPlaying()) {
            done = false
            allTransistors.forEach { transistor ->
                val nextBoardState = boardState.next(transistor.spaces)
                val multProp = mult * transistor.mult
                nextGameMap[nextBoardState] = nextGameMap[nextBoardState]?.let { it + multProp } ?: multProp
            }
        }
        else {
            nextGameMap[boardState] = nextGameMap[boardState]?.let { it + mult } ?: mult
        }
    }
    gameMap = nextGameMap
}

val scores = gameMap
    .map { Pair(it.key, it.value) }
    .groupBy { it.first.diff().compareTo(0) }
    .map { Pair(it.key, it.value.map{ it.second }.fold(0, Long::plus)) }

println(scores.maxOf { it.second })