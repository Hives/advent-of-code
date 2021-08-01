#! /usr/bin/python3

from functools import reduce

puzzle_input = [int(char) for char in open("input.txt").read().strip()]


def pair_with_next(xs):
    return list(map(
        lambda n: [xs[n], xs[(n + 1) % len(xs)]],
        range(len(xs))))


def pair_with_opposite(xs):
    return list(map(
        lambda n: [xs[n], xs[(n + (len(xs) // 2)) % len(xs)]],
        range(len(xs))))


def take_value_if_equal(pair):
    if pair[0] == pair[1]:
        return pair[0]
    else:
        return 0


def solve_captcha(input, make_pairs):
    return reduce(
        lambda acc, pair: acc + take_value_if_equal(pair),
        make_pairs(input),
        0)


def part_1(input):
    return solve_captcha(input, pair_with_next)


def part_2(input):
    return solve_captcha(input, pair_with_opposite)


print(part_1(puzzle_input))
print(part_2(puzzle_input))
