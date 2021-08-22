#! /usr/bin/env pypy

example_input = 65, 8921
puzzle_input = 277, 349


class Generator:
    def __init__(self, start, factor, multiple):
        self.current = start
        self.factor = factor
        self.multiple = multiple

    def next(self):
        while True:
            self.current = (self.current * self.factor) % 2147483647
            if self.current % self.multiple == 0:
                break
        return self.current


def last_16_bits(n):
    return n & 65535


def match(n1, n2):
    return last_16_bits(n1) == last_16_bits(n2)


def solve(deets, a_multiple, b_multiple, iterations):
    a_init, b_init = deets
    gen_a = Generator(a_init, 16807, a_multiple)
    gen_b = Generator(b_init, 48271, b_multiple)
    matches = 0

    for n in range(0, iterations):
        if match(gen_a.next(), gen_b.next()):
            matches += 1

    return matches


def part_1(deets):
    return solve(deets, 1, 1, 40_000_000)


def part_2(deets):
    return solve(deets, 4, 8, 5_000_000)


print(part_1(puzzle_input))
print(part_2(puzzle_input))
