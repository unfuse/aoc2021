import com.sun.org.apache.xpath.internal.operations.Bool
import kotlin.math.abs

data class Point(val x: Int = 0, val y: Int = 0, val z: Int = 0, val t: Int = 0) : Comparable<Point> {
    fun plus(point: Point) : Point {
        return pointMapper(point, Int::plus)
    }

    fun minus(point: Point) : Point {
        return pointMapper(point, Int::minus)
    }

    fun minPieces(point: Point) : Point {
        return pointMapper(point, Math::min)
    }

    fun maxPieces(point: Point) : Point {
        return pointMapper(point, Math::max)
    }

    fun manhattan(point: Point) : Int {
        return minus(point).reduceInt( { item -> abs(item) }, Int::plus)
    }

    fun lesserPieces(point: Point) : Boolean {
        return pointMapper(point, Int::compareTo).reduce( { item -> item < 0}, Boolean::and)
    }

    fun greaterPieces(point: Point) : Boolean {
        return pointMapper(point, Int::compareTo).reduce( { item -> item > 0}, Boolean::and)
    }

    fun pointMapper(point: Point, mapper: (Int, Int) -> Int) : Point {
        return Point(mapper.invoke(x, point.x), mapper.invoke(y, point.y), mapper.invoke(z, point.z), mapper.invoke(t, point.t))
    }

    fun reduceInt(mapper: (item: Int) -> Int = { a -> a }, reducer: (acc: Int, next: Int) -> Int) : Int {
        return listOf(y, z, t).map(mapper).fold(mapper.invoke(x), reducer)
    }

    fun <T> reduce(mapper: (item: Int) -> T, reducer: (acc: T, next: T) -> T) : T {
        return listOf(y, z, t).map(mapper).fold(mapper.invoke(x), reducer)
    }

    override fun compareTo(other: Point): Int {
        val comparator = compareBy<Point>({ point -> point.t }, { point -> point.z }, { point -> point.y }, { point -> point.x })
        return comparator.compare(this, other)
    }

    override fun toString(): String {
        return "($x, $y, $z, $t)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Point

        if (x != other.x) return false
        if (y != other.y) return false
        if (z != other.z) return false
        if (t != other.t) return false

        return true
    }

    override fun hashCode(): Int {
        var result = x
        result = 31 * result + y
        result = 31 * result + z
        result = 31 * result + t
        return result
    }
}