#! /usr/bin/env pypy
from functools import reduce

test_input = "s1,x3/4,pe/b".split(",")
puzzle_input = open("inputs/day_16.txt").read().strip().split(",")


def spin(programs, params):
    x = int(params)
    return programs[-x:] + programs[0:-x]


def exchange(programs, params):
    param_values = [int(p) for p in params.split("/")]
    param_values.sort()
    a, b = param_values
    return programs[0:a] + programs[b] + programs[a + 1: b] + programs[a] + programs[b + 1:]


def partner(programs, params):
    a, b = params.split("/")
    index_a = programs.index(a)
    index_b = programs.index(b)
    return exchange(programs, f"{index_a}/{index_b}")


def dance(programs, move):
    moves = {
        "s": spin,
        "x": exchange,
        "p": partner
    }
    index = move[0]
    params = move[1:]
    return moves[index](programs, params)


def whole_dance(moves, initial):
    return reduce(dance, moves, initial)


def part_2(moves):
    programs = "abcdefghijklmnop"
    states = []

    for n in range(1, 1_000_000_000):
        programs = whole_dance(moves, programs)

        if programs in states:
            print(f"dance starts looping at iteration {n}")
            break

        states.append(programs)

    return states[(1000000000 - 1) % len(states)]


def baz():
    programs = "abcdefghijklmnop"
    for n in range(1, 20):
        programs = whole_dance(puzzle_input, programs)
        print(f"{n}: {programs}")


print(whole_dance(test_input, "abcde"))
print(whole_dance(puzzle_input, "abcdefghijklmnop"))
print(part_2(puzzle_input))
