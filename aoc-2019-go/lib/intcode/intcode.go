package intcode

import (
	"errors"
	"fmt"
)

func GetInitial(program []int, input []int) State {
	return State{
		program:      sliceToMap(program),
		Input:        input,
		pointer:      0,
		relativeBase: 0,
	}
}

func sliceToMap(ns []int) map[int]int {
	var ret = make(map[int]int)
	for i, n := range ns {
		ret[i] = n
	}
	return ret
}

func Run(state State) State {
	for state.ReadOpcode() != 99 {
		state = Step(state)
	}
	return state
}

func Step(state State) State {
	pointer := state.pointer

	modes := state.parameterModes()

	mode1 := modes.get(0)
	param1 := state.getParam(pointer+1, mode1)

	mode2 := modes.get(1)
	param2 := state.getParam(pointer+2, mode2)

	// this is bullshit
	mode3 := modes.get(2)
	var param3 int
	if mode3 == 0 {
		param3 = state.program[pointer+3]
	} else if mode3 == 2 {
		param3 = state.program[pointer+3] + state.relativeBase
	} else {
		panic(fmt.Sprintf("Inappropriate write parameter mode: %d", mode3))
	}

	switch state.ReadOpcode() {
	case 1:
		// addition
		target := param3
		state.program[target] = param1 + param2
		state.pointer = pointer + 4
	case 2:
		// multiplication
		target := param3
		state.program[target] = param1 * param2
		state.pointer = pointer + 4
	case 3:
		// read Input
		var target int
		if mode1 == 0 {
			target = state.program[pointer+1]
		} else if mode1 == 2 {
			target = state.program[pointer+1] + state.relativeBase
		} else {
			panic(fmt.Sprintf("Inappropriate read parameter mode: %d", mode1))
		}
		input, err := takeInput(&state)
		if err != nil {
			return state
		}
		state.program[target] = input
		state.pointer = pointer + 2
	case 4:
		// write Output
		state.Output = append(state.Output, param1)
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
		//target := State.program[pointer+3]
		target := param3
		if param1 < param2 {
			state.program[target] = 1
		} else {
			state.program[target] = 0
		}
		state.pointer = pointer + 4
	case 8:
		// equals
		//target := State.program[pointer+3]
		target := param3
		if param1 == param2 {
			state.program[target] = 1
		} else {
			state.program[target] = 0
		}
		state.pointer = pointer + 4
	case 9:
		// adjust relative base
		state.relativeBase = state.relativeBase + param1
		state.pointer = state.pointer + 2
	default:
		panic(fmt.Sprintf("Unknown opcode: %v", state.ReadOpcode()))
	}

	return state
}

type State struct {
	program map[int]int
	Input   []int
	Output  []int
	pointer int
	relativeBase int
}

func (s State) current() int {
	return s.program[s.pointer]
}

func (s State) ReadOpcode() int {
	return s.current() % 100
}

func (s State) parameterModes() defaultedList {
	modesDigits := s.current() / 100
	var modes []int
	for modesDigits > 0 {
		digit := modesDigits % 10
		modes = append(modes, digit)
		modesDigits = modesDigits / 10
	}
	return defaultedList{ns: modes, fallback: 0}
}

func (s State) getParam(i int, mode int) int {
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
	case 2:
		// relative mode
		position := s.program[i] + s.relativeBase
		value := s.program[position]
		return value
	default:
		panic(fmt.Sprintf("Unknown parameter mode: %v", mode))
	}
}

func takeInput(s *State) (int, error) {
	if len(s.Input) == 0 {
		return 0, errors.New("run out of inputs")
	}
	input, remainingInput := s.Input[0], s.Input[1:]
	s.Input = remainingInput
	return input, nil
}

func (s State) writeOutput(value int) {
	s.Output = append(s.Output, value)
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
