#! /usr/bin/python3

test_input = [0, 3, 0, 1, -3]
puzzle_input = [int(line) for line in open("inputs/day_05.txt").readlines()]


def part_1(jumps):
    jumps_copy = jumps.copy()

    pointer = 0
    count = 0

    while pointer < len(jumps_copy):
        new_pointer = pointer + jumps_copy[pointer]
        jumps_copy[pointer] += 1
        pointer = new_pointer
        count += 1

    return count


def part_2(jumps):
    jumps_copy = jumps.copy()

    pointer = 0
    count = 0

    while pointer < len(jumps_copy):
        jump = jumps_copy[pointer]
        new_pointer = pointer + jump

        if jump >= 3:
            jumps_copy[pointer] -= 1
        else:
            jumps_copy[pointer] += 1

        pointer = new_pointer
        count += 1

    return count


print(part_1(puzzle_input))
print(part_2(puzzle_input))
