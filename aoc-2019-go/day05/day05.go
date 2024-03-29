package main

import (
	"aoc"
	"fmt"
	"reader"
)

func main() {
	input := reader.Program("./input.txt")
	aoc.CheckAnswer("Part 1", part1(input), 5044655)
	aoc.CheckAnswer("Part 2", part2(input), 7408802)
}

func part1(input []int) int {
	initial := state{
		program: sliceToMap(input),
		input:   []int{1},
		pointer: 0,
	}
	final := run(initial)
	return final.output[len(final.output)-1]
}

func part2(input []int) int {
	initial := state{
		program: sliceToMap(input),
		input:   []int{5},
		pointer: 0,
	}
	final := run(initial)
	return final.output[len(final.output)-1]
}

func sliceToMap(ns []int) map[int]int {
	var ret = make(map[int]int)
	for i, n := range ns {
		ret[i] = n
	}
	return ret
}

func run(state state) state {

	for state.readOpcode() != 99 {
		pointer := state.pointer

		modes := state.parameterModes()
		param1 := state.getParam(pointer+1, modes.get(0))
		param2 := state.getParam(pointer+2, modes.get(1))

		switch state.readOpcode() {
		case 1:
			// addition
			writePosition := state.program[pointer+3]
			state.program[writePosition] = param1 + param2
			state.pointer = pointer + 4
		case 2:
			// multiplication
			writePosition := state.program[pointer+3]
			state.program[writePosition] = param1 * param2
			state.pointer = pointer + 4
		case 3:
			// read input
			input := state.takeInput()
			writePosition := state.program[pointer+1]
			state.program[writePosition] = input
			state.pointer = pointer + 2
		case 4:
			// write output
			readPosition := state.program[pointer+1]
			value := state.program[readPosition]
			state.output = append(state.output, value)
			state.pointer = pointer + 2
		case 5:
			// jump if true
			if param1 != 0 {
				state.pointer = param2
			} else {
				state.pointer = pointer + 3
			}
		case 6:
			// jump if false
			if param1 == 0 {
				state.pointer = param2
			} else {
				state.pointer = pointer + 3
			}
		case 7:
			// less than
			writePosition := state.program[pointer+3]
			if param1 < param2 {
				state.program[writePosition] = 1
			} else {
				state.program[writePosition] = 0
			}
			state.pointer = pointer + 4
		case 8:
			// equals
			writePosition := state.program[pointer+3]
			if param1 == param2 {
				state.program[writePosition] = 1
			} else {
				state.program[writePosition] = 0
			}
			state.pointer = pointer + 4
		default:
			panic(fmt.Sprintf("Unknown opcode: %v", state.readOpcode()))
		}
	}

	return state
}

type state struct {
	program map[int]int
	input   []int
	output  []int
	pointer int
}

func (s state) current() int {
	return s.program[s.pointer]
}

func (s state) readOpcode() int {
	return s.current() % 100
}

func (s state) parameterModes() defaultedList {
	modesDigits := s.current() / 100
	var modes []int
	for modesDigits > 0 {
		digit := modesDigits % 10
		modes = append(modes, digit)
		modesDigits = modesDigits / 10
	}
	return defaultedList{ns: modes, fallback: 0}
}

func (s state) getParam(i int, mode int) int {
	switch mode {
	case 0:
		// position mode
		position := s.program[i]
		value := s.program[position]
		return value
	case 1:
		// immediate mode
		value := s.program[i]
		return value
	default:
		panic(fmt.Sprintf("Unknown parameter mode: %v", mode))
	}
}

func (s state) takeInput() int {
	input, remainingInput := s.input[0], s.input[1:]
	s.input = remainingInput
	return input
}

func (s state) writeOutput(value int) {
	s.output = append(s.output, value)
}

func (s state) print() {
	fmt.Println("------------------")
	fmt.Println("program: ", s.program)
	fmt.Println("pointer: ", s.pointer)
	fmt.Println("input: ", s.input)
	fmt.Println("output: ", s.output)
	fmt.Println("------------------")
}

type defaultedList struct {
	ns       []int
	fallback int
}

func (l defaultedList) get(i int) int {
	if i < len(l.ns) {
		return l.ns[i]
	}
	return l.fallback
}
