#! /usr/bin/env pypy
import operator
from functools import reduce

example_input = (5, [3, 4, 1, 5])
puzzle_input = (256, [102, 255, 99, 252, 200, 24, 219, 57, 103, 2, 226, 254, 1, 0, 69, 216])
puzzle_input_2 = "102,255,99,252,200,24,219,57,103,2,226,254,1,0,69,216"


def rotate(l, n):
    n_normalised = n % len(l)
    return l[n_normalised:] + l[:n_normalised]


def calculate_sparse_hash(size, lengths, rounds=1):
    joined_lengths = []
    for i in range(0, rounds):
        joined_lengths += lengths

    offset = 0
    skip_size = 0
    state = list(range(size))

    for length in joined_lengths:
        selected = state[0:length]
        remainder = state[length:]
        selected.reverse()

        state = rotate(selected + remainder, length + skip_size)
        offset = (offset + length + skip_size) % size
        skip_size += 1

    return rotate(state, -offset)


def int_to_hex(n):
    h = str(hex(n))[2:]
    if len(h) == 1:
        h = "0" + h
    return h


def calculate_dense_hash(numbers):
    chunked = [numbers[n * 16:(n + 1) * 16] for n in range(0, 16)]
    dense_hash = [reduce(operator.xor, chunk) for chunk in chunked]
    return "".join([int_to_hex(n) for n in dense_hash])


def part_1(deets):
    final_state = calculate_sparse_hash(deets[0], deets[1])
    return final_state[0] * final_state[1]


def part_2(deets):
    salt = [17, 31, 73, 47, 23]
    lengths = [ord(char) for char in list(deets)] + salt
    sparse_hash = calculate_sparse_hash(256, lengths, 64)
    dense_hash = calculate_dense_hash(sparse_hash)
    return dense_hash


print(part_1(puzzle_input))
print(part_2(puzzle_input_2))
