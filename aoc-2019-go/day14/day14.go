package main

import (
	"aoc"
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
}

func part1(input []string) int {
	ingredientsMap := parse(input)

	spare := map[string]int{}
	required := map[string]int{"FUEL": 1}

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
