package reader

import (
	"os"
	"strconv"
	"strings"
)

func String(path string) string {
	return string(read(path))
}

func Strings(path string) (ret []string) {
	ss := strings.Split(String(path), "\n")
	for _, s := range ss {
		if s != "" {
			ret = append(ret, s)
		}
	}
	return ret
}

func Grid(path string) (ret [][]string) {
	ss := Strings(path)
	grid := [][]string{{}}
	for _, row := range ss {
		rowChars := strings.Split(row, "")
		for _, char := range rowChars {
			grid[len(grid)-1] = append(grid[len(grid)-1], char)
		}
		grid = append(grid, nil)
	}
	return grid
}

func Ints(path string) []int {
	input := Strings(path)
	var ints []int
	for _, s := range input {
		n, err := strconv.Atoi(s)
		check(err)
		ints = append(ints, n)
	}
	return ints
}

func Digits(path string) []int {
	input := String(path)
	var ints []int
	for _, s := range strings.Split(input, "") {
		n, err := strconv.Atoi(s)
		if err == nil {
			ints = append(ints, n)
		}
	}
	return ints
}

func Program(path string) []int {
	input := Strings(path)
	var ints []int
	for _, s := range strings.Split(input[0], ",") {
		n, err := strconv.Atoi(s)
		check(err)
		ints = append(ints, n)
	}
	return ints
}

func read(path string) []byte {
	input, err := os.ReadFile(path)
	check(err)
	return input
}

func check(e error) {
	if e != nil {
		panic(e)
	}
}
