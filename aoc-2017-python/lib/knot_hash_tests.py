#! /usr/bin/env pypy

from lib.knot_hash import knot_hash


def tests():
    foo = [("", "a2582a3a0e66e6e86e3812dcb672a272"),
           ("AoC 2017", "33efeb34ea91902bb2f59c9920caa6cd"),
           ("1,2,3", "3efbe78a8d82f29979031a4aa0b16a9d"),
           ("1,2,4", "63960835bcdc130f0b66d7ff4f6a5a8e")]
    for string, expected in foo:
        actual = knot_hash(string)
        outcome = "CORRECT" if actual == expected else "FAILURE"
        print(f"hashed \"{string}\" and got {actual}: {outcome}")
