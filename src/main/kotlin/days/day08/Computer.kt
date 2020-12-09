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
        programIsComplete(program, newState) -> Pair(newState, FINITO)
        programIsLooping(newState, pointerHistory) -> Pair(newState, INFINITE_LOOP)
        else -> iterate(program, newState, pointerHistory + newState.pointer)
    }
}

data class State(val pointer: Int, val accumulator: Int) {
    companion object {
        val INITIAL = State(0, 0)
    }
}

fun computeNextState(state: State, program: List<String>) =
    Operation.from(program[state.pointer]).operate(state)

private fun programIsComplete(
    program: List<String>,
    newState: State
) = newState.pointer >= program.size

private fun programIsLooping(
    newState: State,
    pointerHistory: List<Int>
) = pointerHistory.contains(newState.pointer)

enum class FinalStatus {
    INFINITE_LOOP, FINITO
}

private abstract class Operation {
    abstract fun operate(state: State): State

    object NoOp : Operation() {
        override fun operate(state: State) =
            state.copy(pointer = state.pointer + 1)
    }

    class Jump(val argument: Int) : Operation() {
        override fun operate(state: State) =
            state.copy(pointer = state.pointer + argument)
    }

    class Accumulate(val argument: Int) : Operation() {
        override fun operate(state: State) =
            State(
                pointer = state.pointer + 1,
                accumulator = state.accumulator + argument
            )
    }

    companion object {
        fun from(input: String): Operation {
            val (operation, argument) = input.split(" ")

            return when(operation) {
                "nop" -> NoOp
                "jmp" -> Jump(argument.toInt())
                "acc" -> Accumulate(argument.toInt())
                else -> throw Exception("Unrecognised operation")
            }
        }
    }
}

fun switchJmpAndNop(instruction: String): String =
    when (instruction.take(3)) {
        "jmp" -> "nop" + instruction.drop(3)
        "nop" -> "jmp" + instruction.drop(3)
        else -> instruction
    }

fun <T> List<T>.replace(n: Int, value: T): List<T> =
    this.subList(0, n) + value + this.subList(n + 1, this.size)
