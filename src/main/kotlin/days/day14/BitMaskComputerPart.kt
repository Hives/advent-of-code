package days.day14

import kotlin.math.pow

class BitMaskComputerPart1(program: List<String>) : BitMaskComputer(program) {
    override fun updateMemory() {
        val (location, value) = readNonMaskLine()
        mem[location.toLong()] = applyMask(value.toInt().toBinaryString36())
    }

    private fun applyMask(input: String) = input.zip(mask).map { (value, maskValue) ->
        if (maskValue == 'X') value
        else maskValue
    }.joinToString("")
}

class BitMaskComputerPart2(program: List<String>) : BitMaskComputer(program) {
    override fun updateMemory() {
        val (location, value) = readNonMaskLine()
        val maskedLocation = applyMask(location.toInt().toBinaryString36())

        resolveFloatingBits(maskedLocation).forEach {
            mem[it.toLong(2)] = value.toInt().toBinaryString36()
        }
    }

    private fun applyMask(input: String) = input.zip(mask).map { (value, maskValue) ->
        when (maskValue) {
            '0' -> value
            '1' -> '1'
            'X' -> 'X'
            else -> throw Exception("Unknown character in bitmask: $maskValue")
        }
    }.joinToString("")

    private fun resolveFloatingBits(input: String): List<String> {
        val xCount = input.count { it == 'X' }
        val permutations = 2.0.pow(xCount).toInt()

        return (0 until permutations).map {
            val binaryDigits = it.toBinaryString().padZeros(xCount).toList()
            binaryDigits.fold(input) { acc, c ->
                acc.replaceFirst('X', c)
            }
        }
    }
}

sealed class BitMaskComputer(private val program: List<String>) {
    protected lateinit var mask: String
    protected var mem = mutableMapOf<Long, String>()
    private var pointer = 0

    private val memRegex = """^mem\[(\d+)] = (\d+)$""".toRegex()

    val sumOfValues
        get() = mem.values.map { it.toLong(2) }.sum()

    fun run() {
        while (pointer < program.size) crunch()
    }

    private fun crunch() {
        if (program[pointer].take(4) == "mask") getNewMask()
        else updateMemory()

        pointer++
    }

    private fun getNewMask() {
        mask = program[pointer].split(" ").last()
    }

    protected abstract fun updateMemory()

    protected fun readNonMaskLine(): Pair<String, String> {
        val (location, value) = memRegex.find(program[pointer])!!.destructured
        return Pair(location, value)
    }

    protected fun Int.toBinaryString36(): String = this.toBinaryString().padZeros(36)
    protected fun Int.toBinaryString(): String = Integer.toBinaryString(this)

    protected fun String.padZeros(length: Int) =
        if (this.length < length) "0".repeat(length - this.length) + this
        else this

}

