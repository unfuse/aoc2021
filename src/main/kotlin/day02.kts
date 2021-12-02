enum class Direction {
    FORWARD {
        override fun move_simple(pos: Position, mag: Int): Position {
            return Position(pos.x + mag, pos.y, pos.aim)
        }

        override fun move_complex(pos: Position, mag: Int): Position {
            return Position(pos.x + mag, pos.y + (pos.aim * mag), pos.aim)
        }
    },
    DOWN {
        override fun move_simple(pos: Position, mag: Int): Position {
            return Position(pos.x, pos.y + mag, pos.aim)
        }

        override fun move_complex(pos: Position, mag: Int): Position {
            return Position(pos.x, pos.y, pos.aim + mag)
        }
    },
    UP {
        override fun move_simple(pos: Position, mag: Int): Position {
            return Position(pos.x, pos.y - mag, pos.aim)
        }

        override fun move_complex(pos: Position, mag: Int): Position {
            return Position(pos.x, pos.y, pos.aim - mag)
        }
    },
    ;

    abstract fun move_simple(pos: Position, mag: Int) : Position

    abstract fun move_complex(pos: Position, mag: Int) : Position
}

data class Position(var x: Int, var y: Int, var aim: Int);

val input: List<Pair<Direction, Int>> = Utils.readFile("day02",
    { line ->   val stuff = line.split(" "); Pair(Direction.valueOf(stuff[0].uppercase()), stuff[1].toInt())},
    ::ArrayList,
    { item, collector -> collector.add(item) }
)

var posa = Position(0, 0, 0)
var posb = Position(0, 0, 0)

input.forEach {
    posa = it.first.move_simple(posa, it.second)
    posb = it.first.move_complex(posb, it.second)
}

println(posa.x * posa.y)
println(posb.x * posb.y)