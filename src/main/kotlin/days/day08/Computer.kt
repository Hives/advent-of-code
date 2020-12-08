package days.day08

import days.day08.Operation.*
import days.day08.Status.FINISHED
import days.day08.Status.INFINITE_LOOP

fun compute(program: List<String>, history: History): History {
    val currentPointer = history.pointer.last()
    val currentAcc = history.acc.last()

    val (operation, argument) = parseCode(program[currentPointer])

    return when (operation) {
        NOP -> History(
            pointer = history.pointer + (currentPointer + 1),
            acc = history.acc + currentAcc
        )
        JMP -> History(
            pointer = history.pointer + (currentPointer + argument),
            acc = history.acc + currentAcc
        )
        ACC -> History(
            pointer = history.pointer + (currentPointer + 1),
            acc = history.acc + (currentAcc + argument)
        )
    }
}

tailrec fun execute(program: List<String>, initial: History = History(listOf(0), listOf(0))): Pair<History, Status> {
    val new = compute(program, initial)

    return when {
        new.isInLoop() -> Pair(new, INFINITE_LOOP)
        new.isFinished(program) -> Pair(new, FINISHED)
        else -> execute(program, new)
    }
}

private val instructionRegex = """(\w{3}) ([-+])(\d+)""".toRegex()

private enum class Operation {
    NOP, JMP, ACC
}

enum class Status {
    INFINITE_LOOP, FINISHED
}

private fun parseCode(input: String): Pair<Operation, Int> {
    val (operation, sign, argument) = instructionRegex.find(input)!!.destructured

    return Pair(
        Operation.valueOf(operation.toUpperCase()),
        argument.toInt().let { if (sign == "-") it * -1 else it }
    )
}

fun switchJmpAndNop(line: String): String =
    when (line.take(3)) {
        "jmp" -> "nop" + line.drop(3)
        "nop" -> "jmp" + line.drop(3)
        else -> line
    }

fun <T> List<T>.replace(n: Int, value: T): List<T> =
    this.subList(0, n) + value + this.subList(n + 1, this.size)

data class History(val pointer: List<Int>, val acc: List<Int>) {
    val size: Int
        get() = pointer.size

    fun isInLoop() = pointer.subList(0, pointer.size - 1).contains(pointer.last())
    fun isFinished(program: List<String>) = pointer.last() >= program.size

    companion object {
        fun new() = History(listOf(0), listOf(0))
    }
}
