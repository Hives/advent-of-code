import { readStrings } from "../lib/reader.js";
// very hacky/flukey solution to this one 😲

import { removeValuesFromArray, sum } from "../lib/array.js";

const puzzleInput = readStrings("day24.txt")
    .reverse()
    .map((s) => parseInt(s));

const exampleInput = [11, 10, 9, 8, 7, 5, 4, 3, 2, 1];

console.log(part1(puzzleInput));
console.log(part2(puzzleInput));

function part1(input) {
    const target = sum(input) / 3;

    const group1s = getNumbersThatAddTo(target, input).sort(
        (a, b) => a.length - b.length
    );

    const shortestGroup1s = group1s.filter(
        (list) => list.length === group1s[0].length
    );

    const shortestGroup1sSortedByQE = shortestGroup1s
        .map((group1) => ({ group1, qe: quantumEntanglement(group1) }))
        .sort((a, b) => a.qe - b.qe);

    return shortestGroup1sSortedByQE
}

function part2(input) {
    const target = sum(input) / 4;

    const group1s = getNumbersThatAddTo(target, input).sort(
        (a, b) => a.length - b.length
    );

    const shortestGroup1s = group1s.filter(
        (list) => list.length === group1s[0].length
    );

    const shortestGroup1sSortedByQE = shortestGroup1s
        .map((group1) => ({ group1, qe: quantumEntanglement(group1) }))
        .sort((a, b) => a.qe - b.qe);

    return shortestGroup1sSortedByQE
}

function getNumbersThatAddTo(total, numbers) {
    return numbers
        .filter((n) => n <= total)
        .flatMap((n) => {
            if (n === total) {
                return [[n]];
            }

            const foo = numbers.slice(
                numbers.findIndex((number) => number === n) + 1
            );

            const subLists = getNumbersThatAddTo(total - n, foo);

            return subLists.map((subList) => [n].concat(subList));
        });
}

function quantumEntanglement(numbers) {
    return numbers.reduce((acc, current) => current * acc, 1);
}
