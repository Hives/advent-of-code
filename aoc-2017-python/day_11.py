#! /usr/bin/env pypy

from functools import reduce

examples = [
    ("ne,ne,ne", 3),
    ("ne,ne,sw,sw", 0),
    ("ne,ne,s,s", 2),
    ("se,sw,se,sw,sw", 3)
]
puzzle_input = open("inputs/day_11.txt").read().strip()

units = {
    "n": (0, 1, -1),
    "ne": (1, 0, -1),
    "se": (1, -1, 0),
    "s": (0, -1, 1),
    "sw": (-1, 0, 1),
    "nw": (-1, 1, 0)
}


def add(point1, point2):
    return point1[0] + point2[0], point1[1] + point2[1], point1[2] + point2[2]


def distance(point):
    return (abs(point[0]) + abs(point[1]) + abs(point[2])) / 2


def get_final_distance(walk):
    steps = walk.split(",")
    final = reduce(lambda acc, step: add(acc, units[step]), steps, (0, 0, 0))
    return distance(final)


def get_max_distance(walk):
    steps = walk.split(",")
    locations = []
    current = (0, 0, 0)
    for step in steps:
        current = add(current, units[step])
        locations.append(current)
    return max([distance(location) for location in locations])


def test():
    for walk, final_distance in examples:
        print(f"{get_final_distance(walk)} should be {final_distance}")


test()
print(get_final_distance(puzzle_input))
print(get_max_distance(puzzle_input))
