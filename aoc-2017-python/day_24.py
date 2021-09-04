#! /usr/bin/env pypy
from lib.list_things import flatten

test_input = """0/2
2/2
2/3
3/4
3/5
0/1
10/1
9/10""".split("\n")
puzzle_input = open("inputs/day_24.txt").read().strip().split("\n")


def parse(deets):
    return [[int(end) for end in connector.split("/")] for connector in deets]


def strength(bridge):
    return sum(flatten(bridge))


class BridgeMaker:
    def __init__(self, connections):
        self.unfinished = [{"connected": [[0, 0]], "remaining": connections}]
        self.finished = []

    def tick(self):
        new = []
        for bridge in self.unfinished:
            terminal = bridge["connected"][-1][1]
            possible_connections = [c for c in bridge["remaining"] if terminal in c]
            if len(possible_connections) == 0:
                self.finished.append(bridge["connected"])
            else:
                for poss in possible_connections:
                    extension = poss if poss[0] == terminal else poss[::-1]
                    new.append({
                        "connected": bridge["connected"] + [extension],
                        "remaining": [c for c in bridge["remaining"] if c != poss]
                    })
        self.unfinished = new

    def go(self):
        while len(self.unfinished) > 0:
            self.tick()

    def strongest(self):
        return max([strength(bridge) for bridge in self.finished])

    def strength_of_longest(self):
        longest_length = max([len(bridge) for bridge in self.finished])
        longest_bridges = [bridge for bridge in self.finished if len(bridge) == longest_length]
        return max([strength(bridge) for bridge in longest_bridges])


def do_it(deets):
    bridge_maker = BridgeMaker(parse(deets))
    bridge_maker.go()
    print(bridge_maker.strongest())
    print(bridge_maker.strength_of_longest())


do_it(test_input)
do_it(puzzle_input)
