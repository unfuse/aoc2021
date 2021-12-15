import kotlin.math.abs

data class Point(val x: Int, val y: Int) : Comparable<Point> {
    fun plus(point: Point) : Point {
        return Point(x + point.x, y + point.y)
    }

    fun minus(point: Point) : Point {
        return Point(x - point.x, y - point.y)
    }

    fun manhattan(point: Point) : Int {
        return abs(point.x - x) + abs(point.y - y)
    }

    override fun compareTo(other: Point): Int {
        val yCompare = this.y.compareTo(other.y)
        return if (yCompare != 0) yCompare else this.x.compareTo(other.x)
    }

    override fun toString(): String {
        return "($x, $y)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Point

        if (x != other.x) return false
        if (y != other.y) return false

        return true
    }

    override fun hashCode(): Int {
        var result = x
        result = 31 * result + y
        return result
    }
}