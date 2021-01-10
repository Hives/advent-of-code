const { readStrings } = require('../lib/reader.js')

const input = readStrings("day06.txt")

const processInstruction = (instructionString, rules) => {
    const inst = parseInstruction(instructionString)

    for (let j = inst.start.y; j <= inst.end.y; j++) {
        for (let i = inst.start.x; i <= inst.end.x; i++) {
            rules[inst.operation](i, j)
        }
    }
}

const parseInstruction = string => {
    const [ , operation, x1, y1, x2, y2] =
        string.match(/^(.*) (\d+),(\d+) through (\d+),(\d+)$/)

    return {
        operation,
        start: {
            x: parseInt(x1),
            y: parseInt(y1)
        },
        end: {
            x: parseInt(x2),
            y: parseInt(y2)
        }
    }
}

const rules1 = {
    "turn on": (x, y) => grid[y][x] = 1,
    "turn off": (x, y) => grid[y][x] = 0,
    "toggle": (x, y) => grid[y][x] = (grid[y][x] + 1) % 2 
}

const rules2 = {
    "turn on": (x, y) => grid[y][x] = grid[y][x] + 1,
    "turn off": (x, y) => grid[y][x] = Math.max(grid[y][x] - 1, 0),
    "toggle": (x, y) => grid[y][x] = grid[y][x] + 2,
}

const sumArray = array => array.reduce((acc, n) => acc + n)
const sumGrid = () => sumArray(grid.flat())

const testInstructions = [
    "turn on 0,0 through 7,7",
    "turn off 3,5 through 7,7",
    "toggle 1,1 through 6,6",
    "toggle 4,3 through 5,5"
]

const exampleInstructions = [
    "turn on 0,0 through 999,999", 
    "toggle 0,0 through 999,0",
    "turn off 499,499 through 500,500"
]

let grid

const gridSize = 1000

grid = Array(gridSize).map(_ => Array(gridSize).fill(0))
input.forEach(i => processInstruction(i, rules1))
console.log(`${sumGrid()} - should be 543903`)

grid = Array(gridSize).map(_ => Array(gridSize).fill(0))
input.forEach(i => processInstruction(i, rules2))
console.log(`${sumGrid()} - should be 14687245`)

