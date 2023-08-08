package aoc

import "fmt"

func CheckAnswer(tag string, actual any, expected any) {
	colorReset := "\033[0m"
	colorRed := "\033[31m"
	colorGreen := "\033[32m"

	if actual == expected {
		fmt.Println(tag, colorGreen, actual, colorReset)
	} else {
		message := fmt.Sprintf("expected %v but got %v", expected, actual)
		fmt.Println(tag, colorRed, message, colorReset)
	}
}
