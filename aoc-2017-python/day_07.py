#! /usr/bin/python3
import re


def parse_input_line(line):
    m = re.search(r'([a-z]+) \((\d+)\)( -> )?(.+)?', line)
    name = m[1]
    weight = int(m[2])
    children = m[4].split(", ") if m[4] else []
    return {
        "name": name,
        "weight": weight,
        "children": children
    }


def flatten(t):
    return [item for sublist in t for item in sublist]


def create_parent_map(programs):
    d = dict()
    for p in programs:
        for c in p["children"]:
            d[c] = p["name"]
    return d


def create_program_map(programs):
    d = dict()
    for p in programs:
        d[p["name"]] = p
    return d


def part_1(programs):
    program_names = [p["name"] for p in programs]
    programs_with_parents = flatten([p["children"] for p in programs])
    return (set(program_names) - set(programs_with_parents)).pop()


def part_2(programs):
    program_map = create_program_map(programs)
    root = part_1(programs)

    def fill_in_total_weights(name):
        program = program_map[name]

        if len(program["children"]) == 0:
            program["total weight"] = program["weight"]
            program["balanced"] = True
        else:
            child_total_weights = [fill_in_total_weights(c) for c in program["children"]]
            program["total weight"] = program["weight"] + sum(child_total_weights)
            program["balanced"] = len(set(child_total_weights)) < 2

        return program["total weight"]

    fill_in_total_weights(root)

    unbalanced = [p for p in program_map.values() if not p["balanced"]]
    terminal_unbalanced = [p for p in unbalanced if all([program_map[c]["balanced"] for c in p["children"]])]

    for p in terminal_unbalanced:
        print("unbalanced disc:")
        print(p)
        print("children:")
        for c in p["children"]:
            print(program_map[c])

    return


puzzle_input = [parse_input_line(line) for line in open("inputs/day_07.txt").readlines()]
example_input = [parse_input_line(line) for line in open("inputs/day_07_example.txt").readlines()]

print(part_1(puzzle_input))
part_2(puzzle_input)
