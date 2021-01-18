import { readStrings } from "../lib/reader.js";

const input = readStrings("day08.txt");
const exampleInput = ['""', '"abc"', '"aaa\\"aaa"', '"\\x27"'];

function codeLength(string) {
    return string.length;
}

function memLength(string) {
    const replacedString = [
        replaceHex,
        replaceEscapedQuote,
        replaceEscapedBackslash,
        removeSurroundingQuotes,
    ].reduce((acc, replacer) => replacer(acc), string);
    return replacedString.length;
}

function replaceHex(string) {
    const replaceChar = "!";
    return string.replace(/\\x[0-9|a-f]{2}/g, replaceChar);
}

function replaceEscapedQuote(string) {
    const replaceChar = '"';
    return string.replace(/\\"/g, replaceChar);
}

function replaceEscapedBackslash(string) {
    const replaceChar = "!";
    return string.replace(/\\\\/g, replaceChar);
}

function removeSurroundingQuotes(string) {
    return string.replace(/^"/, "").replace(/"$/, "");
}

const part1 = input.reduce(
    (acc, string) => acc + codeLength(string) - memLength(string),
    0
);

console.log(`part 1: ${part1} (should be 1371)`);

function reencodedLength(string) {
    const escapedString = [
        escapeBackslashes,
        escapeQuotes,
        wrapWithQuotes,
    ].reduce((acc, replacer) => replacer(acc), string);
    return escapedString.length;
}

function escapeQuotes(string) {
    return string.replace(/"/g, '\\"');
}

function escapeBackslashes(string) {
    return string.replace(/\\/g, "\\\\");
}

function wrapWithQuotes(string) {
    return `"${string}"`;
}

const part2 = input.reduce(
    (acc, string) => acc + reencodedLength(string) - codeLength(string),
    0
);

console.log(`part 2: ${part2} (should be 2117)`);
