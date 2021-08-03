#! /usr/bin/python3

from functools import reduce

puzzle_input = [line.strip().split(" ") for line in open("input.txt").readlines()]


def is_valid_1(passphrase):
    return len(set(passphrase)) == len(passphrase)


def is_valid_2(passphrase):
    sorted_words = list(map(
        lambda word: ''.join(sorted(word)),
        passphrase
    ))
    return is_valid_1(sorted_words)


def solve(passphrases, is_valid):
    return reduce(
        lambda acc, passphrase: acc + (1 if is_valid(passphrase) else 0),
        passphrases,
        0
    )


def part_1(passphrases):
    return solve(passphrases, is_valid_1)


def part_2(passphrases):
    return solve(passphrases, is_valid_2)


print(part_1(puzzle_input))
print(part_2(puzzle_input))
