from timeit import default_timer


def timer(f):
    # warm up
    for n in range(0, 10):
        f()

    repetitions = 100
    start = default_timer()
    for n in range(0, repetitions):
        f()
    end = default_timer()

    print(f"average of {(end - start)/repetitions} seconds")