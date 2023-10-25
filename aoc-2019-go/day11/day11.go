package main

import (
	"aoc"
	"fmt"
	"intcode"
	"reader"
)

func main() {
	input := reader.Program("./input.txt")
	aoc.CheckAnswer("Part 1", part1(input), 2054)
	aoc.CheckAnswer("Part 2", part2(input), "asd")
}

func part1(input []int) int {
	panelsPainted, _ := runRobot(input, 0)
	return panelsPainted
}

func part2(input []int) int {
	_, white := runRobot(input, 1)
	fmt.Println("white: " + fmt.Sprint(white))
	return -1
}

func runRobot(input []int, initial int) (int, map[point]struct{}) {
	painted := map[point]struct{}{}
	white := map[point]struct{}{}
	state := intcode.GetInitial(input, []int{}, intcode.Quiet)
	position := point{0, 0}
	if initial == 1 {
		// white
		white[position] = struct{}{}
	}
	facing := U

	for state.ReadOpcode() != 99 {
		if len(state.Output) == 2 {
			colour, direction := state.Output[0], state.Output[1]
			state.Output = []int{}

			painted[position] = struct{}{}

			if colour == 1 {
				// white
				white[position] = struct{}{}
			} else if colour == 0 {
				// black
				delete(white, position)
			} else {
				panic(fmt.Sprintf("Unknown colour: %d", colour))
			}

			if direction == 0 {
				// turn left
				switch facing {
				case U:
					facing = L
				case L:
					facing = D
				case D:
					facing = R
				case R:
					facing = U
				}
			} else if direction == 1 {
				// turn right
				switch facing {
				case U:
					facing = R
				case R:
					facing = D
				case D:
					facing = L
				case L:
					facing = U
				}
			} else {
				panic(fmt.Sprintf("Unknown direction: %d", direction))
			}

			switch facing {
			case U:
				position = point{position.x, position.y - 1}
			case R:
				position = point{position.x + 1, position.y}
			case D:
				position = point{position.x, position.y + 1}
			case L:
				position = point{position.x - 1, position.y}
			}
		}

		if state.ReadOpcode() == 3 {
			_, positionIsWhite := white[position]
			if positionIsWhite {
				state.Input = append(state.Input, 1)
			} else {
				state.Input = append(state.Input, 0)
			}
		}

		state = intcode.Step(state)
	}

	return len(painted), white
}

type direction int

const (
	U direction = iota
	R
	D
	L
)

type point struct {
	x int
	y int
}

// --- V intcode computer V ---
