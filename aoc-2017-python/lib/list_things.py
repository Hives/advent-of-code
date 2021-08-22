from functools import reduce


def flat_map(f, xs):
    return reduce(lambda acc, current: acc + current, map(f, xs))


def flatten(xs):
    return [item for sublist in xs for item in sublist]
