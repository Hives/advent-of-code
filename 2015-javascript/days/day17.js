import { pipe } from "../lib/fn.js";
import { sort, reverse, sum, map } from "../lib/array.js";

const puzzleInput = [
    11,
    30,
    47,
    31,
    32,
    36,
    3,
    1,
    5,
    3,
    32,
    36,
    15,
    11,
    46,
    26,
    28,
    1,
    19,
    3,
];

const exampleInput = [20, 15, 10, 5, 5];

console.log(part1(puzzleInput, 150));
console.log(part2(puzzleInput, 150));

function part1(input, quantity) {
    return pipe(
        input,
        sort,
        reverse,
        (containers) => fitQuantityIntoContainers(quantity, containers),
        (array) => array.length
    );
}

function part2(input, quantity) {
    return pipe(
        input,
        sort,
        reverse,
        (containers) => fitQuantityIntoContainers(quantity, containers),
        filterMinimumLength,
        (array) => array.length
    );
}

function filterMinimumLength(arrays) {
    const minimumLength = arrays
        .slice(1, arrays.length)
        .reduce(
            (acc, current) => (current.length < acc ? current.length : acc),
            arrays[0].length
        );

    return arrays.filter((array) => array.length === minimumLength);
}

function fitQuantityIntoContainers(quantity, containers) {
    if (sum(containers) == quantity) {
        return [containers];
    }
    if (sum(containers) < quantity) {
        return [];
    }

    const notIncludingFirst = fitQuantityIntoContainers(
        quantity,
        containers.slice(1, containers.length)
    );

    const includingFirst =
        containers[0] <= quantity
            ? fitQuantityIntoContainers(
                  quantity - containers[0],
                  containers.slice(1, containers.length)
              ).map((subList) => [containers[0]].concat(subList))
            : [];

    return includingFirst.concat(notIncludingFirst);
}
