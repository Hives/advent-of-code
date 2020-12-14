package days.day14

private val example = ("mask = XXXXXXXXXXXXXXXXXXXXXXXXXXXXX1XXXX0X\n" +
        "mem[8] = 11\n" +
        "mem[7] = 101\n" +
        "mem[8] = 0").trim().lines()

fun main() {
    val b = BitMaskComputerPart1(example)
    b.go()
    b.print()
    println(b.addValues())
}

class BitMaskComputerPart1(val program: List<String>) {
    lateinit var mask: String
    var mem = mutableMapOf<Int, String>()
    var pointer = 0

    private val memRegex = """^mem\[(\d+)\] = (\d+)$""".toRegex()

    fun go() {
        while(pointer < program.size) {
            crunch()
        }
    }

    fun crunch() {
        if (program[pointer].take(4) == "mask") updateMask()
        else writeMemory()

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

    private fun writeMemory() {
        val (location, value) = memRegex.find(program[pointer])!!.destructured
        mem[location.toInt()] = value.toInt().toBinary36().applyMask(mask)
    }
}

private fun Int.toBinary36() = Integer.toBinaryString(this).pad36()

private fun String.applyMask(mask: String) = this.zip(mask).map { (value, maskValue) ->
    if (maskValue == 'X') value
    else maskValue
}.joinToString("")

private fun String.pad36() =
    if (this.length < 36) "0".repeat(36 - this.length) + this
    else this

