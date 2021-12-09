val input: Grid<Int> = Utils.readFileAsGrid("day09", { line -> line.chunked(1) }) { cell -> cell.toInt() }

val adjPoints: List<Pair<Int, Int>> = listOf(-1, 1, 0, 0).zip(listOf(0, 0, 1, -1))
val adjPointsMapper: (point: Point) -> List<Point> = { point ->
    adjPoints.map { Point(point.x + it.first, point.y + it.second) }
}

val basinPairs : List<Pair<Point, Int>> = input.getEntries()
    .filter { (point, item) ->
        adjPointsMapper.invoke(point).all { adjPoint ->
            input.getVal(adjPoint)?.let { adjV -> adjV > item } ?: true
        }
    }

// Part 1
println(basinPairs.sumOf { it.second + 1 })

// Part 2
println(basinPairs.asSequence()
    .map { it.first }
    .map { getValley(it).size }
    .sortedByDescending { it }
    .take(3)
    .reduce(Int::times)
)

// technically the Grid should be parameterized, but it saves my poor stack frame to ref the "global" variable
// memo to help cache things we have already seen
// returns the set of points in the valley (meanwhile wrecking my precious stack frame anyway =( )
fun getValley(point: Point, memo: MutableSet<Point> = mutableSetOf()) : MutableSet<Point> {
    val response : MutableSet<Point> = mutableSetOf()
    if (!memo.contains(point)) {
        memo.add(point)
        input.getVal(point)?.let {
            if (it != 9) {
                response.add(point)
                adjPointsMapper.invoke(point).forEach { adj -> response.addAll(getValley(adj, memo)) }
            }
        }
    }
    return response
}