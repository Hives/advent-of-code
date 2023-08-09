package main

import (
	"aoc"
	"fmt"
	"reader"
)

func main() {
	input := reader.Program("./input.txt")
	aoc.CheckAnswer("Part 1", part1(input), 5044655)
}

func part1(program []int) int {
	initial := state{
		program: sliceToMap(program),
		input:   []int{1},
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
	counter := 0

	for state.readOpcode() != 99 {
		counter = counter + 1
		state.print()
		fmt.Println("state.current(): ", state.current())
		switch state.readOpcode() {
		case 1:
			opcode1Addition(&state)
		case 2:
			opcode2Multiplication(&state)
		case 3:
			opcode3Input(&state)
		case 4:
			opcode4Output(&state)
		default:
			panic(fmt.Sprintf("Unknown opcode: %v", state.readOpcode()))
		}
	}

	fmt.Println("counter: ", counter)

	return state
}

func opcode1Addition(s *state) {
	fmt.Println("Addin")

	modes := s.parameterModes()

	param1 := s.getParam(s.pointer+1, getOrZero(modes, 0))
	param2 := s.getParam(s.pointer+2, getOrZero(modes, 1))
	outputPosition := s.program[s.pointer+3]

	set(s, outputPosition, param1+param2)

	s.pointer = s.pointer + 4
}

func opcode2Multiplication(s *state) {
	fmt.Println("Multiplyin")

	modes := s.parameterModes()

	param1 := s.getParam(s.pointer+1, getOrZero(modes, 0))
	param2 := s.getParam(s.pointer+2, getOrZero(modes, 1))
	outputPosition := s.program[s.pointer+3]

	set(s, outputPosition, param1*param2)

	s.pointer = s.pointer + 4
}

func opcode3Input(s *state) {
	fmt.Println("Inputtin")

	writePosition := s.program[s.pointer+1]
	input := s.takeInput()

	fmt.Println(fmt.Sprintf("Writing %v to position %v", input, writePosition))

	set(s, writePosition, input)

	s.pointer = s.pointer + 2
}

func opcode4Output(s *state) {
	fmt.Println("Outputtin")

	readPosition := s.program[s.pointer+1]

	s.output = append(s.output, s.program[readPosition])

	s.pointer = s.pointer + 2
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

func (s state) parameterModes() []int {
	modesDigits := s.current() / 100
	var modes []int
	for modesDigits > 0 {
		digit := modesDigits % 10
		modes = append(modes, digit)
		modesDigits = modesDigits / 10
	}
	return modes
}

func (s state) read(i int) int {
	return s.program[i]
}

func (s state) getParam(i int, mode int) int {
	fmt.Println(fmt.Sprintf("getParam with i = %v, mode = %v", i, mode))
	switch mode {
	case 0:
		// position mode
		fmt.Println("position mode")
		position := s.program[i]
		fmt.Println("position: ", position)
		value := s.program[position]
		fmt.Println("value: ", value)
		return value
	case 1:
		// immediate mode
		fmt.Println("immediate mode")
		value := s.program[i]
		fmt.Println("value: ", value)
		return value
	default:
		panic(fmt.Sprintf("Unknown parameter mode: %v", mode))
	}
}

func set(s *state, i int, value int) {
	s.program[i] = value
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
}

func getOrZero(ns []int, i int) int {
	if i < len(ns) {
		return ns[i]
	}
	return 0
}
