#! /usr/bin/env pypy
from lib.list_things import flatten

initial = ".#./..#/###"
test_input = """../.# => ##./#../...
.#./..#/### => #..#/..../..../#..#""".split("\n")
puzzle_input = open("inputs/day_21.txt").read().strip().split("\n")


def rotate_cw(grid):
    return list(zip(*grid[::-1]))


def flip(grid):
    return [row[::-1] for row in grid]


def string_to_grid(string):
    return [list(r) for r in string.split("/")]


def grid_to_string(grid):
    return "/".join(["".join(r) for r in grid])


def parse_rules(deets):
    rules = {}

    for rule in deets:
        left, right = rule.split(" => ")

        orientation_1 = string_to_grid(left)
        orientation_2 = rotate_cw(orientation_1)
        orientation_3 = rotate_cw(orientation_2)
        orientation_4 = rotate_cw(orientation_3)

        flipped_orientation_1 = flip(orientation_1)
        flipped_orientation_2 = rotate_cw(flipped_orientation_1)
        flipped_orientation_3 = rotate_cw(flipped_orientation_2)
        flipped_orientation_4 = rotate_cw(flipped_orientation_3)

        rules[grid_to_string(orientation_1)] = right
        rules[grid_to_string(orientation_2)] = right
        rules[grid_to_string(orientation_3)] = right
        rules[grid_to_string(orientation_4)] = right
        rules[grid_to_string(flipped_orientation_1)] = right
        rules[grid_to_string(flipped_orientation_2)] = right
        rules[grid_to_string(flipped_orientation_3)] = right
        rules[grid_to_string(flipped_orientation_4)] = right

    return rules


def get_sub_grid(grid, x1, y1, x2, y2):
    return [[row[x] for x in range(x1, x2)] for row in [grid[y] for y in range(y1, y2)]]


def divide_grid(grid):
    size = len(grid)
    chunk_size = 2 if size % 2 == 0 else 3

    foo = [chunk_size * n for n in range(0, int(size / chunk_size))]
    bar = [[(x, y) for x in foo] for y in foo]
    return [[get_sub_grid(grid, x, y, x + chunk_size, y + chunk_size) for x, y in row] for row in bar]


def un_divide_grid(divided_grid):
    subgrid_size = len(divided_grid[0][0])
    subgrids = len(divided_grid[0])
    return flatten([[flatten([subgrid[n] for subgrid in divided_grid[m]]) for n in range(0, subgrid_size)] for m in
            range(0, subgrids)])


def process_subgrid(subgrid, rules):
    return string_to_grid(rules[grid_to_string(subgrid)])


def process_grid(grid, rules):
    divided_grid = divide_grid(grid)
    transformed = [[process_subgrid(subgrid, rules) for subgrid in row] for row in divided_grid]
    return un_divide_grid(transformed)


def count_on_after_iterating(deets, iterations):
    rules = parse_rules(deets)
    grid = string_to_grid(initial)
    for n in range(0, iterations):
        print("---")
        print_grid(grid)
        grid = process_grid(grid, rules)
    return flatten(grid).count('#')


def print_grid(grid):
    for row in grid:
        print("".join(row))


# test - should equal 12
# print(part_1(test_input, 2))

print(count_on_after_iterating(puzzle_input, 5))
print(count_on_after_iterating(puzzle_input, 18))
