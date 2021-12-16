import java.util.PriorityQueue

val input = Utils.readFileAsGrid("day15", { line -> line.chunked(1) }) { cell -> cell.toInt() }

val startPoint = Point(0, 0)
val endPoint = Point(input.width - 1, input.height - 1)

var i = 0
data class Path(var curPoint : Point,
                val id : Int,
                val distance : Int,
                val cost : Int = 0,
                val seen : MutableSet<Point> = mutableSetOf()) : Comparable<Path> {

    override fun compareTo(other: Path): Int {
        val dist = distance.compareTo(other.distance)
        return if (dist != 0) dist else cost.compareTo(other.cost)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Path

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id
    }

    override fun toString(): String {
        return "($id) $curPoint @ $cost"
    }
}

val adjPoints: List<Pair<Int, Int>> = listOf(-1, 1, 0, 0).zip(listOf(0, 0, 1, -1))
val adjPointsMapper: (point: Point) -> List<Point> = {
        point -> adjPoints.map { Point(point.x + it.first, point.y + it.second) }
}

val considerations : PriorityQueue<Path> = PriorityQueue()
considerations.add(Path(startPoint, i++, startPoint.manhattan(endPoint)))
var cheapestPath : Path? = null

while (considerations.isNotEmpty()) {
    val curPath = considerations.poll()
//    println("consider: $curPath")
    val nextPoints : Set<Point> = adjPointsMapper.invoke(curPath.curPoint)
        .filterNot { curPath.seen.contains(it) }
        .filter { input.hasPoint(it) }
        .toSet()

    nextPoints.forEach { nextPoint ->
        val nextPath = Path(nextPoint, i++, nextPoint.manhattan(endPoint),
            curPath.cost + input.getVal(nextPoint)!!,
            curPath.seen.toMutableSet().also { it += nextPoint })

        if (nextPoint == endPoint) {
            println("found a winner cost ${nextPath.cost}")
            // found ONE shortest path
            cheapestPath = cheapestPath?.let { if (cheapestPath!!.cost <= nextPath.cost) it else nextPath } ?: nextPath
            if (considerations.isNotEmpty()) considerations.removeIf{ it.cost > cheapestPath!!.cost }
        }
        else {
            cheapestPath?.let {
                if (nextPath.cost <= it.cost) considerations.add(nextPath)
            } ?: considerations.add(nextPath)
        }
    }
}

println(cheapestPath!!.cost)