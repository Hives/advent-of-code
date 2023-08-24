package main

import (
	"aoc"
	"reader"
)

func main() {
	input := reader.Strings("./input.txt")[0]
	aoc.CheckAnswer("Part 1", part1(input), 2975)
	aoc.CheckAnswer("Part 2", part2(input), "\nXXXX X  X XXX  X  X XXXX \nX    X  X X  X X  X X    \nXXX  XXXX X  X X  X XXX  \nX    X  X XXX  X  X X    \nX    X  X X X  X  X X    \nXXXX X  X X  X  XX  XXXX ")
}

func part1(input string) int {
	layers := getLayers(input)

	fewestZeros := 1_000_000
	ret := -1
	for _, layer := range layers {
		zeros := countChars(layer, "0")
		if zeros < fewestZeros {
			fewestZeros = zeros
			ret = countChars(layer, "1") * countChars(layer, "2")
		}
	}

	return ret
}

func part2(input string) string {
	layers := getLayers(input)

	width := 25
	height := 6
	imageSize := width * height

	image := ""
	for x := 0; x < imageSize; x++ {
		c := "2"
		y := 0
		for c == "2" {
			c = string(layers[y][x])
			y = y + 1
		}
		if c == "1" {
			image = image + "X"
		} else {
			image = image + " "
		}
	}

	lines := ""
	for y := 0; y < height; y++ {
		start := width * y
		end := width * (y + 1)
		newLine := image[start:end]
		lines = lines + "\n" + newLine
	}

	return lines
}

func getLayers(input string) []string {
	width := 25
	height := 6
	imageSize := width * height
	var layers []string

	n := 0
	for n < len(input) {
		layers = append(layers, input[n:n+imageSize])
		n = n + imageSize
	}

	return layers
}

func countChars(s string, expected string) int {
	count := 0
	for _, c := range s {
		if string(c) == expected {
			count = count + 1
		}
	}
	return count
}
