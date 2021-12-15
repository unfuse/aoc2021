val input = Utils.readFileSplitByNewlines("test")

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

val upTo = 2

for (i in 1..minOf(upTo, 10)) {
    val addins : String = sequence
        .windowed(2, 1)
        .joinToString("") { reactors[it] ?: throw IllegalStateException("no reactor for $it") }
    sequence = sequence.slickZip(addins)
}

println(sequence)

var countMap: Map<String, Long> = sequence.groupBy{ it }.map { Pair(it.key.toString(), it.value.size.toLong()) }.toMap()

println(countMap)
println(countMap.values.maxOrNull()!! - countMap.values.minOrNull()!!)

// part 2
var countCache : MutableMap<String, Long> = reactors.keys.associateWith { 0L }.toMutableMap()
input[0].windowed(2, 1).forEach{ countCache.computeIfPresent(it) { _, value -> value + 1 } }

/**
 * TRACK COUNTS OF LETTERS AS THEY ARE ADDED
 * EXCEPT ACCOUNT FOR THE MULTIPLICITY CORRECTLY
 */
for (i in 1..upTo) {
    val next : MutableMap<String, Long> = mutableMapOf()
    println("iteration $i")
    countCache.filter { it.value > 0L }.forEach{ (str, mult) ->
        val reactor = reactors[str]!!
        println("process ($str) -> $reactor with mult $mult")
        val left = str[0] + reactor
        val right = reactor + str[1]
        println("children are $left and $right")
        next.compute(left) { _, old -> old?.plus(mult) ?: mult}
        next.compute(right) { _, old -> old?.plus(mult) ?: mult}
    }

    countCache = next
    println(countCache)
    println()
}

countCache.map {  }

countMap = countCache
    .flatMap { (key, value) -> key.chunked(1).map { Pair(it, value) } }
    .groupBy { it.first }
    .map { Pair(it.key, it.value.sumOf { it.second }) }
    .toMap()

println(countCache)
println(countMap)
println(countMap.values.maxOrNull()!! - countMap.values.minOrNull()!!)