data class Point(val x: Int, val y: Int) : Comparable<Point> {
    override fun compareTo(other: Point): Int {
        val xCompare = this.x.compareTo(other.x)
        return if (xCompare != 0) xCompare else this.y.compareTo(other.y)
    }

    override fun toString(): String {
        return "($x, $y)"
    }
}