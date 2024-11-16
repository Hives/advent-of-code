package main

import (
	"aoc"
	"fmt"
	"intcode"
	"reader"
)

func main() {
	input := reader.Program("./input.txt")
	aoc.CheckAnswer("Part 1", part1(input), 246)
	aoc.CheckAnswer("Part 2", part2(input), 376)
}

func part1(program []int) int {
	state := intcode.GetInitial(program, []int{})

	location := point{0, 0}

	walls := map[point]bool{}
	spaces := map[point]bool{}
	spaces[location] = true

	var movesThatMoved []Direction

	for {
		//fmt.Println("------")
		//fmt.Printf("location: %v\n", location)

		isBacktracking := false

		//fmt.Println(movesThatMoved)
		direction := chooseDirection(location, walls, spaces)
		//fmt.Printf("selected direction: %v\n", direction)
		if direction == -1 {
			//fmt.Println("backtracking")
			isBacktracking = true
			lastMove := movesThatMoved[len(movesThatMoved)-1]
			movesThatMoved = movesThatMoved[:len(movesThatMoved)-1]
			direction = invert(lastMove)
		}
		//fmt.Println(movesThatMoved)
		//fmt.Printf("direction: %v\n", direction)

		state.Input = append(state.Input, int(direction))
		for state.ReadOpcode() != 4 /* write output */ {
			state = intcode.Step(state)
		}

		state = intcode.Step(state)

		output := state.Output[len(state.Output)-1]
		switch output {
		case 0:
			// hit wall
			//fmt.Println("hit wall")
			walls[move(location, direction)] = true
		case 1:
			// moved
			//fmt.Println("moved")
			if !isBacktracking {
				movesThatMoved = append(movesThatMoved, direction)
			}
			location = move(location, direction)
			spaces[location] = true
		case 2:
			// moved + found oxygen system
			//fmt.Println("moved and found oxygen system")
			location = move(location, direction)
			if !isBacktracking {
				movesThatMoved = append(movesThatMoved, direction)
			}
			return len(movesThatMoved)
		}

		//fmt.Printf("location: %v\n", location)

		//draw(walls, spaces, location)
	}

	return -1
}

func part2(program []int) int {
	state := intcode.GetInitial(program, []int{})

	location := point{0, 0}

	walls := map[point]bool{}
	spaces := map[point]bool{}
	spaces[location] = true

	var movesThatMoved []Direction

Loop:
	for {
		//fmt.Println("------")
		//fmt.Printf("location: %v\n", location)

		isBacktracking := false

		//fmt.Println(movesThatMoved)
		direction := chooseDirection(location, walls, spaces)
		//fmt.Printf("selected direction: %v\n", direction)
		if direction == -1 {
			//fmt.Println("backtracking")
			isBacktracking = true
			lastMove := movesThatMoved[len(movesThatMoved)-1]
			movesThatMoved = movesThatMoved[:len(movesThatMoved)-1]
			direction = invert(lastMove)
		}
		//fmt.Println(movesThatMoved)
		//fmt.Printf("direction: %v\n", direction)

		state.Input = append(state.Input, int(direction))
		for state.ReadOpcode() != 4 /* write output */ {
			state = intcode.Step(state)
		}

		state = intcode.Step(state)

		output := state.Output[len(state.Output)-1]
		switch output {
		case 0:
			// hit wall
			//fmt.Println("hit wall")
			walls[move(location, direction)] = true
		case 1:
			// moved
			//fmt.Println("moved")
			if !isBacktracking {
				movesThatMoved = append(movesThatMoved, direction)
			}
			location = move(location, direction)
			spaces[location] = true
		case 2:
			// moved + found oxygen system
			//fmt.Println("moved and found oxygen system")
			location = move(location, direction)
			if !isBacktracking {
				movesThatMoved = append(movesThatMoved, direction)
			}
			break Loop
		}

		//fmt.Printf("location: %v\n", location)

	}

	frontier := map[point]bool{}
	frontier[location] = true
	oxygenated := map[point]bool{}
	oxygenated[location] = true

	count := 0

	for {
		for p, _ := range frontier {
			delete(spaces, p)
		}

		newFrontier := map[point]bool{}
		for p, _ := range frontier {
			neighbours := getNeighbours(p)
			for i := 0; i < len(neighbours); i++ {
				neighbour := neighbours[i]
				newFrontier[neighbour] = true
			}
		}
		for p, _ := range newFrontier {
			if oxygenated[p] {
				delete(newFrontier, p)
			}
			if walls[p] {
				delete(newFrontier, p)
			}
			if !spaces[p] {
				delete(newFrontier, p)
			}
		}

		if len(newFrontier) == 0 {
			break
		}

		for p, _ := range newFrontier {
			oxygenated[p] = true
		}
		frontier = newFrontier

		count += 1

		//fmt.Println()
		//fmt.Println(count)
		//draw2(walls, spaces, oxygenated)
	}

	return count
}

