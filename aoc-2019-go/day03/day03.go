package main

import (
	"fmt"
	"reader"
	"strconv"
	"strings"
)

func main() {
	input := reader.Strings("./input.txt")
	fmt.Println(part1(input))
	fmt.Println(part2(input))
}

func part1(input []string) int {
	points1 := getPoints(input[0])
	points2 := getPoints(input[1])
	closest := findClosestIntersection1(points1, points2)
	return closest
}

func part2(input []string) int {
	points1 := getPoints(input[0])
	points2 := getPoints(input[1])
	closest := findClosestIntersection2(points1, points2)
	return closest
}

func getPoints(wire string) []pointAndDist {
	segments := parseWire(wire)
	current := point{0, 0}
	distance := 0
	var points []pointAndDist
	for _, segment := range segments {
		var direction point
		switch segment.dir {
		case "L":
			direction = point{-1, 0}
		case "R":
			direction = point{1, 0}
		case "U":
			direction = point{0, -1}
		case "D":
			direction = point{0, 1}
		default:
			panic(fmt.Sprintf("Unknown direction: %v", segment.dir))
		}
		for i := 0; i < segment.dist; i++ {
			distance = distance + 1
			newPoint := point{current.x + direction.x, current.y + direction.y}
			points = append(points, pointAndDist{newPoint, distance})
			current = newPoint
		}
	}
	return points
}

func parseWire(wire string) []segment {
	split := strings.Split(wire, ",")
	var segments []segment
	for _, s := range split {
		dir := string(s[0])
		dist, _ := strconv.Atoi(s[1:])
		segments = append(segments, segment{dir, dist})
	}
	return segments
}

func findClosestIntersection1(points1 []pointAndDist, points2 []pointAndDist) int {
	points1Map := make(map[point]struct{})
	for _, point := range points1 {
		points1Map[point.p] = struct{}{}
	}

	closest := 1000000000

	for _, point := range points2 {
		_, found := points1Map[point.p]
		if found {
			distance := manhattan(point.p)
			if distance < closest {
				closest = distance
			}
		}
	}

	return closest
}

func findClosestIntersection2(points1 []pointAndDist, points2 []pointAndDist) int {
	points1Map := make(map[point]int)
	for _, point := range points1 {
		_, found := points1Map[point.p]
		if found {
			if point.dist < points1Map[point.p] {
				points1Map[point.p] = point.dist
			}
		} else {
			points1Map[point.p] = point.dist
		}
	}

	closest := 1000000000

	for _, point := range points2 {
		wire1Distance, found := points1Map[point.p]
		if found {
			distance := wire1Distance + point.dist
			if distance < closest {
				closest = distance
			}
		}
	}

	return closest
}

func manhattan(p point) int {
	return abs(p.x) + abs(p.y)
}

func abs(n int) int {
	if n < 0 {
		return -n
	}
	return n
}

type segment struct {
	dir  string
	dist int
}

type point struct {
	x int
	y int
}

type pointAndDist struct {
	p    point
	dist int
}
