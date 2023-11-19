package main

import (
	"aoc"
	"intcode"
	"reader"
)

func main() {
	input := reader.Program("./input.txt")
	aoc.CheckAnswer("Part 1", part1(input), 309)
	aoc.CheckAnswer("Part 2", part2(input), 15410)
}

func part1(progam []int) int {
	initial := intcode.GetInitial(progam, []int{})
	final := intcode.Run(initial)
	blocks := 0
	n := 2
	for true {
		if final.Output[n] == 2 {
			blocks += 1
		}
		n += 3
		if n > len(final.Output) {
			break
		}
	}
	return blocks
}

func part2(program []int) int {
	program[0] = 2
	state := intcode.GetInitial(program, []int{})

	var ball tile
	var paddle tile
	var score tile

	for state.ReadOpcode() != 99 {
		state = intcode.Step(state)

		if state.ReadOpcode() == 3 {
			if ball.x > paddle.x {
				state.Input = append(state.Input, 1)
			} else if ball.x < paddle.x {
				state.Input = append(state.Input, -1)
			} else {
				state.Input = append(state.Input, 0)
			}
		}

		if len(state.Output) == 3 {
			tile := tile{state.Output[0], state.Output[1], state.Output[2]}

			if tile.tileId == 3 {
				paddle = tile
			}

			if tile.tileId == 4 {
				ball = tile
			}

			if tile.x == -1 {
				score = tile
			}

			state.Output = []int{}
		}

	}

	return score.tileId
}

type tile struct {
	x      int
	y      int
	tileId int
}
