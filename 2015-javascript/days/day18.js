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

const rules = [
    (cell, neighbours) =>
        (isOn(cell) && areOn(neighbours, 2)) || areOn(neighbours, 3),
    (cell, neighbours) => isOff(cell) && areOn(neighbours, 3),
];

console.log(pipe(run(puzzleInput, 100, rules), countOn));
console.log(pipe(run(puzzleInput, 100, rules, true), countOn));

function run(
    initial,
    iterations,
    rules,
    cornersAlwaysOn = false
) {
    function turnOnCornersIfRequired(board) {
        return cornersAlwaysOn ? turnOnCorners(board) : board;
    }

    function go(board, iterations) {
        if (iterations == 0) {
            return board;
        }

        const nextBoard = pipe(oneStep(board, rules), turnOnCornersIfRequired);

        return go(nextBoard, iterations - 1);
    }

    return go(turnOnCornersIfRequired(initial), iterations)
}

function oneStep(board, rules) {
    return board.map((row, rowIndex) => {
        return row.map((cell, colIndex) => {
            const neighbours = getNeighbours(board, colIndex, rowIndex);
            return rules.reduce(
                (acc, rule) => acc || rule(cell, neighbours),
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
    return board.flat().filter(cell => isOn(cell)).length;
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

function areOn(cells, number) {
    const numOnCells = cells.reduce(
        (acc, cell) => (isOn(cell) ? acc + 1 : acc),
        0
    );
    return numOnCells === number;
}
