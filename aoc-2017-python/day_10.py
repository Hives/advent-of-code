#! /usr/bin/env pypy

import operator
from functools import reduce

from lib.knot_hash import calculate_sparse_hash, knot_hash

example_input = (5, [3, 4, 1, 5])
puzzle_input = (256, [102, 255, 99, 252, 200, 24, 219, 57, 103, 2, 226, 254, 1, 0, 69, 216])
puzzle_input_2 = "102,255,99,252,200,24,219,57,103,2,226,254,1,0,69,216"


def part_1(deets):
    final_state = calculate_sparse_hash(deets[1], deets[0], 1)
    return final_state[0] * final_state[1]


def part_2(deets):
    return knot_hash(deets)


print(part_1(puzzle_input))
print(part_2(puzzle_input_2))
