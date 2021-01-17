const { readStrings } = require("../lib/reader.js");

function parseInstruction(input) {
    try {
        const [, operand1, operator, operand2, target] = input.match(
            /^(.+?)? ?(AND|OR|NOT|LSHIFT|RSHIFT+) (.+) -> (.+)$/
        );

        return {
            operation: {
                operand1: numberOrString(operand1),
                operator,
                operand2: numberOrString(operand2),
            },
            target,
        };
    } catch (e) {
        const [, value, target] = input.match(/^(.+) -> (.+)$/);

        return {
            operation: {
                operator: "EQUALS",
                operand2: numberOrString(value),
            },
            target,
        };
    }
}

function numberOrString(input) {
    const parsed = parseInt(input);
    if (isNaN(parsed)) {
        return input;
    }
    return parsed;
}

function isString(input) {
    return typeof input === "string";
}

function isNum(input) {
    return typeof input === "number";
}

function instructionsToMap(instructions) {
    const map = {};
    instructions.forEach((inst) => (map[inst.target] = inst.operation));
    return map;
}

function findA(instructions) {
    let ins = { ...instructions };

    do {
        const next = Object.keys(ins).reduce((acc, key) => {
            const instruction = ins[key];
            if (isNum(instruction)) {
                acc[key] = instruction;
                return acc;
            }
            acc[key] = evaluate(instruction, ins);
            return acc;
        }, {});
        ins = next;
    } while (!isNum(ins.a));

    return ins.a;
}

function evaluate(instruction, instructions) {
    const operand1 = isNum(instructions[instruction.operand1])
        ? instructions[instruction.operand1]
        : instruction.operand1;
    const operand2 = isNum(instructions[instruction.operand2])
        ? instructions[instruction.operand2]
        : instruction.operand2;

    if (isString(operand1) || isString(operand2)) {
        return {
            ...instruction,
            operand1,
            operand2,
        };
    }

    switch (instruction.operator) {
        case "EQUALS":
            return normalise(operand2);
        case "AND":
            return normalise(operand1 & operand2);
        case "OR":
            return normalise(operand1 | operand2);
        case "NOT":
            return normalise(~operand2);
        case "LSHIFT":
            return normalise(operand1 << operand2);
        case "RSHIFT":
            return normalise(operand1 >> operand2);
        default:
            return "nope";
    }
}

function sortKeysAndPrint(unordered) {
    const ordered = Object.keys(unordered)
        .sort()
        .reduce((ordered, key) => {
            ordered[key] = unordered[key];
            return ordered;
        }, {});
    console.log(ordered);
}

function normalise(x) {
    return ((x % 65536) + 65536) % 65536;
}

const input = readStrings("day07.txt");

const testInput = `123 -> x
456 -> y
x AND y -> d
x OR y -> e
x LSHIFT 2 -> f
y RSHIFT 2 -> g
NOT x -> h
NOT y -> i`.split("\n");

const instructionList = input.map((s) => parseInstruction(s));
const instructions = instructionsToMap(instructionList);

const part1 = findA(instructions);
console.log(part1);

const part2 = findA({ ...instructions, b: part1 });
console.log(part2);
