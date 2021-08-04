#! /usr/bin/python3

test_input = [0, 2, 7, 0]
puzzle_input = [4, 1, 15, 12, 0, 9, 9, 5, 5, 8, 7, 3, 14, 5, 12, 3]


def reallocate_memory_until_loop(initial):
    state = initial.copy()
    previous_states = []

    while not (state in previous_states):
        previous_states.append(state.copy())
        blocks = max(state)
        index = state.index(blocks)
        state[index] = 0
        while blocks > 0:
            index = (index + 1) % len(state)
            state[index] += 1
            blocks -= 1

    previous_states.append(state.copy())
    return previous_states


def part_1(history):
    return len(history) - 1


def part_2(history):
    return len(history) - history.index(history[-1]) - 1


history_until_loop = reallocate_memory_until_loop(puzzle_input)
print(part_1(history_until_loop))
print(part_2(history_until_loop))
