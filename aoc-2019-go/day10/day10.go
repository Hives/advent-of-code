package main

import (
	"aoc"
	"math"
	"reader"
	"sort"
	"strings"
)

func main() {
	input := reader.Strings("./input.txt")
	aoc.CheckAnswer("Part 1", part1(input), 326)

	//aoc.CheckAnswer("up", point{0, -1}.angle(), 0.0)
	//aoc.CheckAnswer("up-right", point{1, -1}.angle(), math.Pi/4)
	//aoc.CheckAnswer("right", point{1, 0}.angle(), math.Pi/2)
	//aoc.CheckAnswer("down-right", point{1, 1}.angle(), 3*math.Pi/4)
	//aoc.CheckAnswer("down", point{0, 1}.angle(), math.Pi)
	//aoc.CheckAnswer("down-left", point{-1, 1}.angle(), 5*math.Pi/4)
	//aoc.CheckAnswer("left", point{-1, 0}.angle(), 3*math.Pi/2)
	//aoc.CheckAnswer("up-left", point{-1, -1}.angle(), 7*math.Pi/4)

	aoc.CheckAnswer("Part 2", part2(input), 1623)
}

func part1(input []string) int {
	asteroids, max := parse(input)
	most, _ := findBestLocation(asteroids, max)
	return most
}

func part2(input []string) int {
	asteroids, max := parse(input)

	_, station := findBestLocation(asteroids, max)

	visibleAsteroids := slice(getVisibleAsteroids(station, asteroids, max))

	var normalised []point
	for _, asteroid := range visibleAsteroids {
		normalised = append(normalised, asteroid.minus(station))
	}

	sort.Sort(ByAngle(normalised))

	var denormalised []point
	for _, asteroid := range normalised {
		denormalised = append(denormalised, asteroid.plus(station))
	}

	twoHundredth := denormalised[199]

	return 100*twoHundredth.x + twoHundredth.y
}

func findBestLocation(asteroids Set[point], max point) (int, point) {
	most := 0
	var best point
	for station := range asteroids {
		visible := countVisibleAsteroids(station, asteroids, max)
		if visible > most {
			most = visible
			best = station
		}
	}
	return most, best
}

func countVisibleAsteroids(station point, asteroids Set[point], max point) int {
	return len(getVisibleAsteroids(station, asteroids, max))
}

func getVisibleAsteroids(station point, asteroids Set[point], max point) Set[point] {
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

	return asteroids.minus(blocked)
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

type Set[T comparable] map[T]struct{}

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

func slice[T comparable](set Set[T]) []T {
	var keys []T
	for item := range set {
		keys = append(keys, item)
	}
	return keys
}

type point struct {
	x int
	y int
}

func (it point) minus(other point) point {
	return point{it.x - other.x, it.y - other.y}
}

func (it point) plus(other point) point {
	return point{it.x + other.x, it.y + other.y}
}

func (it point) angle() float64 {
	if it.y <= 0 && it.x >= 0 {
		// top right
		return math.Atan(float64(it.x) / float64(-it.y))
	}
	if it.y >= 0 && it.x >= 0 {
		// bottom right
		return math.Pi + math.Atan(float64(it.x)/float64(-it.y))
	}
	if it.y > 0 && it.x < 0 {
		// bottom left
		return math.Pi - math.Atan(float64(it.x)/float64(it.y))
	}
	// top right
	return 2*math.Pi + math.Atan(float64(it.x)/float64(-it.y))
}

type ByAngle []point

func (points ByAngle) Len() int           { return len(points) }
func (points ByAngle) Less(i, j int) bool { return points[i].angle() < points[j].angle() }
func (points ByAngle) Swap(i, j int)      { points[i], points[j] = points[j], points[i] }
