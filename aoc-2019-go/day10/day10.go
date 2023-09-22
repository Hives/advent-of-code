package main

import (
	"aoc"
	"reader"
	"strings"
)

func main() {
	input := reader.Strings("./input.txt")
	aoc.CheckAnswer("Part 1", part1(input), 326)
}

func part1(input []string) int {
	asteroids, max := parse(input)
	return findBestLocation(asteroids, max)
}

func findBestLocation(asteroids Set[point], max point) int {
	most := 0
	for station := range asteroids {
		visible := countVisibleAsteroids(station, asteroids, max)
		if visible > most {
			most = visible
		}
	}
	return most
}

func countVisibleAsteroids(station point, asteroids Set[point], max point) int {
	blocked := Set[point]{}
	blocked[station] = struct{}{}
	for asteroid := range asteroids {
		diff := asteroid.minus(station)
		if (diff != point{0, 0}) {
			shadowLength := shrink(diff)
			shadow := asteroid
			for true {
				shadow = shadow.plus(shadowLength)
				if shadow.x < 0 || shadow.x >= max.x || shadow.y < 0 || shadow.y >= max.y {
					break
				}
				blocked[shadow] = struct{}{}
			}
		}
	}

	visible := asteroids.minus(blocked)

	return len(visible)
}

func shrink(p point) point {
	absP := point{abs(p.x), abs(p.y)}
	for i := 2; i <= max(absP.x, absP.y); i++ {
		if absP.x%i == 0 && absP.y%i == 0 {
			return shrink(point{p.x / i, p.y / i})
		}
	}
	return p
}

func max(a, b int) int {
	if a > b {
		return a
	}
	return b
}

func abs(a int) int {
	if a < 0 {
		return -a
	}
	return a
}

func parse(input []string) (Set[point], point) {
	maxX := len(input[0])
	maxY := len(input)
	asteroids := Set[point]{}
	for y := 0; y < maxY; y++ {
		row := strings.Split(input[y], "")
		for x := 0; x < maxX; x++ {
			if row[x] == "#" {
				asteroids[point{x, y}] = struct{}{}
			}
		}
	}
	return asteroids, point{maxX, maxY}
}

type point struct {
	x int
	y int
}

func (set Set[T]) minus(other Set[T]) Set[T] {
	output := Set[T]{}
	for item := range set {
		if !other.contains(item) {
			output[item] = struct{}{}
		}
	}
	return output
}

func (set Set[T]) contains(p T) bool {
	_, ok := set[p]
	return ok
}

func (it point) minus(other point) point {
	return point{it.x - other.x, it.y - other.y}
}

func (it point) plus(other point) point {
	return point{it.x + other.x, it.y + other.y}
}

type Set[T comparable] map[T]struct{}
