package main

import (
	"aoc"
	"reader"
)

func main() {
	input := reader.Ints("./input.txt")
	aoc.CheckAnswer("Part 1", part1(input), 3406432)
	aoc.CheckAnswer("Part 2", part2(input), 5106777)
}

func part1(masses []int) int {
	sum := 0
	for _, mass := range masses {
		sum = sum + getFuel(mass)
	}
	return sum
}

func part2(masses []int) int {
	sum := 0
	for _, mass := range masses {
		sum = sum + getFuel2(mass)
	}
	return sum
}

func getFuel(mass int) int {
	return mass/3 - 2
}

func getFuel2(mass int) int {
	fuel := getFuel(mass)
	if fuel > 0 {
		return fuel + getFuel2(fuel)
	} else {
		return 0
	}
}
