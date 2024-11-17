package main

import (
	"aoc"
	"reader"
	"strconv"
	"strings"
)

func main() {
	input := reader.Digits("./input.txt")
	//input := []int{1, 2, 3, 4, 5, 6, 7, 8}
	//input := []int{8, 0, 8, 7, 1, 2, 2, 4, 5, 8, 5, 9, 1, 4, 5, 4, 6, 6, 1, 9, 0, 8, 3, 2, 1, 8, 6, 4, 5, 5, 9, 5}
	aoc.CheckAnswer("Part 1", part1(input), "63483758")
}

func part1(input []int) string {
	basePattern := []int{0, 1, 0, -1}

	current := input

	for i := 0; i < 100; i++ {
		var next []int
		for i := 0; i < len(current); i++ {
			pattern := updateBasePattern(basePattern, i)

			total := 0
			for j, v := range current {
				total += v * pattern[j%len(pattern)]
			}
			next = append(next, abs(total%10))
		}
		current = next
	}

	return stringify(current)
}

func updateBasePattern(basePattern []int, position int) []int {
	var newPattern []int
	for i := 0; i < len(basePattern); i++ {
		digit := basePattern[i]
		for j := 0; j <= position; j++ {
			newPattern = append(newPattern, digit)
		}
	}
	return append(newPattern[1:len(newPattern)], newPattern[0])
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
