#! /usr/bin/python3

from functools import reduce
from itertools import permutations

puzzle_input = [[int(n) for n in line.strip().split("\t")] for line in open("inputs/day_02.txt").readlines()]


def part_1(spreadsheet):
    return reduce(
        lambda acc, row: acc + max(row) - min(row),
        spreadsheet,
        0
    )


def find_even_division(row):
    pair_which_evenly_divides = next(filter(
        lambda p: p[0] % p[1] == 0,
        list(permutations(row, 2))
    ))

    return pair_which_evenly_divides[0] // pair_which_evenly_divides[1]


def part_2(spreadsheet):
    return reduce(
        lambda acc, row: acc + find_even_division(row),
        spreadsheet,
        0
    )


print(part_1(puzzle_input))
print(part_2(puzzle_input))
