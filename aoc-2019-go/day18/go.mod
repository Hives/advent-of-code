module day18

go 1.20

replace reader => ../lib/reader

replace aoc => ../lib/aoc

replace intcode => ../lib/intcode

require (
	aoc v0.0.0-00010101000000-000000000000
	intcode v0.0.0-00010101000000-000000000000
	reader v0.0.0-00010101000000-000000000000
)

require golang.org/x/exp v0.0.0-20241108190413-2d47ceb2692f // indirect
