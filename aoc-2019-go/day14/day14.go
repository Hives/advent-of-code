package main

import (
	"aoc"
	"fmt"
	"math"
	"reader"
	"strconv"
	"strings"
)

func main() {
	input := reader.Strings("./input.txt")
	example1 := reader.Strings("./example1.txt")
	aoc.CheckAnswer("Example 1", part1(example1), 31)
	aoc.CheckAnswer("Part 1", part1(input), 273638)
	aoc.CheckAnswer("Part 2", part2(input), -1)
	// part 2 answer is 4,200,533. worked out by manual binary search.
}

func part1(input []string) int {
	return calculateOreRequiredForFuel(1, parse(input))
}

func part2(input []string) int {
	// 4_200_534 too much
	// 4_200_533 too little

	fuel := 4_200_534
	oreRequired := calculateOreRequiredForFuel(fuel, parse(input))
	fmt.Printf("oreRequired: %v\n", oreRequired)
	if oreRequired > 1000000000000 {
		fmt.Println("too much")
	} else if oreRequired < 1000000000000 {
		fmt.Println("too little")
	} else {
		fmt.Println("bang on")
	}
	return 0
}

func calculateOreRequiredForFuel(fuel int, ingredientsMap map[string]recipe) int {
	spare := map[string]int{}
	required := map[string]int{"FUEL": fuel}

	for true {
		chemical := getNextChemical(required)

		if chemical == "ORE" {
			return required["ORE"]
		}

		quantityRequired := required[chemical]

		recipe := ingredientsMap[chemical]
		iterationsNeeded := int(math.Ceil(float64(quantityRequired) / float64(recipe.makes)))

		for _, component := range recipe.components {
			requiredOfComponent := component.quantity * iterationsNeeded

			gotAlready := getOrZero(component.chemical, spare)

			stillNeeded := requiredOfComponent - gotAlready

			takenFromSpare := requiredOfComponent - stillNeeded

			if takenFromSpare > 0 {
				spare[component.chemical] -= takenFromSpare
			}

			required = addToMap(component.chemical, stillNeeded, required)
		}

		quantityProduced := iterationsNeeded * recipe.makes

		excess := quantityProduced - quantityRequired

		if excess > 0 {
			spare = addToMap(chemical, excess, spare)
		}

		delete(required, chemical)
	}

	return -1
}

func parse(input []string) map[string]recipe {
	ingredientsMap := map[string]recipe{}
	for _, line := range input {
		split := strings.Split(line, " => ")
		output := parseComponent(split[1])
		inputs := []component{}
		for _, v := range strings.Split(split[0], ", ") {
			inputs = append(inputs, parseComponent(v))
		}
		ingredientsMap[output.chemical] = recipe{
			makes:      output.quantity,
			components: inputs,
		}
	}
	return ingredientsMap
}

func parseComponent(input string) component {
	split := strings.Split(input, " ")
	return component{
		chemical: split[1],
		quantity: toInt(split[0]),
	}
}

type recipe struct {
	makes      int
	components []component
}

type component struct {
	chemical string
	quantity int
}

func getNextChemical(m map[string]int) string {
	keys := []string{}
	for k := range m {
		keys = append(keys, k)
	}
	for _, k := range keys {
		if k != "ORE" {
			return k
		}
	}
	return "ORE"
}

func toInt(input string) int {
	n, _ := strconv.Atoi(input)
	return n
}

func getOrZero(k string, m map[string]int) int {
	value, ok := m[k]
	if ok {
		return value
	}
	return 0
}

func addToMap(key string, value int, m map[string]int) map[string]int {
	_, ok := m[key]
	if ok {
		m[key] += value
	} else {
		m[key] = value
	}
	return m
}
