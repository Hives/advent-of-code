package main

import (
	"aoc"
	"fmt"
	"reader"
	"strconv"
	"strings"
)

func main() {
	inputPath := "./input.txt"
	aoc.CheckAnswer("Part 1", part1(parse(inputPath)), 3716250)
	aoc.CheckAnswer("Part 2", part2(parse(inputPath)), 6472)
}

func parse(path string) []int {
	input := reader.Strings(path)
	split := strings.Split(input[0], ",")
	var ints []int
	for _, s := range split {
		n, _ := strconv.Atoi(s)
		ints = append(ints, n)
	}
	return ints
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
	program := append([]int{}, inputProgram...)
	program[1] = noun
	program[2] = verb

	pointer := 0

	for program[pointer] != 99 {
		opcode := program[pointer]
		switch opcode {
		case 1:
			// addition
			input1Position := program[pointer+1]
			input2Position := program[pointer+2]
			outputPosition := program[pointer+3]

			program[outputPosition] = program[input1Position] + program[input2Position]

			pointer = pointer + 4
		case 2:
			// multiplication
			input1Position := program[pointer+1]
			input2Position := program[pointer+2]
			outputPosition := program[pointer+3]

			program[outputPosition] = program[input1Position] * program[input2Position]

			pointer = pointer + 4
		default:
			panic(fmt.Sprintf("Unknown opcode: %v", opcode))
		}
	}

	return program
}
