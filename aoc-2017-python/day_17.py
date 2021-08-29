#! /usr/bin/env pypy

test_input = 3
puzzle_input = 312


def part_1(jump_size):
    buffer = [0]
    index = 0
    for n in range(1, 2017 + 1):
        index = ((index + jump_size) % len(buffer)) + 1
        buffer.insert(index, n)
    final_buffer = buffer
    return final_buffer[final_buffer.index(2017) + 1]


def part_2(jump_size):
    second_value = -1
    index = 0
    for n in range(1, 50_000_000 + 1):
        index = ((index + jump_size) % n) + 1
        if index == 1:
            second_value = n
    return second_value


print(part_1(puzzle_input))
print(part_2(puzzle_input))
