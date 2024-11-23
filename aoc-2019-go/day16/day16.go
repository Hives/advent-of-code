package main

import (
	"aoc"
	"fmt"
	"reader"
	"strconv"
	"strings"
)

func main() {
	input := reader.Digits("./input.txt")

	//part1testInput1 := []int{8, 0, 8, 7, 1, 2, 2, 4, 5, 8, 5, 9, 1, 4, 5, 4, 6, 6, 1, 9, 0, 8, 3, 2, 1, 8, 6, 4, 5, 5, 9, 5}
	//aoc.CheckAnswer("Part 1 test", part1(part1testInput1), "24176176")
	//
	//part1testInput2 := []int{1, 9, 6, 1, 7, 8, 0, 4, 2, 0, 7, 2, 0, 2, 2, 0, 9, 1, 4, 4, 9, 1, 6, 0, 4, 4, 1, 8, 9, 9, 1, 7}
	//aoc.CheckAnswer("Part 1 test", part1(part1testInput2), "73745418")
	//
	//aoc.CheckAnswer("Part 1", part1(input), "63483758")

	//part2testInput1 := []int{0, 3, 0, 3, 6, 7, 3, 2, 5, 7, 7, 2, 1, 2, 9, 4, 4, 0, 6, 3, 4, 9, 1, 5, 6, 5, 4, 7, 4, 6, 6, 4}
	//aoc.CheckAnswer("Part 2 test", part2(part2testInput1), "84462026")

	aoc.CheckAnswer("Part 2", part2(input), "63483758")
}

func part1(input []int) string {
	return doIt(input, 1)
}

func part2(input []int) string {
	inputCopies := 10_000

	var realInput []int
	for i := 0; i < inputCopies; i++ {
		for _, v := range input {
			realInput = append(realInput, v)
		}
	}

	realInputLength := len(realInput)

	fmt.Printf("real input length: %v\n", realInputLength)

	for phase := 1; phase <= 100; phase++ {
		patternLength := phase * 4
		lcm := LCM(len(input), patternLength)
		repeats := realInputLength / lcm
		fmt.Printf("phase: %v, lcm: %v, repeats: %v, remainder: %v\n", phase, lcm, repeats, realInputLength-(repeats*lcm))

	}
	return ":("
}

func doIt(input []int, inputCopies int) string {
	basePattern := []int{0, 1, 0, -1}
	iterations := 100

	var realInput []int
	for i := 0; i < inputCopies; i++ {
		for _, v := range input {
			realInput = append(realInput, v)
		}
	}

	realInputLength := len(realInput)

	fmt.Printf("real input length: %v\n", realInputLength)

	allPlusOneIndices := make([][]int, realInputLength)
	allMinusOneIndices := make([][]int, realInputLength)
	for i := 0; i < realInputLength; i++ {
		if i%100 == 0 {
			println(i)
		}
		plusOneIndices, minusOneIndices := getImportantPatternIndices(basePattern, realInputLength, i)
		allPlusOneIndices[i] = plusOneIndices
		allMinusOneIndices[i] = minusOneIndices
	}

	fmt.Printf("allPlusOneIndices length: %v\n", len(allPlusOneIndices))

	current := realInput

	fmt.Println("starting")

	for phase := 0; phase < iterations; phase++ {
		fmt.Println(phase)
		var next []int
		for i := 0; i < len(current); i++ {
			pluses := 0
			for _, j := range allPlusOneIndices[i] {
				pluses += current[j]
			}

			minuses := 0
			for _, j := range allMinusOneIndices[i] {
				minuses += current[j]
			}

			next = append(next, abs((pluses-minuses)%10))
		}
		current = next
		//fmt.Println(current)
	}

	return stringify(current)
}

func getImportantPatternIndices(basePattern []int, max int, i int) ([]int, []int) {
	pattern := updateBasePattern(basePattern, i)
	var plusOneIndices []int
	var minusOneIndices []int
	for i := 0; i < max; i++ {
		if pattern[i%len(pattern)] == 1 {
			plusOneIndices = append(plusOneIndices, i)
		}
		if pattern[i%len(pattern)] == -1 {
			minusOneIndices = append(minusOneIndices, i)
		}
	}
	return plusOneIndices, minusOneIndices
}

func updateBasePattern(basePattern []int, position int) []int {
	var newPattern []int
	for i := 0; i < len(basePattern); i++ {
		digit := basePattern[i]
		for j := 0; j <= position; j++ {
			newPattern = append(newPattern, digit)
		}
	}
	return append(newPattern[1:], newPattern[0])
}

func stringify(ints []int) string {
	var s []string
	for _, i := range ints[0:8] {
		s = append(s, strconv.Itoa(i))
	}
	return strings.Join(s, "")
}

func abs(i int) int {
	if i > 0 {
		return i
	} else {
		return -i
	}
}

// greatest common divisor (GCD) via Euclidean algorithm
func GCD(a, b int) int {
	for b != 0 {
		t := b
		b = a % b
		a = t
	}
	return a
}

// find Least Common Multiple (LCM) via GCD
func LCM(a, b int, integers ...int) int {
	result := a * b / GCD(a, b)

	for i := 0; i < len(integers); i++ {
		result = LCM(result, integers[i])
	}

	return result
}
