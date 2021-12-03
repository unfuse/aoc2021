val input: List<String> = Utils.readFileAsStringList("day03")

val length = input[0].length

// least common
var epsilon: String = ""
// most common
var gamma: String = ""

for (i in 0 until length) {
    var numzero = 0
    var numone = 0

    for (j in input) {
        if (j[i] == '0') {
            numzero++
        }
        else {
            numone++
        }
    }

    if (numzero > numone) {
        gamma += "0"
        epsilon += "1"
    }
    else {
        gamma += "1"
        epsilon += "0"
    }
}

val epsilonNum: Int = epsilon.toInt(2)
val gammaNum: Int = gamma.toInt(2)

println(epsilonNum * gammaNum)

fun filterByBitPositionCondition(list: List<String>, pos: Int, selector: (Int, Int) -> Boolean) : List<String> {
    val zeroList: MutableList<String> = ArrayList()
    val oneList: MutableList<String> = ArrayList()

    for (item in list) {
        if (item[pos] == '0') {
            zeroList.add(item)
        }
        else {
            oneList.add(item)
        }
    }

    if (zeroList.isEmpty()) {
        return oneList
    }

    if (oneList.isEmpty()) {
        return zeroList
    }

    return if (selector.invoke(zeroList.size, oneList.size)) zeroList else oneList
}

var gammalist: List<String> = ArrayList(input)
var epsilonlist: List<String> = ArrayList(input)

for (i in 0 until length) {
    // ties go to ones
    gammalist = filterByBitPositionCondition(gammalist, i) { a: Int, b: Int -> a > b }
    // ties go to zeros
    epsilonlist = filterByBitPositionCondition(epsilonlist, i) { a: Int, b: Int -> a <= b }
}

println(gammalist.last().toInt(2) * epsilonlist.last().toInt(2))