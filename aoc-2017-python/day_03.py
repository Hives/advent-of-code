#! /usr/bin/python3

from functools import reduce

puzzle_input = 265149


class Spiral:
    vectors = [(1, 0), (0, 1), (-1, 0), (0, -1)]

    def __init__(self):
        self.toggle = False
        self.side = 1
        self.count = 1
        self.direction = 0
        self.n = 1
        self.location = (0, 0)

    def next(self):
        vector = self.vectors[self.direction]
        self.location = (self.location[0] + vector[0], self.location[1] + vector[1])
        self.n += 1

        self.count -= 1

        # l.o.l. at this
        if self.count == 0:
            self.direction = (self.direction + 1) % 4
            self.count = self.side

            self.toggle = not self.toggle
            if self.toggle:
                self.side += 1


def part_1(n):
    spiral = Spiral()
    while not spiral.n == n:
        spiral.next()
    return abs(spiral.location[0]) + abs(spiral.location[1])


def vector_add(pt1, pt2):
    return pt1[0] + pt2[0], pt1[1] + pt2[1]


def neighbours(point):
    vectors = [(-1, 1), (0, 1), (1, 1), (-1, 0), (1, 0), (-1, -1), (0, -1), (1, -1)]
    return [vector_add(vector, point) for vector in vectors]


def intersection(lst1, lst2):
    return set(lst1).intersection(lst2)


def part_2(n):
    score = 1
    grid = {(0, 0): score}
    spiral = Spiral()

    while not score > n:
        spiral.next()
        score = reduce(
            lambda acc, point: acc + grid[point],
            intersection(grid.keys(), neighbours(spiral.location)),
            0
        )
        grid[spiral.location] = score

    return score


print(part_1(puzzle_input))
print(part_2(puzzle_input))
