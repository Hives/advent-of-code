package main

import (
	"aoc"
	"strconv"
	"strings"
)

func main() {
	input := "359282-820401"
	aoc.CheckAnswer("Part 1", part1(input), 511)
	aoc.CheckAnswer("Part 2", part2(input), 316)
}

func part1(input string) int {
	low, high := parse(input)

	count := 0
	for password := low; password <= high; password++ {
		if test(password) {
			count = count + 1
		}
	}

	return count
}

func test(password int) bool {
	digits := digits(password)
	return neverDecreases(digits) && hasDouble(digits)
}

func part2(input string) int {
	low, high := parse(input)

	count := 0
	for password := low; password <= high; password++ {
		if test2(password) {
			count = count + 1
		}
	}

	return count
}

func test2(password int) bool {
	digits := digits(password)
	return neverDecreases(digits) && hasExactDouble(digits)
}

func neverDecreases(digits []int) bool {
	previous := 0
	for _, digit := range digits {
		if digit < previous {
			return false
		}
		previous = digit
	}
	return true
}

func hasDouble(digits []int) bool {
	previous := -1
	for _, digit := range digits {
		if digit == previous {
			return true
		}
		previous = digit
	}
	return false
}

func hasExactDouble(digits []int) bool {
	var chars []string
	for _, digit := range digits {
		chars = append(chars, strconv.Itoa(digit))
	}
	padded := append(append([]string{"X"}, chars...), "X")

	for i := 0; i < len(padded)-3; i++ {
		groupOf4 := padded[i : i+4]
		if groupOf4[1] == groupOf4[2] && groupOf4[0] != groupOf4[1] && groupOf4[3] != groupOf4[2] {
			return true
		}
	}

	return false
}

func digits(n int) []int {
	string := strconv.Itoa(n)
	split := strings.Split(string, "")
	var ret []int
	for _, char := range split {
		ret = append(ret, toInt(char))
	}
	return ret
}

func parse(input string) (int, int) {
	split := strings.Split(input, "-")
	return toInt(split[0]), toInt(split[1])
}

func toInt(s string) int {
	i, _ := strconv.Atoi(s)
	return i
}
