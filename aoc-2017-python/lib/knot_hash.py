import operator
from functools import reduce


# created for day 10, used in day 14

def rotate(l, n):
    n_normalised = n % len(l)
    return l[n_normalised:] + l[:n_normalised]


def calculate_sparse_hash(lengths, size=256, rounds=64):
    joined_lengths = []

    for i in range(0, rounds):
        joined_lengths += lengths

    offset = 0
    skip_size = 0
    state = list(range(size))

    for length in joined_lengths:
        selected = state[0:length]
        remainder = state[length:]
        selected.reverse()

        state = rotate(selected + remainder, length + skip_size)
        offset = (offset + length + skip_size) % size
        skip_size += 1

    return rotate(state, -offset)


def int_to_hex(n):
    h = str(hex(n))[2:]
    if len(h) == 1:
        h = "0" + h
    return h


def calculate_dense_hash(numbers):
    chunked = [numbers[n * 16:(n + 1) * 16] for n in range(0, 16)]
    dense_hash = [reduce(operator.xor, chunk) for chunk in chunked]
    return "".join([int_to_hex(n) for n in dense_hash])


def knot_hash(string):
    """
    Calculates the knot hash for a given string, as defined in Day 10
    """
    salt = [17, 31, 73, 47, 23]
    lengths = [ord(char) for char in list(string)] + salt

    sparse_hash = calculate_sparse_hash(lengths)
    return calculate_dense_hash(sparse_hash)
