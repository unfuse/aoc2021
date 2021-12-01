import java.io.File

var nums: ArrayList<Int> = ArrayList()

File("/Users/david.kennedy/code/hacks/aoc2021/src/main/resources/day01.txt").forEachLine {
    nums.add(it.toInt())
}

// Part 1
println(nums.windowed(2, 1).count { it[1] > it[0] })

// Part 2
println(nums.windowed(3, 1) { it.sum() }.windowed(2, 1).count { it[1] > it[0] })