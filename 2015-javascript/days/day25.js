import { pipe } from "../lib/fn.js";

// row 3010
// column 3019

console.log(part1())

function part1() {
    const start = {
        col: 1,
        row: 1,
        code: 20151125
    }

    let state = start
    while (state.row !== 3010 || state.col !== 3019) {
        state = next(state)
    }

    return state
}


function next({code, row, col}) {
    const multiplier = 252533
    const divider = 33554393

    return {
        col: row === 1 ? 1 : col + 1,
        row: row === 1 ? col + 1 : row - 1,
        code: (code * multiplier % divider)
    }
}
