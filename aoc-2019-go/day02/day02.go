package main

import (
	"aoc"
	"fmt"
	"reader"
)

func main() {
	input := reader.Program("./input.txt")
	aoc.CheckAnswer("Part 1", part1(input), 3716250)
	aoc.CheckAnswer("Part 2", part2(input), 6472)
}

func part1(program []int) int {
	finalState := run(program, 12, 2)
	return finalState[0]
}

func part2(program []int) int {
	for noun := range [100]int{} {
		for verb := range [100]int{} {
			finalState := run(program, noun, verb)
			if finalState[0] == 19690720 {
				return 100*noun + verb
			}
		}
	}

	return -1
}

func run(inputProgram []int, noun int, verb int) []int {
	state := state{
		program: initialise(inputProgram, noun, verb),
		pointer: 0,
	}

	for state.current() != 99 {
		switch state.current() {
		case 1:
			opcode1Addition(&state)
		case 2:
			opcode2Multiplication(&state)
		default:
			panic(fmt.Sprintf("Unknown opcode: %v", state.current()))
		}
	}

	return state.program
}

func initialise(inputProgram []int, noun int, verb int) []int {
	program := append([]int{}, inputProgram...)
	program[1] = noun
	program[2] = verb
	return program
}

func opcode1Addition(s *state) {
	input1Position := s.currentPlus(1)
	input2Position := s.currentPlus(2)
	outputPosition := s.currentPlus(3)

	s.set(outputPosition, s.read(input1Position)+s.read(input2Position))
	s.pointer = s.pointer + 4
}

func opcode2Multiplication(s *state) {
	input1Position := s.currentPlus(1)
	input2Position := s.currentPlus(2)
	outputPosition := s.currentPlus(3)

	s.set(outputPosition, s.read(input1Position)*s.read(input2Position))
	s.pointer = s.pointer + 4
}

type state struct {
	program []int
	pointer int
}

func (s state) current() int {
	return s.program[s.pointer]
}

func (s state) currentPlus(n int) int {
	return s.program[s.pointer+n]
}

func (s state) read(i int) int {
	return s.program[i]
}

func (s state) set(i int, value int) state {
	program := s.program
	program[i] = value
	return state{
		program: program,
		pointer: s.pointer,
	}
}
