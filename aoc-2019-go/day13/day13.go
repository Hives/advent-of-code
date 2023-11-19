package main

import (
	"aoc"
	"intcode"
	"reader"
)

func main() {
	input := reader.Program("./input.txt")
  aoc.CheckAnswer("Part 1", part1(input), 309)
}

func part1(progam []int) int {
  state := intcode.GetInitial(progam, []int{})
  final := intcode.Run(state)
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

type tile struct {
  x int
  y int
  tileId int
}
