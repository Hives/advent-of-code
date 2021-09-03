#! /usr/bin/env pypy
import math

puzzle_input = open("inputs/day_23.txt").read().strip().split("\n")


class Registers:
    def __init__(self):
        self.registers = {}

    def get(self, register):
        return self.registers.get(register, 0)

    def set(self, register, value):
        self.registers[register] = value


class Processor:
    def __init__(self, instructions, debug):
        self.registers = Registers()
        self.position = 0
        self.instructions = instructions
        self.mul_count = 0
        if not debug:
            self.registers.set("a", 1)

    def value_or_register(self, x):
        try:
            return int(x)
        except ValueError:
            return self.registers.get(x)

    def set(self, x, y):
        self.registers.set(x, self.value_or_register(y))
        self.position += 1

    def sub(self, x, y):
        self.registers.set(x, self.registers.get(x) - self.value_or_register(y))
        self.position += 1

    def mul(self, x, y):
        self.registers.set(x, self.registers.get(x) * self.value_or_register(y))
        self.position += 1
        self.mul_count += 1

    def jnz(self, x, y):
        if self.value_or_register(x) != 0:
            self.position += self.value_or_register(y)
        else:
            self.position += 1

    def tick(self):
        instruction = self.instructions[self.position][0:3]
        params = self.instructions[self.position][4:].split(" ")
        {
            "set": lambda: self.set(params[0], params[1]),
            "sub": lambda: self.sub(params[0], params[1]),
            "mul": lambda: self.mul(params[0], params[1]),
            "jnz": lambda: self.jnz(params[0], params[1]),
        }[instruction]()

    def print_registers(self):
        print(", ".join(
            [str(self.position + 1)] + [str(self.registers.get(r)) for r in ["a", "b", "c", "d", "e", "f", "g", "h"]]))

    def go(self):
        while self.position < len(self.instructions):
            self.tick()


# this is a translation of the original program into python, with two optimisations (see comments)
# the program looks for compound (non-prime) numbers in the set [107900 + 17n for n in [0, 1000]] by trial and error.
# b takes the values of that set, then it increments f and d in a nested loop and increments h if it finds an
# f and d such that f * d = b, hence b is compound.
# f and d such that f * d = b, hence b is compound.
# the two optimisations made allow the program to run in a sensible time.
def part_2():
    b = 107900
    c = 124900

    h = 0
    outer_loop_count = 0

    while True:
        outer_loop_count += 1
        print(f"outer loop: {outer_loop_count}, b={b}")
        # in here, b takes the values 107900 + 17n for n in [0, 1000]
        f = 1
        d = 2
        while True:
            e = 2
            while True:
                if (d * e) == b:
                    print(f"{d} * {e} == {b}")
                    f = 0
                e += 1
                if e > b / d:  # in the original, this condition is e == b
                    break
            d += 1
            if d > math.sqrt(b):  # in the original, this condition is d == b
                break

        if f == 0:
            h += 1

        if b == c:
            break
        else:
            b += 17  # have to do this 1,000 times to escape

    return h


def part_1(deets):
    processor = Processor(deets, True)
    processor.go()
    return processor.mul_count


print(part_1(puzzle_input))
print(part_2())
