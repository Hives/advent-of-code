package main

import (
	"aoc"
)

func main() {
	input := []moon{
		{{p: 14, v: 0}, {p: 2, v: 0}, {p: 8, v: 0}},
		{{p: 7, v: 0}, {p: 4, v: 0}, {p: 10, v: 0}},
		{{p: 1, v: 0}, {p: 17, v: 0}, {p: 16, v: 0}},
		{{p: -4, v: 0}, {p: -1, v: 0}, {p: 1, v: 0}},
	}
	example1 := []moon{
		{{p: -1, v: 0}, {p: 0, v: 0}, {p: 2, v: 0}},
		{{p: 2, v: 0}, {p: -10, v: 0}, {p: -7, v: 0}},
		{{p: 4, v: 0}, {p: -8, v: 0}, {p: 8, v: 0}},
		{{p: 3, v: 0}, {p: 5, v: 0}, {p: -1, v: 0}},
	}
	example2 := []moon{
		{{p: -8, v: 0}, {p: -10, v: 0}, {p: 0, v: 0}},
		{{p: 5, v: 0}, {p: 5, v: 0}, {p: 10, v: 0}},
		{{p: 2, v: 0}, {p: -7, v: 0}, {p: 3, v: 0}},
		{{p: 9, v: 0}, {p: -8, v: 0}, {p: -3, v: 0}},
	}
	aoc.CheckAnswer("Part 1, Example 1", part1(example1, 10), 179)
	aoc.CheckAnswer("Part 1, Example 2", part1(example2, 100), 1940)

	aoc.CheckAnswer("Part 1", part1(input, 1_000), 9139)
}

func part1(initial []moon, iterations int) int {
	moons := initial
	for i := 0; i < iterations; i++ {
		gravity := applyGravity(moons)
		moons = applyVelocity(gravity)
	}
	return calculateEnergy(moons)
}

//func part2(initial []moon, iterations int) int {
//
//	return -1
//}
//
//func findLoopingPoint(moons []moon, index int)  {
//}

func applyGravity(moons []moon) []moon {
	for i := 0; i < len(moons); i++ {
		for j := 0; j < len(moons); j++ {
			if i != j {
				for k := 0; k < 3; k++ {
					moons[i][k].v += normalise(moons[j][k].p - moons[i][k].p)
				}
			}
		}
	}
	return moons
}

func applyVelocity(moons []moon) []moon {
	for i := 0; i < len(moons); i++ {
		for k := 0; k < 3; k++ {
			moons[i][k].p += moons[i][k].v
		}
	}
	return moons
}

func calculateEnergy(moons []moon) int {
	total := 0
	for _, moon := range moons {
		potential := abs(moon[0].p) + abs(moon[1].p) + abs(moon[2].p)
		kinetic := abs(moon[0].v) + abs(moon[1].v) + abs(moon[2].v)

		total += potential * kinetic
	}
	return total
}

func normalise(i int) int {
	if i < 0 {
		return -1
	}
	if i > 0 {
		return 1
	}
	return 0
}

func abs(i int) int {
	if i < 0 {
		return -i
	}
	return i
}

type moon []moonSingleAxis

type moonSingleAxis struct {
	p int
	v int
}

func zero() []int {
	return []int{0, 0, 0}
}
