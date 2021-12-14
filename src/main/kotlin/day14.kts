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

for (i in 1..10) {
    println(i)
    val addins : String = sequence
        .windowed(2, 1)
        .joinToString("") { reactors[it] ?: throw IllegalStateException("no reactor for $it") }
    sequence = sequence.slickZip(addins)
}

val countMap: Map<String, Long> = sequence.groupBy{ it }.map { Pair(it.key.toString(), it.value.size.toLong()) }.toMap()

println(countMap.values.maxOrNull()!! - countMap.values.minOrNull()!!)
