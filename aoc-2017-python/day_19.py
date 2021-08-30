#! /usr/bin/env pypy
import re

from lib.timer import timer

test_input = """     |          
     |  +--+    
     A  |  C    
 F---|----E|--+ 
     |  |  |  D 
     +B-+  +--+ 
""".split("\n")
puzzle_input = open("inputs/day_19.txt").read().split("\n")


def add(vector1, vector2):
    return vector1[0] + vector2[0], vector1[1] + vector2[1]


def get_neighbours(point):
    units = [(1, 0), (-1, 0), (0, 1), (0, -1)]
    return [add(unit, point) for unit in units]


def invert(vector):
    x, y = vector
    return -x, -y


def is_letter(string):
    return re.match(r"^[A-Z]$", string)


class RoutingDiagramFollower:
    def __init__(self, diagram):
        self.diagram = diagram
        self.location = self.find_start()
        self.direction = 0, 1
        self.collected = []
        self.steps = 0
        self.finished = False

    def find_start(self):
        return self.diagram[0].index("|"), 0

    def lookup_location(self, location):
        x, y = location
        try:
            return self.diagram[y][x]
        except IndexError:
            return " "

    def current(self):
        return self.lookup_location(self.location)

    def set_new_direction(self):
        directions = [(1, 0), (-1, 0), (0, 1), (0, -1)]
        excluded_directions = [self.direction, invert(self.direction)]
        self.direction = \
            [d for d in directions if
             d not in excluded_directions
             and self.lookup_location(add(d, self.location)) != " "][0]

    def go(self):
        while self.current() != " ":
            current = self.current()

            if is_letter(current):
                self.collected.append(current)

            if current == "+":
                self.set_new_direction()

            self.location = add(self.location, self.direction)
            self.steps += 1

        return "".join(self.collected), self.steps


print(RoutingDiagramFollower(puzzle_input).go())
timer(lambda: RoutingDiagramFollower(puzzle_input).go())
