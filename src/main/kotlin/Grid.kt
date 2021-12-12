class Grid<T>(grid: Map<Point, T>) {
    private val grid: MutableMap<Point, T> = grid.toMutableMap()
    private val width: Int
    private val height: Int

    init {
        var _width = 0
        var _height = 0

        grid.keys.forEach {
            _width = maxOf(_width, it.x)
            _height = maxOf(_height, it.y)
        }

        width = _width + 1
        height = _height + 1
    }

    fun getPoints() : List<Point> {
        return grid.keys.toList()
    }

    fun getValues() : Collection<T> {
        return grid.values.toSet()
    }

    fun getEntries() : List<Pair<Point, T>> {
        return grid.entries.map { Pair(it.key, it.value) }.toList()
    }

    fun getVal(point: Point) : T? {
        return grid[point]
    }

    fun getRow(y: Int) : List<Pair<Int, T>>? {
        if (y < 0 || y >= height) {
            return null
        }

        return grid.filter { it.key.y == y }.map { Pair(it.key.x, it.value) }
    }

    fun getCol(x: Int) : List<Pair<Int, T>>? {
        if (x < 0 || x >= width) {
            return null
        }

        return grid.filter { it.key.x == x }.map { Pair(it.key.y, it.value) }
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
        return getPoints().sorted()
            .map { grid[it]?.toString() }
            .windowed(height, height)
            .joinToString("\n") { it.joinToString("") }
    }
}