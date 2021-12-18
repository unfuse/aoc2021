import Utils.Companion.squareOfExcept
import java.util.PriorityQueue

val input = Utils.readFileAsGrid("day15", { line -> line.chunked(1) }) { cell -> cell.toInt() }

val gridSectionMapper = squareOfExcept(Point(0, 0), Point(4, 4), emptySet())
val startPoint = Point(0, 0)

val grid : Grid<Int> = Grid()

for (section in gridSectionMapper) {
    input.getEntries().forEach { (key, value) ->
        val nextPoint = key.plus(Point(section.x * input.width, section.y * input.height))
        var nextVal = value + startPoint.manhattan(section)
        if (nextVal > 9) {
            nextVal %= 9
        }
        grid.update(nextPoint, nextVal)
    }
}

val endPoint = Point(grid.width - 1, grid.height - 1)

var i = 0
data class Path(var curPoint : Point,
                val id : Int,
                val distance : Int,
                val cost : Int = 0,
                val expectedCost : Int = distance + cost) : Comparable<Path> {

    override fun compareTo(other: Path): Int {
        return this.expectedCost.compareTo(other.expectedCost)
    }

    override fun toString(): String {
        return "($id) $curPoint @ $cost [$expectedCost]"
    }
}

val adjPoints: List<Pair<Int, Int>> = listOf(-1, 1, 0, 0).zip(listOf(0, 0, 1, -1))
val adjPointsMapper: (point: Point) -> List<Point> = {
        point -> adjPoints.map { Point(point.x + it.first, point.y + it.second) }
}

val considerations : PriorityQueue<Path> = PriorityQueue()
considerations.add(Path(startPoint, i++, startPoint.manhattan(endPoint)))
var cheapestPath : Path? = null

val bestCostPerPoint : MutableMap<Point, Int> = mutableMapOf()
bestCostPerPoint[startPoint] = 0
while (considerations.isNotEmpty()) {
    val curPath = considerations.poll()
    val nextPoints : Set<Point> = adjPointsMapper.invoke(curPath.curPoint)
        .filter { grid.hasPoint(it) }
        .toSet()

    nextPoints.forEach { nextPoint ->
        val nextPath = Path(nextPoint, i++, nextPoint.manhattan(endPoint),
            curPath.cost + grid.getVal(nextPoint)!!)

        bestCostPerPoint[nextPoint]
            ?.let { if (it <= nextPath.cost) { return@forEach } }
            ?:run { bestCostPerPoint[nextPoint] = nextPath.cost }

        if (nextPoint == endPoint) {
            cheapestPath?.let {
                if (nextPath.cost < it.cost) {
                    cheapestPath = nextPath
                }
            } ?: run {
                cheapestPath = nextPath
            }

            considerations.removeIf{ it.cost >= nextPath.cost  }
        }
        else {
            cheapestPath?.let {
                if (nextPath.cost < it.cost) considerations.add(nextPath)
            } ?: considerations.add(nextPath)
        }

        considerations.removeIf{ it.curPoint == nextPath.curPoint && it.cost > nextPath.cost  }
    }
}

println(cheapestPath!!.cost)