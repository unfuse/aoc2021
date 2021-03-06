val input = Utils.readFileSplitByNewlines("day20")

val lookup = input[0].chunked(1)
var grid = Utils.readStringAsGrid(input[1], { line -> line.chunked(1) }) { it }

val serializer = mapOf(Pair(".", "0"), Pair("#", "1"))
var default: String = "."
val pointOne = Point(1, 1, 1, 1)

fun iteratePicture(grid: Grid<String>) : Grid<String> {
    val nextGrid = mutableMapOf<Point, String>()
    for(nextPoint in Utils.squareOfExcept(
        grid.lowPoint.minus(pointOne),
        grid.upperPoint.plus(pointOne),
        emptySet())
    ) {
        val (code, nextVal) = calcPoint(nextPoint)
        nextGrid[nextPoint] = nextVal
    }

    // Reset the default of the universe since we cannot store it
    default = lookup[default.repeat(9).map{ serializer[it.toString()] }.joinToString("").toInt(2)]

    return Grid(nextGrid)
}

fun calcPoint(point: Point) : Pair<Int, String> {
    val code : Int = Utils.squareOfExcept(
        point.minus(pointOne),
        point.plus(pointOne),
        emptySet()
    )
        .sorted()
        .map { grid.getVal(it) ?: default }
        .joinToString("") { serializer[it]!! }
        .toInt(2)
    return Pair(code, lookup[code])
}

// part 1 is two iter
// part 2 is 50 iter
for (i in 0..49) {
    grid = iteratePicture(grid)
}

println(grid.getEntries().count{ (_, item) -> item == "#" })