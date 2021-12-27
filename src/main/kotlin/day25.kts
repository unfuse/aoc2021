var floor = Utils.readFileAsGrid("day25", { line -> line.chunked(1)}) { cell -> cell }

var easts  = floor.getEntries().filter { it.second == ">" }.map { it.first }
var souths = floor.getEntries().filter { it.second == "v" }.map { it.first }

val moveEastFn  = { point: Point -> if (point.x + 1 > floor.upperPoint.x) Point(floor.lowPoint.x, point.y) else Point(point.x + 1, point.y)}
val moveSouthFn = { point: Point -> if (point.y + 1 > floor.upperPoint.y) Point(point.x, floor.lowPoint.x) else Point(point.x, point.y + 1)}

var done = false
var counter = 0
while (!done) {
    // reset break condition
    done = true

    // process easts
    val nextEasts = mutableListOf<Point>()

    for (east in easts) {
        val nextEast = moveEastFn.invoke(east)

        if (nextEast !in easts && nextEast !in souths) {
            nextEasts.add(nextEast)
            done = false
        }
        else {
            nextEasts.add(east)
        }
    }

    // process souths
    val nextSouths = mutableListOf<Point>()
    for (south in souths) {
        val nextSouth = moveSouthFn(south)

        if (nextSouth !in nextEasts && nextSouth !in souths) {
            nextSouths.add(nextSouth)
            done = false
        }
        else {
            nextSouths.add(south)
        }
    }

    // setup next grid
    val nextGrid = Grid<String>()
    nextGrid.lowPoint = floor.lowPoint
    nextGrid.upperPoint = floor.upperPoint
    nextEasts.forEach { nextGrid.update(it, ">") }
    nextSouths.forEach{ nextGrid.update(it, "v") }

    // carry vars over
    easts = nextEasts
    souths = nextSouths
    floor = nextGrid

    // step over
    counter++
}

println(counter)