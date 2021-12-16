val input = Utils.readFileSplitByNewlines("day14")

val reactors : Map<String, String> = input[1].lines()
    .flatMap { it.split("->") }
    .map(String::trim)
    .windowed(2, 2)
    .associate { Pair(it[0], it[1]) }

var sequence : String = input[0]

fun String.slickZip(other: String) : String {
    var result = ""
    val thisIt = this.iterator()
    val thatIt = other.iterator()
    while (thisIt.hasNext() || thatIt.hasNext()) {
        if (thisIt.hasNext()) {
            result += thisIt.next()
        }
        if (thatIt.hasNext()) {
            result += thatIt.next()
        }
    }
    return result
}

val upTo = 40

for (i in 1..minOf(upTo, 10)) {
    val addins : String = sequence
        .windowed(2, 1)
        .joinToString("") { reactors[it] ?: throw IllegalStateException("no reactor for $it") }
    sequence = sequence.slickZip(addins)
}

var countMap: MutableMap<String, Long> = sequence.groupBy{ it }.map { Pair(it.key.toString(), it.value.size.toLong()) }.toMap().toMutableMap()

println(countMap.values.maxOrNull()!! - countMap.values.minOrNull()!!)

// part 2
var countCache : MutableMap<String, Long> = reactors.keys.associateWith { 0L }.toMutableMap()
input[0].windowed(2, 1).forEach{ countCache.computeIfPresent(it) { _, value -> value + 1 } }
countMap = mutableMapOf()
input[0].chunked(1).forEach { countMap.compute(it){ _, old -> old?.plus(1) ?: 1L } }

for (i in 1..upTo) {
    val next : MutableMap<String, Long> = mutableMapOf()
    println("iteration $i")
    countCache.filter { it.value > 0L }.forEach{ (str, mult) ->
        val reactor = reactors[str]!!
        countMap.compute(reactor){ _, old -> old?.plus(mult) ?: mult }
        println("process ($str) -> $reactor with mult $mult")
        val left = str[0] + reactor
        val right = reactor + str[1]
        println("children are $left and $right")
        next.compute(left) { _, old -> old?.plus(mult) ?: mult}
        next.compute(right) { _, old -> old?.plus(mult) ?: mult}
    }

    countCache = next
}

println(countMap.values.maxOrNull()!! - countMap.values.minOrNull()!!)