import kotlin.math.abs

data class Instruction(val state: Int, val xRange: IntRange, val yRange: IntRange, val zRange: IntRange)

val input: List<Instruction> = Utils.readFileAsList("test") { line ->
    val first = line.trim().split(" ")
    val state = if(first[0] == "on") 1 else 0
    // x, y, z
    val ranges = first[1].split(",")
        .map { it.trim().split("=").get(1).trim() }
        .map { it.split("..") }
        .flatMap { it.map { it.toInt() } }
        .windowed(2, 2)
        .map { IntRange(it[0], it[1]) }

    Instruction(state, ranges[0], ranges[1], ranges[2])
}

data class PointRange(val low: Point, val high: Point) {
    fun intersect(pointRange: PointRange) : PointRange? {
        val nextLow: Point = low.maxPieces(pointRange.low)
        val nextHigh: Point = high.minPieces(pointRange.high)

        if (nextLow.lesserPieces(nextHigh)) {
            return PointRange(nextLow, nextHigh)
        }
        return null
    }

    fun bisect(pointRange: PointRange) : List<PointRange>? {
        return intersect(pointRange)?.let {
            val result = mutableListOf<PointRange>()
            if (low.lesserPieces(it.low)) result.add(PointRange(low, it.low))
            if (high.greaterPieces(it.high)) result.add(PointRange(it.high, high))
            result
        }
    }

    fun size() : Long {
        return high.minus(low).reduce( { item -> if(item != 0) abs(item.toLong()) else 1L }, Long::times)
    }
}

val grid = Grid<Int>()

for (inst in input) {
    for (x in inst.xRange) {
        if (abs(x) > 50) continue
        for (y in inst.yRange) {
            if (abs(y) > 50) continue
            for (z in inst.zRange) {
                if (abs(z) > 50) continue
                grid.update(Point(x, y, z), inst.state)
            }
        }
    }
}

println(grid.getEntries().sumOf { it.second })

val map = mutableMapOf<PointRange, Int>()

fun addAndBreak(pointRange: PointRange, state: Int) {
    val impactedKeys = map.keys.mapNotNull { key ->
        pointRange.intersect(key)?.let {  Pair(key, pointRange.intersect(key)!!) }
    }

    // Something to do!
    if (impactedKeys.isNotEmpty()) {
        // if the impacted key has different state than target
        impactedKeys.forEach{ (key, intersection) ->
            val oldState = map[key]!!
            if (oldState != state) {
                // change the overlap area to new state
                map[intersection] = state

                // create new keys for any underlap matching old state
                key.bisect(intersection)?.forEach { map[it] = oldState }

                // remove old key
                map.remove(key)
            }
        }
    }
    // Otherwise, if there is no action and state is relevant, add new a key
    else if (state == 1) {
        map[pointRange] = state
    }
}

for(inst in input) {
    val pointRange = PointRange(
        Point(inst.xRange.first, inst.yRange.first, inst.zRange.first),
        Point(inst.xRange.last, inst.yRange.last, inst.zRange.last)
    )
    addAndBreak(pointRange, inst.state)
    println(map)
}

println(map.map { (key, value) -> if (value == 1) key.size() else 1L }.reduce(Long::plus))