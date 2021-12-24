import Utils.Companion.toward
import kotlin.math.max
import kotlin.math.min

class Grid<T>(grid: Map<Point, T> = emptyMap()) {
    private val grid: MutableMap<Point, T> = grid.toMutableMap()
    var lowPoint: Point
    var upperPoint: Point

    init {
        lowPoint = Point(0,0,0,0)
        upperPoint = Point(0,0,0,0)

        grid.keys.forEach {
            lowPoint = lowPoint.minPieces(it)
            upperPoint = upperPoint.maxPieces(it)
        }
    }

    fun hasPoint(point: Point) : Boolean {
        return grid.keys.contains(point)
    }

    fun getPoints() : List<Point> {
        return grid.keys.toList()
    }

    fun getValues() : Collection<T> {
        return grid.values.toSet()
    }

    // The points in the map may be incomplete (sparse matrix style) - allow getting all "virtual" points for processing
    fun getVirtualPoints() : List<Point> {
        return 0.toward(getWidth()-1).flatMap { x -> 0.toward(getHeight()-1).map{ Point(x, it) } }
    }

    fun getEntries() : List<Pair<Point, T>> {
        return grid.entries.map { Pair(it.key, it.value) }.toList()
    }

    fun getVal(point: Point) : T? {
        return grid[point]
    }

    fun getWidth() : Int {
        return upperPoint.x - lowPoint.x + 1
    }

    fun getHeight() : Int {
        return upperPoint.y - lowPoint.y + 1
    }

    fun getDepth() : Int {
        return upperPoint.z - lowPoint.z + 1
    }

    fun visit(point: Point, visitor: (T) -> Unit) {
        grid[point]?.let(visitor)
    }

    fun update(point: Point, item: T) : T? {
        lowPoint = lowPoint.minPieces(point)
        upperPoint = upperPoint.maxPieces(point)

        return if (grid.containsKey(point)) {
            val old = grid[point]
            grid[point] = item
            old
        } else {
            grid[point] = item
            null
        }
    }

    override fun toString(): String {
        return getVirtualPoints().sorted()
            .map { grid[it]?.toString() ?: "." }
            .windowed(getWidth(), getWidth())
            .joinToString("\n") { it.joinToString("") }
    }
}