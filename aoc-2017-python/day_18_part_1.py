#! /usr/bin/env pypy

test_input = """set a 1
add a 2
mul a a
mod a 5
snd a
set a 0
rcv a
jgz a -1
set a 1
jgz a -2""".split("\n")
puzzle_input = open("inputs/day_18.txt").read().strip().split("\n")


class Processor:
    def __init__(self, instructions):
        self.state = {"recover": False}
        self.position = 0
        self.instructions = instructions

    def value_or_register(self, x):
        try:
            return int(x)
        except ValueError:
            return self.state.get(x, 0)

    def snd(self, x):
        self.state["sound"] = self.value_or_register(x)
        self.position += 1

    def set(self, x, y):
        self.state[x] = self.value_or_register(y)
        self.position += 1

    def add(self, x, y):
        current = self.state.get(x, 0)
        self.state[x] = current + self.value_or_register(y)
        self.position += 1

    def mul(self, x, y):
        current = self.state.get(x, 0)
        self.state[x] = current * self.value_or_register(y)
        self.position += 1

    def mod(self, x, y):
        current = self.state.get(x, 0)
        self.state[x] = current % self.value_or_register(y)
        self.position += 1

    def rcv(self, x):
        if self.value_or_register(x) != 0:
            self.state["recover"] = True
        else:
            self.position += 1

    def jgz(self, x, y):
        if self.value_or_register(x) > 0:
            self.position += self.value_or_register(y)
        else:
            self.position += 1

    def go(self):
        while self.position < len(self.instructions):
            if self.state["recover"]:
                return self.state["sound"]
            instruction = self.instructions[self.position][0:3]
            params = self.instructions[self.position][4:].split(" ")
            {
                "snd": lambda: self.snd(params[0]),
                "set": lambda: self.set(params[0], params[1]),
                "add": lambda: self.add(params[0], params[1]),
                "mul": lambda: self.mul(params[0], params[1]),
                "mod": lambda: self.mod(params[0], params[1]),
                "rcv": lambda: self.rcv(params[0]),
                "jgz": lambda: self.jgz(params[0], params[1]),
            }[instruction]()


print(Processor(puzzle_input).go())
