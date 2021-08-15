#! /usr/bin/python3
import copy
import re
from functools import reduce

example_input = """b inc 5 if a > 1
a inc 1 if b < 5
c dec -10 if a >= 1
c inc -20 if c == 10""".split("\n")

puzzle_input = open("inputs/day_08.txt").readlines()


def create_condition(register, operator, test_value):
    def condition(state):
        value = state.get(register, 0)
        return {
            ">": value > test_value,
            "<": value < test_value,
            ">=": value >= test_value,
            "<=": value <= test_value,
            "==": value == test_value,
            "!=": value != test_value,
        }[operator]

    return condition


def parse_input_line(line):
    m = re.search(r'^(.+) (inc|dec) (-?\d+) if (.+) (.+) (-?\d+)$', line)

    register = m[1]
    increase = int(m[3]) if m[2] == "inc" else -int(m[3])

    condition = create_condition(m[4], m[5], int(m[6]))

    return register, increase, condition


def parse_input(lines):
    return [parse_input_line(line) for line in lines]


def execute_instruction(instruction, state):
    (register, increase, condition) = instruction
    new_state = copy.deepcopy(state)

    if condition(state):
        new_state[register] = new_state.get(register, 0) + increase

    return new_state


def run(instructions):
    def process(acc, instruction):
        new_state = execute_instruction(instruction, acc["state"])
        highest = max(new_state.values(), default=0)
        return {
            "state": new_state,
            "highest ever": max(highest, acc["highest ever"])
        }

    return reduce(
        process,
        instructions,
        {"state": {}, "highest ever": 0}
    )


def part_1_and_2(lines):
    instructions = parse_input(lines)
    final = run(instructions)

    print(max(final["state"].values()))
    print(final["highest ever"])

    return


part_1_and_2(puzzle_input)
