#! /usr/bin/python3

from functools import reduce

puzzle_input = [int(char) for char in open("input.txt").read().strip()]


def pair_with_next(xs):
    return [[xs[n], xs[(n + 1) % len(xs)]] for n in range(len(xs))]


def pair_with_opposite(xs):
    return [[xs[n], xs[(n + (len(xs) // 2)) % len(xs)]] for n in range(len(xs))]


def take_value_if_equal(pair):
    return pair[0] if pair[0] == pair[1] else 0


def solve_captcha(numbers, make_pairs):
    return reduce(
        lambda acc, pair: acc + take_value_if_equal(pair),
        make_pairs(numbers),
        0)


def part_1(captcha):
    return solve_captcha(captcha, pair_with_next)


def part_2(captcha):
    return solve_captcha(captcha, pair_with_opposite)


print(part_1(puzzle_input))
print(part_2(puzzle_input))
