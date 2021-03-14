import { readFile } from "../lib/reader.js";
import { sum } from "../lib/array.js";

const puzzleInput = readFile("day12.json");

console.log(`part 1: ${sumNumbers(puzzleInput)}`);

let s = puzzleInput
while (s.indexOf("red") !== -1) {
    s = deleteRed(s)
}
console.log(`part 2: ${sumNumbers(s)}`)

function sumNumbers(string) {
    return sum(findNumbers(string));
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
        const closingBracketIndex = findMatchingBrace(
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

function findMatchingBrace(string, openBraceIndex) {
    let braceDepth = 1;
    let current = openBraceIndex;

    do {
        current++;
        if (string[current] === "{") {
            braceDepth++;
        }
        if (string[current] === "}") {
            braceDepth--;
        }
    } while (braceDepth > 0);

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
