import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

data class Line(val start: Point, val end: Point) {

    fun overlapsAt(that: Line) : Set<Point> {
        return this.getPoints().intersect(that.getPoints())
    }

    fun getPoints() : List<Point> {
        if (isHorizonal()) {
            return IntRange(min(this.start.x, this.end.x), max(this.start.x, this.end.x)).map { Point(it, this.start.y) }
        }

        if (isVertical()) {
            return IntRange(min(this.start.y, this.end.y), max(this.start.y, this.end.y)).map { Point(this.start.x, it) }
        }

        if (isOneSlope()) {
            val xs : List<Int> = start.x.toward(end.x).toList()
            val ys : List<Int> = start.y.toward(end.y).toList()

            return xs.zip(ys).map { (a: Int, b: Int) -> Point(a, b) }
        }

        throw IllegalStateException("idk what to do")
    }

    fun isHorizonal() : Boolean {
        return start.y == end.y
    }

    fun isVertical() : Boolean {
        return start.x == end.x
    }

    fun isOneSlope() : Boolean {
        return abs(this.start.x - this.end.x) == abs(this.start.y - this.end.y)
    }

    fun partOneFilter() : Boolean {
        return isHorizonal() || isVertical()
    }

    // ridiculous that something like this isn't built in to "range" in the base package.
    // a..b and b..a should work regardless of which one is smaller
    // stackoverflow eventually educated me for why it wasn't working and i stole this
    private infix fun Int.toward(to: Int): IntProgression {
        val step = if (this > to) -1 else 1
        return IntProgression.fromClosedRange(this, to, step)
    }
}

val input: List<Line> = Utils.readFile("day05",
    {line: String -> val points = line.split("->").map {
            part: String -> val stuff = part.trim().split(",");
            Point(stuff[0].toInt(), stuff[1].toInt()) };
        Line(points[0], points[1]) },
    ::ArrayList,
    { item, collector -> collector += item }
)

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