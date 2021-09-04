#! /usr/bin/env pypy
import re

test_input = """Begin in state A.
Perform a diagnostic checksum after 6 steps.

In state A:
  If the current value is 0:
    - Write the value 1.
    - Move one slot to the right.
    - Continue with state B.
  If the current value is 1:
    - Write the value 0.
    - Move one slot to the left.
    - Continue with state B.

In state B:
  If the current value is 0:
    - Write the value 1.
    - Move one slot to the left.
    - Continue with state A.
  If the current value is 1:
    - Write the value 1.
    - Move one slot to the right.
    - Continue with state A.""".split("\n\n")
puzzle_input = open("inputs/day_25.txt").read().strip().split("\n\n")


def parse_rule(rule_string):
    def get_write(line):
        return int(re.search(r'Write the value ([0,1])', line)[1])

    def get_move(line):
        return 1 if re.search(r'Move one slot to the (left|right)', line)[1] == "right" else -1

    def get_next_state(line):
        return re.search(r'Continue with state ([A-Z])', line)[1]

    lines = rule_string.split("\n")

    state = re.search(r'In state ([A-Z]):', lines[0])[1]

    return state, {
        0: {
            "write": get_write(lines[2]),
            "move": get_move(lines[3]),
            "state": get_next_state(lines[4])
        },
        1: {
            "write": get_write(lines[6]),
            "move": get_move(lines[7]),
            "state": get_next_state(lines[8])
        },
    }


def parse(deets):
    config = deets[0].split("\n")
    rules = {}
    for state, rule in [parse_rule(rule_string) for rule_string in deets[1:]]:
        rules[state] = rule
    return {
        "state": (re.search(r'Begin in state ([A-Z]).', config[0])[1]),
        "checksum_steps": (int(re.search(r'Perform a diagnostic checksum after (\d+) steps.', config[1])[1])),
        "rules": rules
    }


class Turing:
    def __init__(self, config):
        self.state = config["state"]
        self.rules = config["rules"]
        self.checksum_steps = config["checksum_steps"]
        self.on_positions = set()
        self.cursor = 0
        self.steps = 0

    def tick(self):
        current = 1 if self.cursor in self.on_positions else 0
        rule = self.rules[self.state][current]

        if rule["write"] == 1:
            self.on_positions.add(self.cursor)
        else:
            try:
                self.on_positions.remove(self.cursor)
            except KeyError:
                pass

        self.cursor += rule["move"]
        self.state = rule["state"]
        self.steps += 1

    def go(self):
        while self.steps < self.checksum_steps:
            self.tick()


def part_1(deets):
    config = parse(deets)
    turing = Turing(config)
    turing.go()
    return len(turing.on_positions)


print(part_1(test_input))
print(part_1(puzzle_input))
