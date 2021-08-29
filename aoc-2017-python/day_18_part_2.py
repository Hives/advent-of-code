#! /usr/bin/env pypy
from lib.timer import timer

test_input = """snd 1
snd 2
snd p
rcv a
rcv b
rcv c
rcv d""".split("\n")
puzzle_input = open("inputs/day_18.txt").read().strip().split("\n")


class Registers:
    def __init__(self):
        self.registers = {}

    def get(self, register):
        return self.registers.get(register, 0)

    def set(self, register, value):
        self.registers[register] = value


class Processor:
    def __init__(self, instructions, program_id):
        self.registers = Registers()
        self.registers.set("p", program_id)
        self.position = 0
        self.instructions = instructions
        self.message_queue = []
        self.partner = None
        self.waiting = False
        self.terminated = False
        self.send_count = 0

    def set_partner(self, partner):
        self.partner = partner

    def enqueue_message(self, message):
        self.message_queue.append(message)

    def is_running(self):
        return not self.terminated

    def value_or_register(self, x):
        try:
            return int(x)
        except ValueError:
            return self.registers.get(x)

    def snd(self, x):
        self.partner.enqueue_message(self.value_or_register(x))
        self.send_count += 1
        self.position += 1

    def set(self, x, y):
        self.registers.set(x, self.value_or_register(y))
        self.position += 1

    def add(self, x, y):
        self.registers.set(x, self.registers.get(x) + self.value_or_register(y))
        self.position += 1

    def mul(self, x, y):
        self.registers.set(x, self.registers.get(x) * self.value_or_register(y))
        self.position += 1

    def mod(self, x, y):
        self.registers.set(x, self.registers.get(x) % self.value_or_register(y))
        self.position += 1

    def rcv(self, x):
        if self.message_queue:
            self.registers.set(x, self.message_queue.pop(0))
            self.position += 1
        else:
            self.waiting = True

    def jgz(self, x, y):
        if self.value_or_register(x) > 0:
            self.position += self.value_or_register(y)
        else:
            self.position += 1

    def tick(self):
        if self.waiting and self.partner.waiting:
            self.terminated = True
        if not self.terminated:
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
            if self.position > len(self.instructions):
                self.terminated = True


def part_2(instructions):
    program_0 = Processor(instructions, 0)
    program_1 = Processor(instructions, 1)
    program_0.set_partner(program_1)
    program_1.set_partner(program_0)

    while program_0.is_running() and program_1.is_running():
        program_0.tick()
        program_1.tick()

    return program_1.send_count


print(part_2(puzzle_input))
timer(lambda: part_2(puzzle_input))
