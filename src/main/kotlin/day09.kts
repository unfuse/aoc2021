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
val basinPoints : List<Point> = basinPairs.map { it.first }
val basins : MutableMap<Point, MutableSet<Point>> = mutableMapOf()
for (basin in basinPoints) {
    val valley: MutableSet<Point> = mutableSetOf()
    getValley(input, basin, mutableSetOf(), valley)
    basins[basin] = valley
}

println(basins.map { Pair(it.key, it.value.size) }.sortedByDescending { it.second }.take(3).map { it.second }.reduce(Int::times))

// seenPoints to help cache things we have already seen
// valley is the incrementally built useful return value
fun getValley(grid: Grid<Int>, point: Point, seenPoints: MutableSet<Point>, valley: MutableSet<Point>) {
    if (seenPoints.contains(point)) {
        return
    }
    seenPoints.add(point)
    grid.getVal(point)?.let {
        if (it != 9) {
            valley.add(point)
            adjPointsMapper.invoke(point).forEach { adj -> getValley(grid, adj, seenPoints, valley) }
        }
    }
}