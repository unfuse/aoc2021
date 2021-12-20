import Utils.Companion.toward
import kotlin.math.max
import kotlin.math.min

class Grid<T>(grid: Map<Point, T> = emptyMap()) {
    private val grid: MutableMap<Point, T> = grid.toMutableMap()
    var lowX: Int
    var lowY: Int
    var upperX: Int
    var upperY: Int

    init {
        lowX = 0
        lowY = 0
        upperX = 0
        upperY = 0

        grid.keys.forEach {
            lowX = min(lowX, it.x)
            upperX = max(upperX, it.x)
            lowY = min(lowY, it.y)
            upperY = max(upperY, it.y)
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
        return upperX - lowX + 1
    }

    fun getHeight() : Int {
        return upperY - lowY + 1
    }

    fun visit(x: Int, y: Int, visitor: (T) -> Unit) {
        visit(Point(x, y), visitor)
    }

    fun visit(point: Point, visitor: (T) -> Unit) {
        grid[point]?.let(visitor)
    }

    fun update(x: Int, y: Int, item: T) : T? {
        return update(Point(x, y), item)
    }

    fun update(point: Point, item: T) : T? {
        lowX = min(lowX, point.x)
        upperX = max(upperX, point.x)
        lowY = min(lowY, point.y)
        upperY = max(upperY, point.y)

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