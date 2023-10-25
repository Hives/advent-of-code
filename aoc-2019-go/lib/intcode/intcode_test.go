package intcode

import (
	"reader"
	"testing"
)

func TestIntcode(t *testing.T) {
	program := reader.Program("./test-program.txt")
	initialState := GetInitial(program, []int{1}, Quiet)
	finalState := Run(initialState)

	actual := finalState.Output[0]
	expected := 2671328082

	if actual != expected {
		t.Fatalf("Test program output = %d, expected %d", actual, expected)
	}
}
