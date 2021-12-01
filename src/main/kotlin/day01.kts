val nums: List<Int> = Utils.readFileAsIntList("day01")

// Part 1
println(nums.windowed(2).count { it[1] > it[0] })

// Part 2
println(nums.windowed(3) { it.sum() }.windowed(2).count { it[1] > it[0] })