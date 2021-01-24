import { readStrings } from "../lib/reader.js";
import { sum } from "../lib/array.js";

interface Point {
    x: number;
    y: number;
}

enum Operation {
    "turn on" = "TurnOn",
    "turn off" = "TurnOff",
    "toggle" = "Toggle",
}

type Rule = (p: Point) => void;

interface Rules {
    TurnOn: Rule;
    TurnOff: Rule;
    Toggle: Rule;
}

interface Instruction {
    operation: Operation;
    start: Point;
    end: Point;
}

const input: string[] = readStrings("day06.txt");

function processInstruction(instructionString: string, rules: Rules): void {
    const inst = parseInstruction(instructionString);

    for (let y = inst.start.y; y <= inst.end.y; y++) {
        for (let x = inst.start.x; x <= inst.end.x; x++) {
            rules[inst.operation]({ x, y });
        }
    }
}

function parseInstruction(instruction: string): Instruction {
    const [, operation, x1, y1, x2, y2] = instruction.match(
        /^(.*) (\d+),(\d+) through (\d+),(\d+)$/
    );

    return {
        operation: Operation[operation],
        start: {
            x: parseInt(x1),
            y: parseInt(y1),
        },
        end: {
            x: parseInt(x2),
            y: parseInt(y2),
        },
    };
}

function sumGrid(): number {
    return sum(grid.flat());
}

const testInstructions = [
    "turn on 0,0 through 7,7",
    "turn off 3,5 through 7,7",
    "toggle 1,1 through 6,6",
    "toggle 4,3 through 5,5",
];

const exampleInstructions = [
    "turn on 0,0 through 999,999",
    "toggle 0,0 through 999,0",
    "turn off 499,499 through 500,500",
];

const rules1: Rules = {
    TurnOn: (pnt) => (grid[pnt.y][pnt.x] = 1),
    TurnOff: (pnt) => (grid[pnt.y][pnt.x] = 0),
    Toggle: (pnt) => (grid[pnt.y][pnt.x] = (grid[pnt.y][pnt.x] + 1) % 2),
};

const rules2: Rules = {
    TurnOn: (pnt) => (grid[pnt.y][pnt.x] = grid[pnt.y][pnt.x] + 1),
    TurnOff: (pnt) =>
        (grid[pnt.y][pnt.x] = Math.max(grid[pnt.y][pnt.x] - 1, 0)),
    Toggle: (pnt) => (grid[pnt.y][pnt.x] = grid[pnt.y][pnt.x] + 2),
};

let grid: number[][];

const gridSize = 1000;

grid = [...Array(gridSize)].map((_) => Array(gridSize).fill(0));
input.forEach((i) => processInstruction(i, rules1));
console.log(`${sumGrid()} - should be 543903`);

grid = [...Array(gridSize)].map((_) => Array(gridSize).fill(0));
input.forEach((i) => processInstruction(i, rules2));
console.log(`${sumGrid()} - should be 14687245`);
