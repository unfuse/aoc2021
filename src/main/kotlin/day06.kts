data class Lanternfish(var cycle: Int, val multiplier: Long) {
    fun process() : Long {
        if (cycle == 0) {
            cycle = 6
            return multiplier
        }
        cycle--
        return 0
    }

    override fun toString(): String {
        return "{ $cycle, $multiplier }"
    }
}

val input: List<Lanternfish> = Utils.readLineAsIntList("day06")
    .groupBy { it }
    .map { Lanternfish(it.key, it.value.size.toLong()) }

var allFish: MutableList<Lanternfish> = input.toMutableList()

// part 1 is to 80
for (i in 0 until 256) {
    val rollingMult = allFish.sumOf(Lanternfish::process)
    val nextFish : MutableList<Lanternfish> = mutableListOf()
    // Make new fish group if needed
    if (rollingMult > 0) { nextFish.add(Lanternfish(8, rollingMult)) }
    // Group existing fish if the cycles line up
    nextFish.addAll(allFish.groupBy { it.cycle }.map { Lanternfish(it.key, it.value.sumOf(Lanternfish::multiplier)) } )
    allFish = nextFish.toMutableList()
}

println(allFish.sumOf{ it.multiplier })