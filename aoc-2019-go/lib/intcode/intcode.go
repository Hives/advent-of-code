package intcode

import (
	"errors"
	"fmt"
	"strconv"
)

func GetInitial(program []int, input []int, debug DebugLevel) State {
	return State{
		program:      sliceToMap(program),
		Input:        input,
		pointer:      0,
		relativeBase: 0,
		debugLevel:   debug,
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
		state.debug("----- start of run loop -----", Descriptions)
		state.debug(fmt.Sprintf("pos: %d, opcode: %d", state.pointer, state.current()), Positions)
		state.summarise()
		state = Step(state)
	}
	state.debug(state.Output, Positions)
	return state
}

func Step(state State) State {
	pointer := state.pointer

	state.debug("current: "+strconv.Itoa(state.current()), Everything)

	modes := state.parameterModes()

	mode1 := modes.get(0)
	state.debug("mode1: "+strconv.Itoa(mode1), Everything)
	param1 := state.getParam(pointer+1, mode1)
	state.debug("param1: "+strconv.Itoa(param1), Everything)

	mode2 := modes.get(1)
	state.debug("mode2: "+strconv.Itoa(mode2), Everything)
	param2 := state.getParam(pointer+2, mode2)
	state.debug("param2: "+strconv.Itoa(param2), Everything)

	// this is bullshit
	mode3 := modes.get(2)
	state.debug("mode3: "+strconv.Itoa(mode3), Everything)
	var param3 int
	if mode3 == 0 {
		param3 = state.program[pointer+3]
	} else if mode3 == 2 {
		param3 = state.program[pointer+3] + state.relativeBase
	} else {
		panic(fmt.Sprintf("Inappropriate write parameter mode: %d", mode3))
	}
	state.debug("param3: "+strconv.Itoa(param3), Everything)

	switch state.ReadOpcode() {
	case 1:
		// addition
		state.debug("Addition", Descriptions)
		state.debug(fmt.Sprintf("%d %d %d %d", state.program[pointer], state.program[pointer+1], state.program[pointer+2], state.program[pointer+3]), Descriptions)
		//target := State.program[pointer+3]
		target := param3
		state.debug(fmt.Sprintf("add %d and %d and write to %d", param1, param2, target), Descriptions)
		state.program[target] = param1 + param2
		state.pointer = pointer + 4
	case 2:
		// multiplication
		state.debug("Multiplication", Descriptions)
		state.debug(fmt.Sprintf("%d %d %d %d", state.program[pointer], state.program[pointer+1], state.program[pointer+2], state.program[pointer+3]), Descriptions)
		//target := State.program[pointer+3]
		target := param3
		state.debug(fmt.Sprintf("multiply %d and %d and write to %d", param1, param2, target), Descriptions)
		state.program[target] = param1 * param2
		state.pointer = pointer + 4
	case 3:
		// read Input
		state.debug("Read Input", Descriptions)
		state.debug(fmt.Sprintf("%d %d", state.program[pointer], state.program[pointer+1]), Descriptions)
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
			state.debug("No Input to read, returning", Quiet)
			return state
		}
		state.debug(fmt.Sprintf("read Input %d and write to %d", input, target), Descriptions)
		state.program[target] = input
		state.pointer = pointer + 2
	case 4:
		// write Output
		state.debug("Write Output", Descriptions)
		state.debug(fmt.Sprintf("%d %d", state.program[pointer], state.program[pointer+1]), Descriptions)
		state.debug(fmt.Sprintf("write %d to Output", param1), Descriptions)
		state.Output = append(state.Output, param1)
		state.pointer = pointer + 2
	case 5:
		// jump if true
		state.debug("Jump if true", Descriptions)
		state.debug(fmt.Sprintf("%d %d %d", state.program[pointer], state.program[pointer+1], state.program[pointer+2]), Descriptions)
		state.debug(fmt.Sprintf("if param1:%d != 0 jump to param2:%d", param1, param2), Descriptions)
		if param1 != 0 {
			state.pointer = param2
		} else {
			state.pointer = pointer + 3
		}
	case 6:
		// jump if false
		state.debug("Jump if false", Descriptions)
		state.debug(fmt.Sprintf("%d %d %d", state.program[pointer], state.program[pointer+1], state.program[pointer+2]), Descriptions)
		state.debug(fmt.Sprintf("if param1:%d == 0 jump to param2:%d", param1, param2), Descriptions)
		if param1 == 0 {
			state.pointer = param2
		} else {
			state.pointer = pointer + 3
		}
	case 7:
		// less than
		state.debug("Less than", Descriptions)
		state.debug(fmt.Sprintf("%d %d %d %d", state.program[pointer], state.program[pointer+1], state.program[pointer+2], state.program[pointer+3]), Descriptions)
		//target := State.program[pointer+3]
		target := param3
		state.debug(fmt.Sprintf("if %d < %d write 1 to %d, else write 0", param1, param2, target), Descriptions)
		if param1 < param2 {
			state.program[target] = 1
		} else {
			state.program[target] = 0
		}
		state.pointer = pointer + 4
	case 8:
		// equals
		state.debug("Equals", Descriptions)
		state.debug(fmt.Sprintf("%d %d %d %d", state.program[pointer], state.program[pointer+1], state.program[pointer+2], state.program[pointer+3]), Descriptions)
		//target := State.program[pointer+3]
		target := param3
		state.debug(fmt.Sprintf("if %d == %d write 1 to %d, else write 0", param1, param2, target), Descriptions)
		if param1 == param2 {
			state.program[target] = 1
		} else {
			state.program[target] = 0
		}
		state.pointer = pointer + 4
	case 9:
		// adjust relative base
		state.debug("Adjust relative base", Descriptions)
		state.debug(fmt.Sprintf("%d %d", state.program[pointer], state.program[pointer+1]), Descriptions)
		state.debug(fmt.Sprintf("relative base was %d, add %d to get %d", state.relativeBase, param1, state.relativeBase+param1), Descriptions)
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
	debugLevel   DebugLevel
}

type DebugLevel int64

const (
	Quiet        DebugLevel = 0
	Positions    DebugLevel = 1
	Descriptions DebugLevel = 2
	Everything   DebugLevel = 3
)

func (s State) current() int {
	return s.program[s.pointer]
}

func (s State) ReadOpcode() int {
	return s.current() % 100
}

func (s State) debug(input any, level DebugLevel) {
	if s.debugLevel >= level {
		fmt.Println(input)
	}
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

func (s State) summarise() {
	if s.debugLevel < Everything {
		return
	}
	fmt.Println("------------------")
	fmt.Println("program: ", s.program)
	fmt.Println("pointer: ", s.pointer)
	fmt.Println("relative base: ", s.relativeBase)
	fmt.Println("Input: ", s.Input)
	fmt.Println("Output: ", s.Output)
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
