class ALU(private val program: List<String>) {
    val registers : MutableMap<String, Int> = mutableMapOf("w" to 0, "x" to 0, "y" to 0, "z" to 0)
    var w : Int
        get() = registers["w"]!!
        set(value) { registers["w"] = value }
    var x : Int
        get() = registers["x"]!!
        set(value) { registers["x"] = value }
    var y : Int
        get() = registers["y"]!!
        set(value) { registers["y"] = value }
    var z : Int
        get() = registers["z"]!!
        set(value) { registers["z"] = value }

    fun runProgram(input: List<Int>) : Int {
        var inputCounter = 0
        program.forEach { line ->
//            println("process line $line")
            val parts = line.trim().split(" ")

            val updateRegister : (f: (a: Int, b: Int) -> Int) -> Unit = { f ->
                registers.compute(parts[1]) { _: String, i: Int? ->
                    i?.let { f.invoke(it, getRegisterOrInt(parts[2])) } ?: getRegisterOrInt(parts[2])
                }
            }

            when (parts[0]) {
                "inp" -> registers[parts[1]] = input[inputCounter++]
                "add" -> updateRegister(Int::plus)
                "mul" -> updateRegister(Int::times)
                "div" -> updateRegister(Int::div)
                "mod" -> updateRegister(Int::mod)
                "eq"  -> updateRegister{ a, b -> if (a == b) 1 else 0 }
            }
        }
        return z
    }

    private fun getRegisterOrInt(item: String) : Int {
        return when (item) {
            "w", "x", "y", "z" -> registers[item]!!
            else -> item.toInt()
        }
    }

    fun clearRegisters() {
        registers.clear()
    }
}

val input = Utils.readFileAsStringList("test")

val alu = ALU(input)

fun findLargestSerial() : Long {
    // abcde fghij klmn
    for (a in 9 downTo 1) {
        for (b in 9 downTo 1) {
            for (c in 9 downTo 1) {
                for (d in 9 downTo 1) {
                    for (e in 9 downTo 1) {
                        for (f in 9 downTo 1) {
                            for (g in 9 downTo 1) {
                                for (h in 9 downTo 1) {
                                    for (i in 9 downTo 1) {
                                        for (j in 9 downTo 1) {
                                            for (k in 9 downTo 1) {
                                                for (l in 9 downTo 1) {
                                                    for (m in 9 downTo 1) {
                                                        for (n in 9 downTo 1) {
                                                            val serial = listOf(a, b, c, d, e, f, g, h, i, j, k, l, m, n)
                                                            println("try $serial")
                                                            if (alu.runProgram(serial) == 0) {
                                                                return serial.joinToString("").toLong()
                                                            }
                                                            else {
                                                                println("  z = ${alu.z}")
                                                                alu.clearRegisters()
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    return -1
}

println(findLargestSerial())

/**
 * attempts at decompiling the the input source code to figure out shortcuts
 *
inp w
mul x 0 # reset x
add x z # apply carryover to x
mod x 26 # up to 26 of it
div z 1 # seems use less, same as z = z
add x 10 # x += 10
eql x w # w is always 1-9 so this will always be 0
eql x 0 # always x = 1
mul y 0 # reset y
add y 25 # y += 25
mul y x # since x = 1, y = y
add y 1 # y += 1
mul z y # z *= 26 whatever it was before
mul y 0 # reset y
add y w # FINALLY USE THE INPUT y = w
add y 0 # no-op
mul y x # y *= x ( == w *= x)
add z y # z += y ( == z += w * x)

inp w
mul x 0
add x z
mod x 26
div z 1
add x 12 # 10
eql x w
eql x 0
mul y 0
add y 25
mul y x
add y 1
mul z y
mul y 0
add y w
add y 6
mul y x
add z y

inp w
mul x 0
add x z
mod x 26
div z 1
add x 13
eql x w
eql x 0
mul y 0
add y 25
mul y x
add y 1
mul z y
mul y 0
add y w
add y 4
mul y x
add z y

inp w
mul x 0
add x z
mod x 26
div z 1
add x 13
eql x w
eql x 0
mul y 0
add y 25
mul y x
add y 1
mul z y
mul y 0
add y w
add y 2
mul y x
add z y

inp w
mul x 0
add x z
mod x 26
div z 1
add x 14
eql x w
eql x 0
mul y 0
add y 25
mul y x
add y 1
mul z y
mul y 0
add y w
add y 9
mul y x
add z y

inp w
mul x 0
add x z
mod x 26
div z 26
add x -2
eql x w
eql x 0
mul y 0
add y 25
mul y x
add y 1
mul z y
mul y 0
add y w
add y 1
mul y x
add z y

inp w
mul x 0
add x z
mod x 26
div z 1
add x 11
eql x w
eql x 0
mul y 0
add y 25
mul y x
add y 1
mul z y
mul y 0
add y w
add y 10
mul y x
add z y

inp w
mul x 0
add x z
mod x 26
div z 26
add x -15
eql x w
eql x 0
mul y 0
add y 25
mul y x
add y 1
mul z y
mul y 0
add y w
add y 6
mul y x
add z y

inp w
mul x 0
add x z
mod x 26
div z 26
add x -10
eql x w
eql x 0
mul y 0
add y 25
mul y x
add y 1
mul z y
mul y 0
add y w
add y 4
mul y x
add z y

inp w
mul x 0
add x z
mod x 26
div z 1
add x 10
eql x w
eql x 0
mul y 0
add y 25
mul y x
add y 1
mul z y
mul y 0
add y w
add y 6
mul y x
add z y

inp w
mul x 0
add x z
mod x 26
div z 26
add x -10
eql x w
eql x 0
mul y 0
add y 25
mul y x
add y 1
mul z y
mul y 0
add y w
add y 3
mul y x
add z y

inp w
mul x 0
add x z
mod x 26
div z 26
add x -4
eql x w
eql x 0
mul y 0
add y 25
mul y x
add y 1
mul z y
mul y 0
add y w
add y 9
mul y x
add z y

inp w
mul x 0
add x z
mod x 26
div z 26
add x -1
eql x w
eql x 0
mul y 0
add y 25
mul y x
add y 1
mul z y
mul y 0
add y w
add y 15
mul y x
add z y

inp w
mul x 0
add x z
mod x 26
div z 26
add x -1
eql x w
eql x 0
mul y 0
add y 25
mul y x
add y 1
mul z y
mul y 0
add y w
add y 5
mul y x
add z y
 */