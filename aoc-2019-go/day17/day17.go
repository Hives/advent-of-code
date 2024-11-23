package main

import (
	"aoc"
	"fmt"
	"intcode"
	"reader"
	"strconv"
)

func main() {
	input := reader.Program("./input.txt")
	aoc.CheckAnswer("Part 1", part1(input), 8408)
	aoc.CheckAnswer("Part 2", part2(input), 1168948)
}

func part1(program []int) int {
	scaffold := getScaffold(program)
	return getAlignmentParamsSum(scaffold)
}

func part2(program []int) int {
	wokenUpProgram := []int{2}
	wokenUpProgram = append(wokenUpProgram, program[1:len(program)-1]...)

	var input []int
	input = append(input, toAsciiInput("A,B,A,C,B,C,A,B,A,C")...)
	input = append(input, toAsciiInput("R,6,L,10,R,8,R,8")...)
	input = append(input, toAsciiInput("R,12,L,8,L,10")...)
	input = append(input, toAsciiInput("R,12,L,10,R,6,L,10")...)
	input = append(input, toAsciiInput("n,10")...)

	initial := intcode.GetInitial(wokenUpProgram, input)
	final := intcode.Run(initial)

	return final.Output[len(final.Output)-1]
}

func toAsciiInput(s string) []int {
	bytes := []byte(s)
	var output []int
	for _, b := range bytes {
		atoi, err := strconv.Atoi(fmt.Sprint(b))
		if err != nil {
			panic(err)
		}
		output = append(output, atoi)
	}
	output = append(output, 10)
	return output
}

func getAlignmentParamsSum(scaffold [][]string) int {
	alignmentParamsTotal := 0
	for y, row := range scaffold {
		for x, cell := range row {
			if cell == "#" {
				neighbours := getNeighbours(x, y, scaffold)
				if neighbours[0] == "#" && neighbours[1] == "#" && neighbours[2] == "#" && neighbours[3] == "#" {
					alignmentParamsTotal += x * y
				}
			}
		}
	}

	return alignmentParamsTotal
}

func getNeighbours(x int, y int, scaffold [][]string) []string {
	return []string{
		getCell(y+1, x, scaffold),
		getCell(y-1, x, scaffold),
		getCell(y, x+1, scaffold),
		getCell(y, x-1, scaffold),
	}
}

func getCell(y int, x int, scaffold [][]string) string {
	if y < 0 || y >= len(scaffold) || x < 0 || x >= len(scaffold[0]) {
		return "."
	}
	return scaffold[y][x]
}

func getScaffold(program []int) [][]string {
	initial := intcode.GetInitial(program, []int{})
	output := intcode.Run(initial).Output

	scaffold := [][]string{{}}
	for _, n := range output {
		switch n {
		case 35:
			scaffold[len(scaffold)-1] = append(scaffold[len(scaffold)-1], "#")
			break
		case 46:
			scaffold[len(scaffold)-1] = append(scaffold[len(scaffold)-1], ".")
			break
		case 94:
			scaffold[len(scaffold)-1] = append(scaffold[len(scaffold)-1], "^")
			break
		case 10:
			scaffold = append(scaffold, nil)
			break
		default:
			panic(fmt.Sprintf("Unknown code: %v\n", n))
		}
	}

	return scaffold[:len(scaffold)-2]
}

func printy(scaffold [][]string) {
	for _, row := range scaffold {
		fmt.Println(row)
	}
}
