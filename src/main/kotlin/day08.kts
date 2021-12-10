data class Readout(val display: List<String>, val result: List<String>) {
    override fun toString(): String {
        return "$display | $result"
    }

    /*
        Consider the segment display as the following characteristics:
        [UP, UL, UR, MID, LOW, LL, LR]
        Mapping numbers to characteristics is an exercise left to the reader

        known: 8, 7, 4, 1

        4-1 = [UL, MID]

        5 segments = 2, 3, 5
        * only 5 has UL, so can filter to find which one intersects [UL, MID]
        * only 3 has LR, so can filter to find which one intersects 1
        * 2 is remaining

        2-3 = LL
        6 segments = 6, 9, 0
        * 6 does not have UR, so can filter to the one that does not intersect 1
        * 9 = 8 - LL
        * 0 is remaining

        From there we have all numbers and merely need to map the sequences present to a number string to an integer and sum
    */
    fun solve() : Int {
        val allThings : Set<Set<Char>> = display.plus(result).map { it.toSet() }.toSet()

        val eight : Set<Char> = allThings.first { it.size == 7 }
        val seven : Set<Char> = allThings.first { it.size == 3 }
        val four  : Set<Char> = allThings.first { it.size == 4 }
        val one   : Set<Char> = allThings.first { it.size == 2 }

        val ulmid : Set<Char> = four.minus(one)

        val len5  : Set<Set<Char>> = allThings.filter { it.size == 5 }.toSet()
        val five  : Set<Char> = len5.first { it.containsAll(ulmid) }
        val three : Set<Char> = len5.filter { it != five }.first { it.containsAll(one) }
        val two   : Set<Char> = len5.filter { it != five }.first { it != three }

        val ll    : Set<Char> = two.minus(three)
        val len6  : Set<Set<Char>> = allThings.filter { it.size == 6 }.toSet()

        val nine  : Set<Char> = eight.minus(ll)
        val six   : Set<Char> = len6.filter { it != nine }.first { !it.containsAll(one) }
        val zero  : Set<Char> = len6.filter { it != nine }.first { it != six }

        val lookup : Map<Set<Char>, String> = mapOf(
            Pair(zero,   "0"),
            Pair(one,    "1"),
            Pair(two,    "2"),
            Pair(three,  "3"),
            Pair(four,   "4"),
            Pair(five,   "5"),
            Pair(six,    "6"),
            Pair(seven,  "7"),
            Pair(eight,  "8"),
            Pair(nine,   "9"))

        return result.mapNotNull { lookup[it.toSet()] }.reduce(String::plus).toInt()
    }
}

val input : List<Readout> = Utils.readFileAsList("day08") { line : String ->
    val spl = line.split("|")
    val display = spl[0].trim().split(" ").map(String::trim)
    val result = spl[1].trim().split(" ").map(String::trim)
    Readout(display, result)
}

println(input.flatMap { it.result }.map { it.length }.count { it in listOf(2, 3, 4, 7) })
println(input.sumOf(Readout::solve))