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

val input: List<Lanternfish> = Utils.readFile("day06",
    { line -> line.split(",").map { it.toInt() }.groupBy { it }.map { Lanternfish(it.key, it.value.size.toLong()) }},
    ::ArrayList,
    { item, collector -> collector.addAll(item) }
)

var allFish: MutableList<Lanternfish> = input.toMutableList()
for (i in 0 until 256) {
    var rollingMult = 0L
    for (fish in allFish) {
        rollingMult += fish.process()
    }
    val nextFish : MutableList<Lanternfish> = mutableListOf()
    // Make new fish group if needed
    if (rollingMult > 0) { nextFish.add(Lanternfish(8, rollingMult)) }
    // Group existing fish if the cycles line up
    nextFish.addAll(allFish.groupBy { it.cycle }.map { Lanternfish(it.key, it.value.sumOf(Lanternfish::multiplier)) } )
    allFish = nextFish.toMutableList()
}

println(allFish.sumOf{ it.multiplier })