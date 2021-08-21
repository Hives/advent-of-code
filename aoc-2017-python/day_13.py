#! /usr/bin/env pypy
import copy

example_input = """0: 3
1: 2
4: 4
6: 4""".split("\n")

puzzle_input = open("inputs/day_13.txt").read().strip().split("\n")


def parse_input(deets):
    scanners = {}
    for deet in deets:
        layer, range = deet.split(": ")
        scanners[int(layer)] = {
            "range": int(range),
            "loop length": (2 * int(range)) - 2,
        }
    return scanners


def take_a_trip_1(scanners_original):
    scanners = copy.deepcopy(scanners_original)
    number_of_layers = max(scanners.keys())

    for scanner in scanners.values():
        scanner["position"] = 0

    depth = 0
    severity = 0

    while depth <= number_of_layers:
        if depth in scanners and scanners[depth]["position"] == 0:
            r = scanners[depth]["range"]
            severity += depth * r
        depth += 1
        for scanner in scanners.values():
            scanner["position"] = (scanner["position"] + 1) % scanner["loop length"]

    return severity


def take_a_trip_2(scanners_original, delay):
    scanners = copy.deepcopy(scanners_original)
    number_of_layers = max(scanners.keys())

    for scanner in scanners.values():
        scanner["position"] = delay % scanner["loop length"]

    depth = 0

    while depth <= number_of_layers:
        if depth in scanners and scanners[depth]["position"] == 0:
            return False
        depth += 1
        for scanner in scanners.values():
            scanner["position"] = (scanner["position"] + 1) % scanner["loop length"]

    return True


def part_1(deets):
    scanners = parse_input(deets)
    return take_a_trip_1(scanners)


def part_2(deets):
    delay = 0
    scanners = parse_input(deets)

    while not take_a_trip_2(scanners, delay):
        if delay % 100_000 == 0:
            print(delay)
        delay += 1

    return delay


print(part_1(puzzle_input))
print(f"part 2 example solution '{part_2(example_input)}' should equal 10")
# brute force solution... slow....
print(part_2(puzzle_input))
