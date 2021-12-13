val input = Utils.readFileSplitByNewlines("day13")

val points : Set<Point> = input[0].lines()
    .flatMap{ it.trim().split(",") }
    .map{ it.toInt() }
    .windowed(2, 2)
    .map { Point(it[0], it[1]) }
    .toSet()

val folds = input[1].lines()
    .map { it.substringAfter("""fold along """).trim() }
    .flatMap { it.split("=") }
    .windowed(2, 2)
    .map { if(it[0] == "x") { Point(it[1].toInt(), 0) } else { Point(0, it[1].toInt()) } }

fun Point.fold(point: Point) : Point {
    return if (x == 0) {
        if (point.y <= y) point else Point(point.x, y + (y - point.y))
    }
    else if (y == 0) {
        if (point.x <= x) point else Point(x + (x - point.x), point.y)
    }
    else {
        throw IllegalStateException("cannot fold a line with slope")
    }
}

// Part 1 - first fold only
val firstFold = points.map{ folds[0].fold(it) }.toSet()
println(firstFold.size)

// Part 2 - all folds and print and read
var curPoints = points
folds.forEach{ fold -> curPoints = curPoints.map { fold.fold(it) }.toSet() }

val grid = Grid(curPoints.associateWith { "#" })
println(grid)