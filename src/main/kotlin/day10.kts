val input = Utils.readFileAsStringList("day10")

val mapping: Map<Char, Char> = mapOf(Pair('(', ')'), Pair('{', '}'), Pair('[', ']'), Pair('<', '>'))

val pointsp1: Map<Char, Long> = mapOf(Pair(')', 3), Pair(']', 57), Pair('}', 1197), Pair('>', 25137))
val filteredp1 = input.mapNotNull { line ->
    var stack : MutableList<Char> = mutableListOf()
    line.forEach { c ->
        if (c in mapping.keys) {
            stack.add(c)
        }
        else {
            if (c != mapping[stack.last()]) {
                return@mapNotNull Pair(line, c)
            } else {
                stack = stack.dropLast(1).toMutableList()
            }
        }
    }
    return@mapNotNull null
}

println(filteredp1.map { it.second }.mapNotNull { pointsp1[it] }.sum())

// pt2
val pointsp2: Map<Char, Long> = mapOf(Pair(')', 1), Pair(']', 2), Pair('}', 3), Pair('>', 4))
val filteredp1OnlyString = filteredp1.map { it.first }
val filteredp2 = input.filterNot { it in filteredp1OnlyString }

val scores = filteredp2.map { line ->
        var stack : MutableList<Char> = mutableListOf()
        line.forEach { c ->
            if (c in mapping.keys) {
                stack.add(c)
            }
            else {
                stack = stack.dropLast(1).toMutableList()
            }
        }
        return@map stack.joinToString("")
    }
    .map { line -> line.reversed().map { mapping[it] }.mapNotNull { pointsp2[it] }.reduce { acc, l -> (acc * 5L) + l } }
    .sorted()

println(scores[scores.size / 2])