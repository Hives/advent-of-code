package main

import (
	"aoc"
	"fmt"
	"golang.org/x/exp/slices"
	"math"
	"reader"
	"strings"
)

func main() {
	//example1 := reader.Grid("./example1.txt")
	//aoc.CheckAnswer("Example 1", part1(example1), 8)

	//example2 := reader.Grid("./example2.txt")
	//aoc.CheckAnswer("Example 2", part1(example2), 86)

	//example3 := reader.Grid("./example3.txt")
	//aoc.CheckAnswer("Example 3", part1(example3), 132)

	//example4 := reader.Grid("./example4.txt")
	//aoc.CheckAnswer("Example 4", part1(example4), 136)

	//example5 := reader.Grid("./example5.txt")
	//aoc.CheckAnswer("Example 5", part1(example5), 81)

	input := reader.Grid("./input.txt")
	aoc.CheckAnswer("Part 1", part1(input), 5450)
}

func part1(grid [][]string) int {
	allKeys := getAllKeys(grid)
	edges := getEdges(grid)
	//for id, m := range edges {
	//	fmt.Printf("%v, %v\n", id, m)
	//}
	start := node{
		id:   "@",
		keys: "",
	}

	distances := make(map[node]int)
	distances[start] = 0
	queue := make(map[node]bool)
	queue[start] = true
	visited := make(map[node]bool)

	for {
		//fmt.Println("-----")
		//fmt.Printf("distances: %v\n", distances)
		//fmt.Printf("queue: %v\n", queue)
		currentNode := getNextCurrentNode(queue, visited, distances)
		delete(queue, currentNode)
		visited[currentNode] = true
		//fmt.Printf("currentNode: %v\n", currentNode)

		adjacentEdges := edges[currentNode.id]
		//fmt.Printf("adjacentEdges: %v\n", adjacentEdges)
		for id, distance := range adjacentEdges {
			//fmt.Printf("id: %v\n", id)
			isVisitable := id == "@" || isKey(id) || (isDoor(id) && strings.Contains(currentNode.keys, strings.ToLower(id)))
			if isVisitable {
				//fmt.Println("is visitable")
				newKeys := currentNode.keys
				if isKey(id) {
					newKeys = addKeyToKeys(newKeys, id)
				}
				newNode := node{id: id, keys: newKeys}
				newDistance := distances[currentNode] + distance
				oldDistance, ok := distances[newNode]
				if !ok || newDistance < oldDistance {
					distances[newNode] = newDistance
				}
				queue[newNode] = true
				if newNode.keys == allKeys {
					return distances[newNode]
				}
			} else {
				//fmt.Println("is not visitable")
			}
		}
	}
}

func part2(grid [][]string) int {

}

func getEdges(grid [][]string) map[string]map[string]int {
	pointsOfInterest := getPointsOfInterest(grid)
	var edges = map[string]map[string]int{}
	for char, point := range pointsOfInterest {
		edges[char] = getNeighbourEdges(point, grid)
	}
	return edges
}

func getPointsOfInterest(grid [][]string) map[string]point {
	pointsOfInterest := make(map[string]point)
	for y, row := range grid {
		for x, char := range row {
			if char != "." && char != "#" {
				pointsOfInterest[char] = point{x, y}
			}
		}
	}
	return pointsOfInterest
}

func getNeighbourEdges(start point, grid [][]string) map[string]int {
	frontier := make(map[point]bool)
	frontier[start] = true
	visited := make(map[point]bool)
	visited[start] = true
	edges := make(map[string]int)
	distance := 0

	for len(frontier) > 0 {
		newFrontier := make(map[point]bool)
		for point, _ := range frontier {
			neighbours := getNeighbours(point, grid)
			for _, neighbour := range neighbours {
				_, alreadyVisited := visited[neighbour]
				if !alreadyVisited {
					newFrontier[neighbour] = true
					visited[neighbour] = true
				}
			}
		}

		distance += 1

		for point, _ := range newFrontier {
			char := grid[point.y][point.x]
			if char != "." && char != "#" {
				edges[char] = distance
				delete(newFrontier, point)
			}
		}

		frontier = newFrontier
	}

	return edges
}

func getAllKeys(grid [][]string) string {
	var keys []string
	for _, row := range grid {
		for _, char := range row {
			if char != "." && char != "#" && char != "@" && isLowercase(char) {
				keys = append(keys, char)
			}
		}
	}
	slices.Sort(keys)
	return strings.Join(keys, "")
}

func addKeyToKeys(keys string, newKey string) string {
	if strings.Contains(keys, newKey) {
		return keys
	}
	newKeys := strings.Split(keys, "")
	newKeys = append(newKeys, newKey)
	slices.Sort(newKeys)
	return strings.Join(newKeys, "")
}

func getNextCurrentNode(queue map[node]bool, visited map[node]bool, distances map[node]int) node {
	var nextCurrentNode node
	nextCurrentNodeDistance := math.MaxInt32
	for node, _ := range queue {
		_, alreadyVisited := visited[node]
		if !alreadyVisited {
			if distances[node] < nextCurrentNodeDistance {
				nextCurrentNodeDistance = distances[node]
				nextCurrentNode = node
			}
		}
	}
	return nextCurrentNode
}

func isKey(s string) bool {
	return s != "@" && isLowercase(s)
}

func isDoor(s string) bool {
	return s != "@" && !isLowercase(s)
}

func isLowercase(s string) bool {
	if strings.ToLower(s) == s {
		return true
	}
	return false
}

func getNeighbours(initial point, grid [][]string) (ret []point) {
	var compassPoints = []point{
		{initial.x - 1, initial.y},
		{initial.x + 1, initial.y},
		{initial.x, initial.y + 1},
		{initial.x, initial.y - 1},
	}
	for _, p := range compassPoints {
		if grid[p.y][p.x] != "#" {
			ret = append(ret, p)
		}
	}
	return ret
}

func printy(grid [][]string) {
	for _, row := range grid {
		fmt.Println(strings.Join(row, ""))
	}
}

type node struct {
	id   string
	keys string
}

type point struct {
	x int
	y int
}
