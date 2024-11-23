package main

import (
	"aoc"
	"fmt"
	"intcode"
	"reader"
)

func main() {
	input := reader.Program("./input.txt")
	//aoc.CheckAnswer("Part 1 example", test(), 76)
	aoc.CheckAnswer("Part 1", part1(input), 8408)
}

//func test() int {
//	scaffold := [][]string{
//		{".", ".", "#", ".", ".", ".", ".", ".", ".", ".", ".", ".", "."},
//		{".", ".", "#", ".", ".", ".", ".", ".", ".", ".", ".", ".", "."},
//		{"#", "#", "#", "#", "#", "#", "#", ".", ".", ".", "#", "#", "#"},
//		{"#", ".", "#", ".", ".", ".", "#", ".", ".", ".", "#", ".", "#"},
//		{"#", "#", "#", "#", "#", "#", "#", "#", "#", "#", "#", "#", "#"},
//		{".", ".", "#", ".", ".", ".", "#", ".", ".", ".", "#", ".", "."},
//		{".", ".", "#", "#", "#", "#", "#", ".", ".", ".", "^", ".", "."},
//	}
//	return getAlignmentParamsSum(scaffold)
//}

func part1(program []int) int {
	scaffold := getScaffold(program)
	return getAlignmentParamsSum(scaffold)
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
