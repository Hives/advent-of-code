package days.day08

import days.day08.FinalStatus.FINITO
import days.day08.FinalStatus.INFINITE_LOOP

fun execute(program: List<String>) = iterate(program, State.INITIAL, emptyList())

private tailrec fun iterate(
    program: List<String>,
    state: State,
    pointerHistory: List<Int>
): Pair<State, FinalStatus> {
    val newState = computeNextState(state, program)

    return when {
        newState.pointer >= program.size -> Pair(newState, FINITO)
        pointerHistory.contains(newState.pointer) -> Pair(newState, INFINITE_LOOP)
        else -> iterate(program, newState, pointerHistory + newState.pointer)
    }
}

fun computeNextState(state: State, program: List<String>): State {
    val (operation, argument) = parseInstruction(program[state.pointer])
    return operation.operate(argument, state)
}

private fun parseInstruction(input: String): Pair<Operation, Int> {
    val (operation, argument) = input.split(" ")

    return Pair(
        Operation.valueOf(operation.toUpperCase()),
        argument.toInt()
    )
}

private enum class Operation {
    NOP {
        override fun operate(argument: Int, state: State) =
            state.copy(pointer = state.pointer + 1)
    },
    JMP {
        override fun operate(argument: Int, state: State) =
            state.copy(pointer = state.pointer + argument)
    },
    ACC {
        override fun operate(argument: Int, state: State) =
            State(
                pointer = state.pointer + 1,
                accumulator = state.accumulator + argument
            )
    };

    abstract fun operate(argument: Int, state: State): State
}

enum class FinalStatus {
    INFINITE_LOOP, FINITO
}

fun switchJmpAndNop(instruction: String): String =
    when (instruction.take(3)) {
        "jmp" -> "nop" + instruction.drop(3)
        "nop" -> "jmp" + instruction.drop(3)
        else -> instruction
    }

fun <T> List<T>.replace(n: Int, value: T): List<T> =
    this.subList(0, n) + value + this.subList(n + 1, this.size)

data class State(val pointer: Int, val accumulator: Int) {
    companion object {
        val INITIAL = State(0, 0)
    }
}
