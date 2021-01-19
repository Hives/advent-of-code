import { readFile } from "../lib/reader.js";
import { sumArray } from "../lib/array.js";

function sumNumbers(string) {
    return sumArray(findNumbers(string));
}

function findNumbers(string) {
    return string.match(/-?\d+/g).map((s) => parseInt(s));
}

function deleteRed(string) {
    const redIndex = string.indexOf("red");

    let current = redIndex;
    let bracketDepth = 0;
    while (!(isOpenBracket(string[current]) && bracketDepth === 1)) {
        current--;
        if (isOpenBracket(string[current])) {
            bracketDepth++;
        }
        if (isCloseBracket(string[current])) {
            bracketDepth--;
        }
    }
    const previousBracketIndex = current;

    // if "red" is inside object, delete the object
    if (string[previousBracketIndex] === "{") {
        const closingBracketIndex = findMatchingBracket(
            string,
            previousBracketIndex
        );
        return deleteSection(string, previousBracketIndex, closingBracketIndex);
    }

    // if "red" is inside array, leave the array but delete the string "red"
    if (string[previousBracketIndex] === "[") {
        return deleteSection(string, redIndex - 1, redIndex + 3);
    }
}

function findMatchingBracket(string, openBracketIndex) {
    let bracketDepth = 1;
    let current = openBracketIndex;

    do {
        current++;
        if (string[current] === "{") {
            bracketDepth++;
        }
        if (string[current] === "}") {
            bracketDepth--;
        }
    } while (bracketDepth > 0);

    return current;
}

function deleteSection(string, start, end) {
    return string.slice(0, start) + '"snip"' + string.slice(end + 1);
}

function isOpenBracket(c) {
    return c === "{" || c === "[";
}

function isCloseBracket(c) {
    return c === "}" || c === "]";
}

const puzzleInput = readFile("day12.json");

console.log(`part 1: ${sumNumbers(puzzleInput)}`);

let s = puzzleInput
while (s.indexOf("red") !== -1) {
    s = deleteRed(s)
}
console.log(`part 2: ${sumNumbers(s)}`)
