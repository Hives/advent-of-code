package main

import (
	"aoc"
)

func main() {
	zero := vector{0, 0, 0}
	input := []moon{
		{position: vector{14, 2, 8}, velocity: zero},
		{position: vector{7, 4, 10}, velocity: zero},
		{position: vector{1, 17, 16}, velocity: zero},
		{position: vector{-4, -1, 1}, velocity: zero},
	}
	example1 := []moon{
		{position: vector{-1, 0, 2}, velocity: zero},
		{position: vector{2, -10, -7}, velocity: zero},
		{position: vector{4, -8, 8}, velocity: zero},
		{position: vector{3, 5, -1}, velocity: zero},
	}
	example2 := []moon{
		{position: vector{-8, -10, 0}, velocity: zero},
		{position: vector{5, 5, 10}, velocity: zero},
		{position: vector{2, -7, 3}, velocity: zero},
		{position: vector{9, -8, -3}, velocity: zero},
	}
	aoc.CheckAnswer("Part 1, Example 1", part1(example1, 10), 179)
	aoc.CheckAnswer("Part 1, Example 2", part1(example2, 100), 1940)

	aoc.CheckAnswer("Part 1", part1(input, 1_000), 9139)
}

func part1(initial []moon, iterations int) int {
	moons := initial
	for i := 0; i < iterations; i++ {
		moons = applyVelocity(applyGravity(moons))
	}
	return calculateEnergy(moons)
}

func applyGravity(moons []moon) []moon {
	for i := 0; i < len(moons); i++ {
		for j := 0; j < len(moons); j++ {
			if i != j {
				otherMoon := moons[j]
				moons[i].velocity.x += normalise(otherMoon.position.x - moons[i].position.x)
				moons[i].velocity.y += normalise(otherMoon.position.y - moons[i].position.y)
				moons[i].velocity.z += normalise(otherMoon.position.z - moons[i].position.z)
			}
		}
	}
	return moons
}

func applyVelocity(moons []moon) []moon {
	for i := 0; i < len(moons); i++ {
		moons[i].position = moons[i].position.plus(moons[i].velocity)
	}
	return moons
}

func calculateEnergy(moons []moon) int {
	total := 0
	for _, moon := range moons {
		p := moon.position
		potential := abs(p.x) + abs(p.y) + abs(p.z)

		v := moon.velocity
		kinetic := abs(v.x) + abs(v.y) + abs(v.z)

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
	position vector
	velocity vector
}

func (v1 vector) plus(v2 vector) vector {
	return vector{
		v1.x + v2.x,
		v1.y + v2.y,
		v1.z + v2.z,
	}
}

type vector struct {
	x int
	y int
	z int
}
