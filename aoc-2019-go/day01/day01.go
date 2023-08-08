package main

import (
	"fmt"
	"reader"
)

func main() {
	input := reader.Ints("./input.txt")
	fmt.Println(part1(input))
	fmt.Println(part2(input))
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
