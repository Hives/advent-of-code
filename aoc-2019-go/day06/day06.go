package main

import (
	"aoc"
	"reader"
	"strings"
)

func main() {
	input := reader.Strings("./input.txt")
	aoc.CheckAnswer("Part 1", part1(input), 117672)
	aoc.CheckAnswer("Part 2", part2(input), 277)
}

func part1(input []string) int {
	orbitMap := parse(input)
	bodies := map[string]struct{}{}

	bodies["COM"] = struct{}{}
	for satellite, _ := range orbitMap {
		bodies[satellite] = struct{}{}
	}

	total := 0
	for k, _ := range bodies {
		body := k
		for body != "COM" {
			total = total + 1
			body = orbitMap[body]
		}
	}

	return total
}

func part2(input []string) int {
	orbitMap := parse(input)
	youPath := getOrbitPath("YOU", orbitMap)
	sanPath := getOrbitPath("SAN", orbitMap)
	for youPath[len(youPath)-1] == sanPath[len(sanPath)-1] {
		youPath = youPath[:len(youPath)-1]
		sanPath = sanPath[:len(sanPath)-1]
	}
	return len(youPath) + len(sanPath)
}

func getOrbitPath(body string, orbitMap map[string]string) []string {
	var path []string
	for body != "COM" {
		next := orbitMap[body]
		path = append(path, next)
		body = next
	}
	return path
}

func parse(lines []string) map[string]string {
	ret := map[string]string{}
	for _, line := range lines {
		split := strings.Split(line, ")")
		ret[split[1]] = split[0]
	}
	return ret
}
