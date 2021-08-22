#! /usr/bin/env pypy
from functools import reduce

from lib.list_things import flat_map, flatten
from lib.knot_hash import knot_hash

example_input = "flqrgnkx"
puzzle_input = "ffayrhll"


def char_to_binary(char):
    binary = bin(int(char, 16))[2:]
    dots_and_hashes = ["#" if d == "1" else "." for d in list(binary)]
    return (["."] * (4 - len(dots_and_hashes))) + dots_and_hashes


def hash_to_row(string):
    return flat_map(char_to_binary, list(string))


def get_disk_status(key):
    return [hash_to_row(knot_hash(f"{key}-{n}")) for n in range(0, 128)]


def part_1(key):
    disk = get_disk_status(key)
    return reduce(
        lambda acc, current: acc + 1 if current == "#" else acc,
        flatten(disk),
        0
    )


def find_first_used(disk):
    for y in range(0, 128):
        for x in range(0, 128):
            if disk[y][x] == "#":
                return x, y


def add(p1, p2):
    return p1[0] + p2[0], p1[1] + p2[1]


def within_disk_bounds(point):
    return 0 <= point[0] < 128 and 0 <= point[1] < 128


def get_neighbours(point):
    units = [(1, 0), (-1, 0), (0, 1), (0, -1)]
    neighbours = [add(unit, point) for unit in units]
    return [n for n in neighbours if within_disk_bounds(n)]


def printy(disk):
    for row in disk:
        print("".join(row))


def part_2(key):
    disk = get_disk_status(key)
    regions = 0

    while True:
        first_used = find_first_used(disk)
        if first_used is None:
            break

        regions += 1
        current = [first_used]

        while len(current) > 0:
            for point in current:
                disk[point[1]][point[0]] = "."

            neighbours = set(flat_map(get_neighbours, current))
            used_neighbours = [n for n in neighbours if disk[n[1]][n[0]] == "#"]
            current = used_neighbours

    return regions


print(part_1(puzzle_input))
print(part_2(puzzle_input))
