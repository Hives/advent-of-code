package main

import (
	"aoc"
)

func main() {
	input := []moon{
		{position: []int{14, 2, 8}, velocity: zero()},
		{position: []int{7, 4, 10}, velocity: zero()},
		{position: []int{1, 17, 16}, velocity: zero()},
		{position: []int{-4, -1, 1}, velocity: zero()},
	}
	example1 := []moon{
		{position: []int{-1, 0, 2}, velocity: zero()},
		{position: []int{2, -10, -7}, velocity: zero()},
		{position: []int{4, -8, 8}, velocity: zero()},
		{position: []int{3, 5, -1}, velocity: zero()},
	}
	example2 := []moon{
		{position: []int{-8, -10, 0}, velocity: zero()},
		{position: []int{5, 5, 10}, velocity: zero()},
		{position: []int{2, -7, 3}, velocity: zero()},
		{position: []int{9, -8, -3}, velocity: zero()},
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

func applyGravity(moons []moon) []moon {
	for i := 0; i < len(moons); i++ {
		for j := 0; j < len(moons); j++ {
			if i != j {
				for k := 0; k < 3; k++ {
					moons[i].velocity[k] += normalise(moons[j].position[k] - moons[i].position[k])
				}
			}
		}
	}
	return moons
}

func applyVelocity(moons []moon) []moon {
	for i := 0; i < len(moons); i++ {
		for k := 0; k < 3; k++ {
			moons[i].position[k] += moons[i].velocity[k]
		}
	}
	return moons
}

func calculateEnergy(moons []moon) int {
	total := 0
	for _, moon := range moons {
		p := moon.position
		potential := abs(p[0]) + abs(p[1]) + abs(p[2])

		v := moon.velocity
		kinetic := abs(v[0]) + abs(v[1]) + abs(v[2])

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

type moon struct {
	position []int
	velocity []int
}

func zero() []int {
	return []int{0, 0, 0}
}