func getNeighbours(p point) []point {
	return []point{move(p, North), move(p, South), move(p, East), move(p, West)}
}

func move(p point, d Direction) point {
	switch d {
	case North:
		return point{p.x, p.y - 1}
	case South:
		return point{p.x, p.y + 1}
	case East:
		return point{p.x + 1, p.y}
	case West:
		return point{p.x - 1, p.y}
	}
	panic("bad direction")
}

func invert(d Direction) Direction {
	switch d {
	case North:
		return South
	case South:
		return North
	case East:
		return West
	case West:
		return East
	}
	panic("bad direction")
}

func chooseDirection(p point, walls map[point]bool, spaces map[point]bool) Direction {
	directions = [4]Direction{North, East, South, West}
	for i := 0; i < len(directions); i++ {
		direction := directions[i]
		if isUnexplored(p, direction, walls, spaces) {
			return direction
		}
	}
	return -1
}

func isUnexplored(p point, d Direction, walls map[point]bool, spaces map[point]bool) bool {
	//fmt.Println("> isUnexplored:")
	//fmt.Printf("  d: %v\n", d)
	p2 := move(p, d)
	//fmt.Printf("  p2: %v\n", p2)
	_, p2IsWall := walls[p2]
	//fmt.Printf("  is wall: %v\n", p2IsWall)
	_, p2IsSpace := spaces[p2]
	//fmt.Printf("  is space: %v\n", p2IsSpace)
	return !p2IsWall && !p2IsSpace
}

func draw(walls map[point]bool, spaces map[point]bool, location point) {
	fmt.Println()

	minX := 1_000_000
	maxX := -1_000_000
	minY := 1_000_000
	maxY := -1_000_000
	for point, _ := range walls {
		if point.x < minX {
			minX = point.x
		}
		if point.x > maxX {
			maxX = point.x
		}
		if point.y < minY {
			minY = point.y
		}
		if point.y > maxY {
			maxY = point.y
		}
	}
	for point, _ := range spaces {
		if point.x < minX {
			minX = point.x
		}
		if point.x > maxX {
			maxX = point.x
		}
		if point.y < minY {
			minY = point.y
		}
		if point.y > maxY {
			maxY = point.y
		}
	}
	for y := minY; y <= maxY; y++ {
		for x := minX; x <= maxX; x++ {
			if (point{x, y} == location) {
				fmt.Print("X")
			} else if walls[point{x, y}] {
				fmt.Print("#")
			} else if spaces[point{x, y}] {
				fmt.Print(".")
			} else {
				fmt.Print(" ")
			}
		}
		fmt.Println()
	}
}

func draw2(walls map[point]bool, spaces map[point]bool, oxygenated map[point]bool) {
	fmt.Println()

	minX := 1_000_000
	maxX := -1_000_000
	minY := 1_000_000
	maxY := -1_000_000
	for point, _ := range walls {
		if point.x < minX {
			minX = point.x
		}
		if point.x > maxX {
			maxX = point.x
		}
		if point.y < minY {
			minY = point.y
		}
		if point.y > maxY {
			maxY = point.y
		}
	}
	for point, _ := range spaces {
		if point.x < minX {
			minX = point.x
		}
		if point.x > maxX {
			maxX = point.x
		}
		if point.y < minY {
			minY = point.y
		}
		if point.y > maxY {
			maxY = point.y
		}
	}
	for point, _ := range oxygenated {
		if point.x < minX {
			minX = point.x
		}
		if point.x > maxX {
			maxX = point.x
		}
		if point.y < minY {
			minY = point.y
		}
		if point.y > maxY {
			maxY = point.y
		}
	}
	for y := minY; y <= maxY; y++ {
		for x := minX; x <= maxX; x++ {
			if (oxygenated[point{x, y}]) {
				fmt.Print("O")
			} else if walls[point{x, y}] {
				fmt.Print("#")
			} else if spaces[point{x, y}] {
				fmt.Print(".")
			} else {
				fmt.Print(" ")
			}
		}
		fmt.Println()
	}
}

type Direction int

const (
	North = iota + 1
	South
	West
	East
)

var directions = [4]Direction{North, South, East, West}

type point struct {
	x int
	y int
}
