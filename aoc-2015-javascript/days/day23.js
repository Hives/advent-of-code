import { readStrings } from "../lib/reader.js";

const puzzleInput = readStrings("day23.txt");
const exampleInput = ["inc a", "jio a, +2", "tpl a", "inc a"];

part1(puzzleInput);
part2(puzzleInput);

function part1(program) {
    const initialState = {
        registers: {
            a: 0,
            b: 0,
        },
        position: 0,
    };

    run(program, initialState);
}

function part2(program) {
    const initialState = {
        registers: {
            a: 1,
            b: 0,
        },
        position: 0,
    };

    run(program, initialState);
}

function run(program, initialState) {
    const go = (state) => {
        if (state.position >= program.length) return state;
        return go(doOne(state, program));
    };

    return go(initialState);
}

function doOne(state, program) {
    const { opcode, p1, p2 } = parse(program[state.position]);
    switch (opcode) {
        case "hlf":
            return half(p1, state);
        case "tpl":
            return triple(p1, state);
        case "inc":
            return increment(p1, state);
        case "jmp":
            return jump(p1, state);
        case "jie":
            return jumpIfEven(p1, p2, state);
        case "jio":
            return jumpIfOne(p1, p2, state);
        default:
            throw new Error(`Unrecognised opcode: ${opcode}`);
    }
}

function parse(string) {
    const [, opcode, p1, p2] = string.match(
        /^(\w*) (a|b|(?:\+|-)\d+)(?:\, (.*))?/
    );

    return {
        opcode,
        p1,
        p2,
    };
}

function half(r, { registers, position }) {
    return {
        registers: {
            ...registers,
            [r]: Math.floor(registers[r] / 2),
        },
        position: position + 1,
    };
}

function triple(r, { registers, position }) {
    return {
        registers: {
            ...registers,
            [r]: registers[r] * 3,
        },
        position: position + 1,
    };
}

function increment(r, { registers, position }) {
    return {
        registers: {
            ...registers,
            [r]: registers[r] + 1,
        },
        position: position + 1,
    };
}

function jump(offset, { registers, position }) {
    return {
        registers: {
            ...registers,
        },
        position: position + parseInt(offset),
    };
}

function jumpIfEven(r, offset, { registers, position }) {
    return {
        registers: {
            ...registers,
        },
        position:
            registers[r] % 2 === 0 ? position + parseInt(offset) : position + 1,
    };
}

function jumpIfOne(r, offset, { registers, position }) {
    return {
        registers: {
            ...registers,
        },
        position:
            registers[r] === 1 ? position + parseInt(offset) : position + 1,
    };
}
