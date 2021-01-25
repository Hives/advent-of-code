import { readStringsToArrays } from "../lib/reader.js";
import { pipe } from "../lib/fn.js";

const on = "#";
const off = ".";

const puzzleInput = readStringsToArrays("day18.txt");

const exampleInput = [
    ".#.#.#",
    "...##.",
    "#....#",
    "..#...",
    "#.#..#",
    "####..",
].map((s) => s.split(""));

const standardRules = [
    (board, i, j) =>
        isOn(board[j][i]) &&
        [2, 3].includes(countOn(getNeighbours(board, i, j))),
    (board, i, j) =>
        isOff(board[j][i]) && countOn(getNeighbours(board, i, j)) === 3,
];

const cornersRule = (board, i, j) =>
    [0, board[j].length - 1].includes(i) && [0, board.length - 1].includes(j);

console.log(`part 1: ${part1(puzzleInput, 100)} (should be 814)`);
console.log(`part 2: ${part2(puzzleInput, 100)} (should be 924)`);

function part1(input, iterations) {
    return pipe(run(input, iterations, standardRules), countOn);
}

function part2(input, iterations) {
    const initial = turnOnCorners(input);
    return pipe(
        run(initial, iterations, [...standardRules, cornersRule]),
        countOn
    );
}

function run(initial, iterations, rules) {
    function go(board, iterations) {
        if (iterations == 0) {
            return board;
        }

        return go(applyRules(board, rules), iterations - 1);
    }

    return go(initial, iterations);
}

function applyRules(board, rules) {
    return board.map((row, rowIndex) => {
        return row.map((_, colIndex) => {
            return rules.reduce(
                (acc, rule) => acc || rule(board, rowIndex, colIndex),
                false
            )
                ? on
                : off;
        });
    });
}

function turnOnCorners(board) {
    return board.map((row, rowIndex) => {
        if (rowIndex === 0 || rowIndex === board.length - 1) {
            return [on].concat(row.slice(1, row.length - 1)).concat([on]);
        }
        return row;
    });
}

function countOn(board) {
    return board.flat().filter((cell) => isOn(cell)).length;
}

function getNeighbours(board, i, j) {
    const coOrds = [
        [i - 1, j - 1],
        [i, j - 1],
        [i + 1, j - 1],
        [i - 1, j],
        [i + 1, j],
        [i - 1, j + 1],
        [i, j + 1],
        [i + 1, j + 1],
    ];

    return coOrds.map((c) => (board[c[1]] ? board[c[1]][c[0]] : undefined));
}

function isOn(cell) {
    return cell === on;
}

function isOff(cell) {
    return !isOn(cell);
}
