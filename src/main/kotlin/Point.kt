data class Point(val x: Int, val y: Int) : Comparable<Point> {
    fun plus(point: Point) : Point {
        return Point(x + point.x, y + point.y)
    }

    fun minus(point: Point) : Point {
        return Point(x - point.x, y - point.y)
    }

    override fun compareTo(other: Point): Int {
        val yCompare = this.y.compareTo(other.y)
        return if (yCompare != 0) yCompare else this.x.compareTo(other.x)
    }

    override fun toString(): String {
        return "($x, $y)"
    }
}