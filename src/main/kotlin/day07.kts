import Utils.Companion.toward
import kotlin.math.abs
import kotlin.math.roundToInt

val input : List<Int> = Utils.readFile<ArrayList<Int>, List<Int>>("day07",
    { line -> line.split(",").map{ it.toInt() } },
    ::ArrayList,
    { item, collector -> collector.addAll(item) })
    .sorted()

fun List<Int>.median() : Int {
    return if (this.size % 2 != 0) {
        val half = this.size / 2
        (this[half] + this[half + 1]) / 2
    }
    else {
        this[this.size.div(2.0).roundToInt()]
    }
}

val med = input.median()
val avg = input.average().toInt()

// sum diff to median
println("part 1 = " + input.sumOf { abs(it - med) })
// sum "factorial addition" of diff to average
println("part 2 = " + input.sumOf { abs(it - avg).toward(0).sum() })