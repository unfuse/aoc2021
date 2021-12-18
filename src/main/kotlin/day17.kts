import Utils.Companion.toward
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

val input = Utils.readFileAsString("test")

val corners: Pair<Point, Point> = input.substringAfter("target area:")
    .split(",")
    .flatMap { it.trim().split("=").drop(1) }
    .flatMap { it.trim().split("..") }
    .map { it.toInt() }
    .windowed(4, 4)
    .map { Pair(Point(it[0], it[2]), Point(it[1], it[3])) }
    .first()

println(corners) // bottom left and top right

/**
 * For every point in the target grid, there is a boring straight line that takes you there and is worth negative style points
 * Call this straight line (X, Y) [== the position of the target point]
 *
 * There may also be parabolic lines to that same point. We can derive them from this basic line.
 *
 * To get possible X vector values, we want to find each value (i) < X such that some sequence length (n) from (i) sums to X
 *
 * Define: Pascal(x) = sum of first x numbers = (x(x+1))/2
 *
 * For (i in x..1) // for every number in 1..X
 *   For (n in 1..i) // for every number 1..^
 *      // is there a sequence of (n) numbers that add to (X)
 *        dividend = x - Pascal(n-1)
 *        if (dividend % n == 0) // divides evenly, no remainder
 *          i = dividend/n
 *          record {initial velocity, num steps} of { i, n } as possible solution
 *
 * These recorded values give us possible X vector values for the initial vector
 *
 * To get possible Y vector values for some initial trajectory X value (i) we want to find the largest value (m) such that
 *   Note: is the second pascal value just the number of steps - 1? Or is it intrinsically tied to the i value?
 *   Total Y Change = Pascal(m) - Pascal(abs(i-m)-1) == Y
 * We can record solutions similarly:
 *   For some { i, n } from part 1
 *   For (m in Y..1)
 *     if (Total_Y_Change(m) == Y) record corresponding Y value { m } for that { i, n }
 *
 * A total solution here is a trajectory (i, m) that will hit the point (X, Y) after (n) steps and reach max height
 *   Pascal(m).
 *
 * We then just wish to find the trajectory with the maxest height!
 */

/**
 * The important thing
 */
fun pascal(i: Int): Int {
    return (i * (i+1))/2
}

data class PossibleTrajectory(val to: Point, val initial: Point, val numSteps: Int) {
    val maxHeight = (initial.y * (initial.y+1))/2
}

fun getPotentialIndices(target: Int) : Set<Pair<Int, Int>> {
    val results = mutableSetOf<Pair<Int, Int>>()
    for (n in 1.toward(target)) {
        isPotentialXIndex(target, n)?.let {
            results += Pair(it, n)
        }
    }
    return results.toSet()
}

/**
 * While i + (i+1) + ... + (i+n) == X is the qualification we are looking for, the actual useful return value
 *   is (i+n) because we want to shoot at (i+n) and have it go down to (i)
 *
 * In other words, we actually want to find (i+n) such that (i+n) + (i+n-1) + ... + i == X :)
 */
fun isPotentialXIndex(target: Int, sequenceLength: Int) : Int? {
//    println("    try $target in $sequenceLength steps")
    val dividend = target - pascal(sequenceLength - 1)
    if (dividend > 0 && dividend % sequenceLength == 0) {
        val result = ((dividend/sequenceLength) + sequenceLength) - 1
//        println("    div = $dividend - result would be $result")
        return result
    }
    return null
}

// -1 because y doesn't change in one of the steps because it sits at y=0 trajectory for a step
fun totalYTraveled(y: Int, xValue: Pair<Int, Int>) : Int {
    // x steps more than y
    if (y < xValue.second) return pascal(y) - pascal(xValue.second - y - 1)
    // x steps same as y
    else if (y == xValue.second) return pascal(y)
    // y steps more than x, need to allow max y step to grow
    else {
        // y continues to move after x = 0 and plummets. it's allowed to do this for as long as it wants
        return 0
    }
}

/**
 * the total Y traveled over n steps for initial velocity y is
 *   if n <= y Pascal(y) - Pascal(y-n) (e.g. y = 9, n = 1, dist = 9 ; y = 9, n = 2, dist = 17)
 *   if n > y
 *     Pascal(y) // for the way up
 *     + Pascal(n-y-1) // hold for 1 step
 *                     // come down for remaining n, this is unbounded
 *
 *   All together this should be
 *     Pascal(y) - max(0, y-n) * Pascal(<) + max(0, n-y-1) * Pascal(<)
 *
 *   We wish to find the y, n that hits some Y given the x n
 */
fun yDistTraveledInStep(yValue: Int, atStep: Int) : Int {
    return pascal(yValue) - (max(0, yValue-atStep).also { it * pascal(it) } + (max(0, yValue-atStep-1).also { it * pascal(it) }))
}

fun findValidY(target: Int, xValue: Pair<Int, Int>) : Pair<Int, Int>? {
    println("  find m for Y = $target from X = $xValue")
    for (m in abs(target).toward(1)) {
        var stepCounter = xValue.second
        while (true) {
            val dist = yDistTraveledInStep(m, stepCounter)
            println("   $m -> $dist ? $target")
            when(dist.compareTo(target)) {
                1 -> break
                0 -> { println("   found $m $stepCounter"); return Pair(m, stepCounter) }
                -1 -> stepCounter++
                else -> throw IllegalStateException("comparison gave invalid value")
            }
        }
    }
    println("  no m found")
    return null
}

val trajectories : MutableSet<PossibleTrajectory> = mutableSetOf()
for (landingPoint in Utils.squareOfExcept(corners.first, corners.second, emptySet())) {
    println("Process landing point: $landingPoint")
    for (possibleX in getPotentialIndices(landingPoint.x)) {
        println(" possible x step: $possibleX")
        findValidY(landingPoint.y, possibleX)?.let { (yVal, yStep) ->
            val tr = PossibleTrajectory(landingPoint, Point(possibleX.first, yVal), max(possibleX.second, yStep))
            println("    found tr $tr")
            trajectories.add(tr)
        }
    }
}

val bestTrajectory = trajectories.maxByOrNull { it.maxHeight }

bestTrajectory?.let {  println("$bestTrajectory at ${bestTrajectory.maxHeight}") } ?: run { println("no solutions") }