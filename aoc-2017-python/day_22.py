#! /usr/bin/env pypy

test_input = "..#/#../...".split("/")
puzzle_input = open("inputs/day_22.txt").read().strip().split("\n")


def parse(deets):
    center = int(len(deets) / 2), int(len(deets[0]) / 2)
    infected = set()
    for j in range(0, len(deets)):
        for i in range(0, len(deets[0])):
            if deets[j][i] == "#":
                infected.add((i - center[0], j - center[1]))
    return infected


def add(p1, p2):
    return p1[0] + p2[0], p1[1] + p2[1]


class Virus1:
    def __init__(self, initial_infected):
        self.infected = initial_infected
        self.position = (0, 0)
        self.units = [(0, -1), (-1, 0), (0, 1), (1, 0)]
        self.direction = 0
        self.infections = 0

    def infect(self):
        self.infected.add(self.position)
        self.infections += 1

    def clean(self):
        self.infected.remove(self.position)

    def turn_right(self):
        self.direction += -1
        self.direction %= 4

    def turn_left(self):
        self.direction += 1
        self.direction %= 4

    def move_forward(self):
        self.position = add(self.position, self.units[self.direction])

    def __repr__(self):
        min_x = min(x for (x, y) in self.infected)
        max_x = max(x for (x, y) in self.infected)
        min_y = min(y for (x, y) in self.infected)
        max_y = max(y for (x, y) in self.infected)

        output = []
        for y in range(min_y, max_y + 1):
            row = []
            for x in range(min_x, max_x + 1):
                spot = "#" if (x, y) in self.infected else "."
                if (x, y) == self.position:
                    left_gutter = "["
                elif (x - 1, y) == self.position:
                    left_gutter = "]"
                else:
                    left_gutter = " "
                row.append(left_gutter + spot)
            output.append(row)

        return "--\n" + "\n".join(["".join(row) for row in output])

    def burst(self):
        if self.position in self.infected:
            self.turn_right()
            self.clean()
        else:
            self.turn_left()
            self.infect()
        self.move_forward()


class Virus2:
    def __init__(self, initial_infected):
        self.nodes = {p: "#" for p in initial_infected}
        self.position = (0, 0)
        self.units = [(0, -1), (-1, 0), (0, 1), (1, 0)]
        self.direction = 0
        self.infections = 0

    def current(self):
        return self.nodes.get(self.position, ".")

    def infect(self):
        self.nodes[self.position] = "#"
        self.infections += 1

    def weaken(self):
        self.nodes[self.position] = "W"

    def flag(self):
        self.nodes[self.position] = "F"

    def clean(self):
        del (self.nodes[self.position])

    def turn_right(self):
        self.direction += -1
        self.direction %= 4

    def turn_left(self):
        self.direction += 1
        self.direction %= 4

    def reverse(self):
        self.direction += 2
        self.direction %= 4

    def move_forward(self):
        self.position = add(self.position, self.units[self.direction])

    def __repr__(self):
        min_x = min(x for (x, y) in self.nodes.keys())
        max_x = max(x for (x, y) in self.nodes.keys())
        min_y = min(y for (x, y) in self.nodes.keys())
        max_y = max(y for (x, y) in self.nodes.keys())

        output = []
        for y in range(min_y, max_y + 1):
            row = []
            for x in range(min_x, max_x + 1):
                spot = self.nodes.get((x, y), ".")
                if (x, y) == self.position:
                    left_gutter = "["
                elif (x - 1, y) == self.position:
                    left_gutter = "]"
                else:
                    left_gutter = " "
                row.append(left_gutter + spot)
            output.append(row)

        return "--\n" + "\n".join(["".join(row) for row in output])

    def burst(self):
        current = self.current()

        if current == ".":
            self.turn_left()
            self.weaken()
        elif current == "W":
            self.infect()
        elif current == "#":
            self.turn_right()
            self.flag()
        elif current == "F":
            self.reverse()
            self.clean()
        else:
            raise Exception("OMGQTFBBQ")

        self.move_forward()


def part_1(deets, bursts):
    virus = Virus1(parse(deets))
    for _ in range(0, bursts):
        virus.burst()
    # print(virus)
    return virus.infections


def part_2(deets, bursts):
    virus = Virus2(parse(deets))
    for _ in range(0, bursts):
        virus.burst()
    # print(virus)
    return virus.infections


print(part_1(test_input, 1_000))
print(part_1(puzzle_input, 10_000))
print(part_2(puzzle_input, 10_000_000))
