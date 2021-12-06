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

data class Position(var x: Int = 0, var y: Int = 0, var aim: Int = 0);

val input: List<Pair<Direction, Int>> = Utils.readFileAsList("day02")
{ line ->
    val stuff = line.split(" ")
    Pair(Direction.valueOf(stuff[0].uppercase()), stuff[1].toInt())
}

var posa = Position()
var posb = Position()

input.forEach {
    posa = it.first.move_simple(posa, it.second)
    posb = it.first.move_complex(posb, it.second)
}

println(posa.x * posa.y)
println(posb.x * posb.y)