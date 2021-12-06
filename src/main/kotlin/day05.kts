import kotlin.math.abs
import Utils.Companion.toward

data class Line(val start: Point, val end: Point) {

    fun overlapsAt(that: Line) : Set<Point> {
        return this.getPoints().intersect(that.getPoints())
    }

    private fun getPoints() : List<Point> {
        if (isHorizontal()) {
            return start.x.toward(end.x).toList().map { Point(it, start.y) }
        }

        if (isVertical()) {
            return start.y.toward(end.y).toList().map { Point(start.x, it) }
        }

        if (isOneSlope()) {
            val xs : List<Int> = start.x.toward(end.x).toList()
            val ys : List<Int> = start.y.toward(end.y).toList()

            return xs.zip(ys).map { (a: Int, b: Int) -> Point(a, b) }
        }

        throw IllegalStateException("idk what to do")
    }

    private fun isHorizontal() : Boolean {
        return start.y == end.y
    }

    private fun isVertical() : Boolean {
        return start.x == end.x
    }

    private fun isOneSlope() : Boolean {
        return abs(this.start.x - this.end.x) == abs(this.start.y - this.end.y)
    }

    fun partOneFilter() : Boolean {
        return isHorizontal() || isVertical()
    }
}

val input: List<Line> = Utils.readFileAsList("day05")
{ line: String ->
    val points = line.split("->").map { part: String ->
        val stuff = part.trim().split(",");
        Point(stuff[0].toInt(), stuff[1].toInt())
    }
    Line(points[0], points[1])
}

fun getOverlaps(lines: List<Line>) : Set<Point> {
    val overlaps: MutableSet<Point> = mutableSetOf()
    for (i in 0 until lines.size - 1) {
        val a = lines[i]
        for (j in i + 1 until lines.size) {
            val b = lines[j]
            a.overlapsAt(b).forEach(overlaps::add)
        }
    }

    return overlaps
}

println(getOverlaps(input.filter(Line::partOneFilter)).size)
println(getOverlaps(input).size)