package days.day14

import kotlin.math.pow

private val example = ("mask = 000000000000000000000000000000X1001X\n" +
        "mem[42] = 100\n" +
        "mask = 00000000000000000000000000000000X0XX\n" +
        "mem[26] = 1\n").trim().lines()

fun main() {
    val b = BitMaskComputerPart2(example)
    b.go()
    b.addValues().also{ println(it) }
}

class BitMaskComputerPart2(val program: List<String>) {
    lateinit var mask: String
    var mem = mutableMapOf<Long, String>()
    var pointer = 0

    private val memRegex = """^mem\[(\d+)\] = (\d+)$""".toRegex()

    fun go() {
        while (pointer < program.size) {
            crunch()
        }
    }

    fun crunch() {
        if (program[pointer].take(4) == "mask") updateMask()
        else updateMemory()

        pointer++
    }

    fun addValues() = mem.toList().map { it.second.toLong(2) }.sum()

    fun print() {
        println("--")
        println("mask: $mask")
        println("mem:")
        println(mem)
    }

    private fun updateMask() {
        mask = program[pointer].split(" ").last()
    }

    private fun updateMemory() {
        val (location, value) = memRegex.find(program[pointer])!!.destructured

        val resolvedLocations = resolveFloatingBits(location.toBinary36().applyMask(mask))
        resolvedLocations.forEach {
            mem[it.toLong(2)] = value.toBinary36()
        }
    }
}

private fun resolveFloatingBits(input: String): List<String> {
    val xCount = input.count { it == 'X' }
    val permutations = 2.0.pow(xCount).toInt()

    return (0 until permutations).map {
        var binaryDigits = it.toBinary().padZeros(xCount).toList()
        var output = input
        while (binaryDigits.isNotEmpty()) {
            output = output.replaceFirst('X', binaryDigits.first())
            binaryDigits = binaryDigits.drop(1)
        }
        output
    }
}

private fun Int.toBinary() = Integer.toBinaryString(this)
private fun Int.toBinary36() = this.toBinary().padZeros(36)
private fun String.toBinary36() = this.toInt().toBinary36()

private fun String.applyMask(mask: String) = this.zip(mask).map { (value, maskValue) ->
    when (maskValue) {
        '0' -> value
        '1' -> '1'
        'X' -> 'X' // floating
        else -> throw Exception("Unknown character in bitmask: $maskValue")
    }
}.joinToString("")

private fun String.padZeros(length: Int) =
    if (this.length < length) "0".repeat(length - this.length) + this
    else this

