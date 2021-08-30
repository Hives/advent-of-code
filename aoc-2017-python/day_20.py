#! /usr/bin/env pypy
import re
from collections import Counter

from lib.timer import timer

test_input_2 = """p=<-6,0,0>, v=<3,0,0>, a=<0,0,0>    
p=<-4,0,0>, v=<2,0,0>, a=<0,0,0>
p=<-2,0,0>, v=<1,0,0>, a=<0,0,0>
p=<3,0,0>, v=<-1,0,0>, a=<0,0,0>""".split("\n")
puzzle_input = open("inputs/day_20.txt").read().strip().split("\n")


def manhattan_distance(vector):
    return abs(vector[0]) + abs(vector[1]) + abs(vector[2])


def add(vector1, vector2):
    return vector1[0] + vector2[0], vector1[1] + vector2[1], vector1[2] + vector2[2]


def tuple_of_ints(v1, v2, v3):
    return tuple([int(p) for p in [v1, v2, v3]])


class Particle:
    def __init__(self, initial, identifier):
        self.id = identifier
        _, px, py, pz, vx, vy, vz, ax, ay, az = re.search(
            r"p=<(-?\d+),(-?\d+),(-?\d+)>, v=<(-?\d+),(-?\d+),(-?\d+)>, a=<(-?\d+),(-?\d+),(-?\d+)>",
            initial)
        self.position = tuple_of_ints(px, py, pz)
        self.velocity = tuple_of_ints(vx, vy, vz)
        self.acceleration = tuple_of_ints(ax, ay, az)

    def __repr__(self):
        return f"{self.id}: {self.position}"

    def tick(self):
        self.velocity = add(self.velocity, self.acceleration)
        self.position = add(self.position, self.velocity)


def setup_particles(deets):
    return [Particle(deet, i) for (i, deet) in enumerate(deets)]


def delete_collisions(particles):
    positions_count = Counter([p.position for p in particles])
    collision_positions = [position for (position, count) in positions_count.items() if count > 1]
    return [p for p in particles if p.position not in collision_positions]


def part_1(deets):
    particles = setup_particles(deets)
    particles.sort(key=lambda p: manhattan_distance(p.acceleration))
    return particles[0].id


def part_2(deets):
    particles = setup_particles(deets)
    for n in range(0, 1000):
        for p in particles:
            p.tick()
        particles = delete_collisions(particles)
    return len(particles)


print(part_1(puzzle_input))
timer(lambda: part_1(puzzle_input))

print(part_2(puzzle_input))
timer(lambda: part_2(puzzle_input))
