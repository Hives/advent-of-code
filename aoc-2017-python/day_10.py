#! /usr/bin/env pypy

example_input = (5, [3, 4, 1, 5])
puzzle_input = (256, [102, 255, 99, 252, 200, 24, 219, 57, 103, 2, 226, 254, 1, 0, 69, 216])


def rotate(l, n):
    n_normalised = n % len(l)
    return l[n_normalised:] + l[:n_normalised]


def knot_hash(size, lengths):
    offset = 0
    skip_size = 0
    state = list(range(size))

    for length in lengths:
        selected = state[0:length]
        remainder = state[length:]
        selected.reverse()

        state = rotate(selected + remainder, length + skip_size)
        offset = (offset + length + skip_size) % size
        skip_size += 1

    renormalised_state = rotate(state, -offset)
    return renormalised_state[0] * renormalised_state[1]


def part_1(deets):
    return knot_hash(deets[0], deets[1])


print(part_1(puzzle_input))
