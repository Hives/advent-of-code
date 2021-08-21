#! /usr/bin/env pypy

example_input = """0 <-> 2
1 <-> 1
2 <-> 0, 3, 4
3 <-> 2, 4
4 <-> 2, 3, 6
5 <-> 6
6 <-> 4, 5""".split("\n")

puzzle_input = open("inputs/day_12.txt").read().strip().split("\n")


def parse_input(deets):
    pipes = {}
    for deet in deets:
        left, right = deet.split(" <-> ")
        pipes[int(left)] = [int(r) for r in right.split(", ")]
    return pipes


def find_group(program, pipes):
    group = set()
    new_neighbours = {program}

    while len(new_neighbours) > 0:
        for neighbour in new_neighbours:
            group.add(neighbour)

        previous_neighbours = new_neighbours
        new_neighbours = set()

        for previous_neighbour in previous_neighbours:
            for neighbour in pipes[previous_neighbour]:
                new_neighbours.add(neighbour)

        new_neighbours = new_neighbours - group

    return group


def count_groups(pipes):
    programs = set(pipes.keys())
    groups = 0

    while len(programs) > 0:
        group = find_group(list(programs)[0], pipes)
        groups += 1
        programs = programs - group

    return groups


def parts_1_and_2(deets):
    pipes = parse_input(deets)
    print(len(find_group(0, pipes)))
    print(count_groups(pipes))


parts_1_and_2(puzzle_input)
