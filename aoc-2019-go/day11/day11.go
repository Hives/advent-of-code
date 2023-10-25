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
	state := intcode.GetInitial(input, []int{})
	position := point{0, 0}
	if initial == 1 {
		// white
		white[position] = struct{}{}
	}
	facing := 0

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
				facing = (facing + 1) % 4
			} else {
				// turn right
				facing = (4 + facing - 1) % 4
			}

			switch facing {
			case 0:
				position.y -= 1
			case 1:
				position.x -= 1
			case 2:
				position.y += 1
			case 3:
				position.x += 1
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

type point struct {
	x int
	y int
}
