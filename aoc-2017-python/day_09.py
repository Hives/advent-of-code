#! /usr/bin/python3

puzzle_input = list(open("inputs/day_09.txt").read().strip())

examples = [
    ("{}", 1),
    ("{{{}}}", 6),
    ("{{},{}}", 5),
    ("{{{},{},{{}}}}", 16),
    ("{<a>,<a>,<a>,<a>}", 1),
    ("{{<ab>},{<ab>},{<ab>},{<ab>}}", 9),
    ("{{<!!>},{<!!>},{<!!>},{<!!>}}", 9),
    ("{{<a!>},{<a!>},{<a!>},{<ab>}}", 3)
]


class StreamProcessor:
    def __init__(self, stream):
        self.stream = stream
        self.ignoreNext = False
        self.inGarbage = False
        self.index = 0
        self.depth = 0
        self.score = 0
        self.garbageCount = 0

    def next(self):
        current = self.stream[self.index]

        if self.inGarbage:
            if self.ignoreNext:
                self.ignoreNext = False
            elif current == "!":
                self.ignoreNext = True
            elif current == ">":
                self.inGarbage = False
            else:
                self.garbageCount += 1
        else:
            if current == "{":
                self.depth += 1
                self.score += self.depth
            elif current == "}":
                self.depth -= 1
            elif current == "<":
                self.inGarbage = True

        self.index += 1

    def process(self):
        while self.index < len(self.stream):
            self.next()


def test():
    print("testin:\n")
    for (stream, correct_answer) in examples:
        processor = StreamProcessor(stream)
        processor.process()
        score = processor.score
        outcome = "SUCCESS" if score == correct_answer else "FAILURE"
        print(f"{stream} produced {score}. correct answer: {correct_answer}. {outcome}")
    print("\n\n")


def part_1_and_2(stream):
    processor = StreamProcessor(stream)
    processor.process()
    print(f"part 1 (score): {processor.score}")
    print(f"part 2 (garbage count): {processor.garbageCount}")


test()

part_1_and_2(puzzle_input)
