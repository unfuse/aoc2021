class DumboOctopus(private var id: Int, private val counter: FlashCounter, private var state: Int = 0) {
    private val neighbors : MutableSet<DumboOctopus> = mutableSetOf()

    fun visit() {
        if (++state == 10) {
            flash()
            neighbors.forEach{ it.visit() }
        }
    }

    private fun flash() {
        counter.count++
    }

    fun reset() {
        if (state > 9) {
            state = 0
        }
    }

    fun setNeighbors(ns: Collection<DumboOctopus>) {
        neighbors.addAll(ns)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as DumboOctopus

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id
    }

    override fun toString(): String {
        return "${if (state <= 9) state else '*' }"
    }
}

data class FlashCounter(var count: Int = 0) {}

val adjacents: List<Point> = listOf(
    Point(-1, -1), Point(-1, 0), Point(-1, 1),
    Point(0, -1), /* 0, 0  omitted */ Point(0, 1),
    Point(1, -1), Point(1, 0),  Point(1, 1)
)

fun getNeighbors(point: Point) : Collection<Point> {
    return adjacents.map{ point.plus(it) }
}

// test answer is 1656 for 100 steps, 204 for 10 steps
var i = 0
val flashCounter = FlashCounter()
val input: Grid<DumboOctopus> = Utils.readFileAsGrid("day11", { line -> line.chunked(1) }) { cell ->
    DumboOctopus(i++, flashCounter, cell.toInt())
}

input.getEntries().forEach { entry ->
    entry.second.setNeighbors(getNeighbors(entry.first).mapNotNull(input::getVal))
}

var lastCounter = flashCounter.count
val size = input.getPoints().size
var step = 0
while (true) {
    input.getValues().forEach(DumboOctopus::visit)
    input.getValues().forEach(DumboOctopus::reset)
    step++
    if (step == 100) {
        println("part1 counter = ${flashCounter.count}")
    }

    if (flashCounter.count - lastCounter == size) {
        println("part2 step = $step")
        break
    }
    lastCounter = flashCounter.count
}