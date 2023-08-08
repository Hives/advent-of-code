package main

import (
	"fmt"
	"reader"
	"strconv"
	"strings"
)

func main() {
	//example1 := reader.Strings("./example1.txt")
	input := reader.Strings("./input.txt")
	fmt.Println(part1(input))
}

func part1(input []string) int {
	points1 := getPoints(input[0])
	points2 := getPoints(input[1])
	intersections := intersection(points1, points2)
	closest := intersections[0]
	for _, p := range intersections {
		if manhattan(p) < manhattan(closest) {
			closest = p
		}
	}
	return manhattan(closest)
}

func getPoints(wire string) []point {
	segments := parseWire(wire)
	current := point{0, 0}
	var points []point
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
			newPoint := point{current.x + direction.x, current.y + direction.y}
			points = append(points, newPoint)
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

func intersection(points1 []point, points2 []point) []point {
	points1Map := make(map[point]struct{})
	for _, point := range points1 {
		points1Map[point] = struct{}{}
	}
	var commonPoints []point
	for _, point := range points2 {
		_, found := points1Map[point]
		if found {
			commonPoints = append(commonPoints, point)
		}
	}
	return commonPoints
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
