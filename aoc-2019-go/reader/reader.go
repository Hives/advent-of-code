package reader

import (
	"os"
	"strconv"
	"strings"
)

func String(path string) string {
	return string(read(path))
}

func Strings(path string) (ret []string) {
	ss := strings.Split(String(path), "\n")
	for _, s := range ss {
		if s != "" {
			ret = append(ret, s)
		}
	}
	return ret
}

func Ints(path string) []int {
	input := String(path)
	trimmed := strings.Trim(input, "\n")
	split := strings.Split(trimmed, ",")
	var ints []int
	for _, s := range split {
		n, err := strconv.Atoi(s)
		check(err)
		ints = append(ints, n)
	}
	return ints
}

func read(path string) []byte {
	input, err := os.ReadFile(path)
	check(err)
	return input
}

func check(e error) {
	if e != nil {
		panic(e)
	}
}
