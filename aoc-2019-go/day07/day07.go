package main

import (
	"aoc"
	"errors"
	"fmt"
	"gonum.org/v1/gonum/stat/combin"
	"reader"
)

func main() {
	input := reader.Program("./input.txt")
	aoc.CheckAnswer("Part 1", part1(input), 366376)
	aoc.CheckAnswer("Part 2", part2(input), 366376)
}

func part1(input []int) int {
	phaseSettings := combin.Permutations(5, 5)
	best := 0
	for _, phaseSetting := range phaseSettings {
		output := evaluatePhaseSettings(phaseSetting, input)
		if output > best {
			best = output
		}
	}
	return best
}

func part2(input []int) int {
	phaseSettings := combin.Permutations(5, 5)
	for i, _ := range phaseSettings {
		var plusFive []int
		for _, n := range phaseSettings[i] {
			plusFive = append(plusFive, n+5)
		}
		phaseSettings[i] = plusFive
	}
	fmt.Println("phaseSettings: ", phaseSettings)
	best := 0
	for _, phaseSetting := range phaseSettings {
		output := evaluatePhaseSettingsWithFeedback(phaseSetting, input)
		if output > best {
			best = output
		}
	}
	return best
}

func evaluatePhaseSettingsWithFeedback(
	phaseSettings []int,
	program []int,
) int {
	var amps []state
	for _, phaseSetting := range phaseSettings {
		initial := state{
			program: sliceToMap(program),
			input:   []int{phaseSetting},
			pointer: 0,
		}
		amps = append(amps, initial)
	}

	amps[0].input = append(amps[0].input, 0)

	ampIndex := 0

	for {
		updatedAmp := run(amps[ampIndex])
		amps[ampIndex] = updatedAmp

		if allAreComplete(amps) {
			break
		}

		nextAmpIndex := (ampIndex + 1) % 5

		amps[nextAmpIndex].input = append(amps[nextAmpIndex].input, updatedAmp.output[len(updatedAmp.output)-1])

		ampIndex = nextAmpIndex
	}

	return amps[4].output[len(amps[4].output)-1]
}

func allAreComplete(amps []state) bool {
	for _, amp := range amps {
		if amp.readOpcode() != 99 {
			return false
		}
	}
	return true
}

func evaluatePhaseSettings(
	phaseSettings []int,
	program []int,
) int {
	value := 0
	for _, phaseSetting := range phaseSettings {
		value = evaluatePhaseSetting(value, phaseSetting, program)
	}
	return value
}

func evaluatePhaseSetting(
	input int,
	phaseSetting int,
	program []int,
) int {
	initial := state{
		program: sliceToMap(program),
		input:   []int{phaseSetting, input},
		pointer: 0,
	}
	final := run(initial)
	return final.output[0]
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
			input, err := takeInput(&state)
			if err != nil {
				return state
			}
			writePosition := state.program[pointer+1]
			state.program[writePosition] = input
			state.pointer = pointer + 2
		case 4:
			fmt.Println("Writing output")
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

func takeInput(s *state) (int, error) {
	if len(s.input) == 0 {
		return 0, errors.New("run out of inputs")
	}
	input, remainingInput := s.input[0], s.input[1:]
	s.input = remainingInput
	return input, nil
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
