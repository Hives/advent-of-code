import { readStrings } from "../lib/reader.js";
import { flatMap } from "../lib/array.js";
import { pipe } from "../lib/fn.js";

const medicineMolecule =
    "CRnSiRnCaPTiMgYCaPTiRnFArSiThFArCaSiThSiThPBCaCaSiRnSiRnTiTiMgArPBCaPMgYPTiRnFArFArCaSiRnBPMgArPRnCaPTiRnFArCaSiThCaCaFArPBCaCaPTiTiRnFArCaSiRnSiAlYSiThRnFArArCaSiRnBFArCaCaSiRnSiThCaCaCaFYCaPTiBCaSiThCaSiThPMgArSiRnCaPBFYCaCaFArCaCaCaCaSiThCaSiRnPRnFArPBSiThPRnFArSiRnMgArCaFYFArCaSiRnSiAlArTiTiTiTiTiTiTiRnPMgArPTiTiTiBSiRnSiAlArTiTiRnPMgArCaFYBPBPTiRnSiRnMgArSiThCaFArCaSiThFArPRnFArCaSiRnTiBSiThSiRnSiAlYCaFArPRnFArSiThCaFArCaCaSiThCaCaCaSiRnPRnCaFArFYPMgArCaPBCaPBSiRnFYPBCaFArCaSiAl";

const puzzleInput = readStrings("day19.txt");

console.log(part1(puzzleInput));
console.log(part2(puzzleInput));

function part1(input) {
    return pipe(
        input,
        createMap,
        flatMap((rule) => getAllSingleReplacements(medicineMolecule, rule)),
        (strings) => new Set(strings),
        (set) => set.size
    );
}

function part2(input) {
    function go(string, rules, iterations = 0) {
        if (string.length === 1) {
            return {
                string,
                iterations
            }
        }

        const nextString = reverseReplace(string, rules) 

        return go(nextString, rules, iterations + 1)
    }

    return go(medicineMolecule, createMap(input))
}

function reverseReplace(string, rules) {
    const { to, from } = rules.find((rule) =>
        string.includes(rule.to)
    );

    return string.replace(to, from);
}

function getAllSingleReplacements(string, rule) {
    const rx = RegExp(rule.from, "g");
    const matches = string.matchAll(rx);
    return Array.from(matches, (m) => {
        const prefix = string.slice(0, m.index);
        const suffix = string.slice(m.index + rule.from.length, string.length);
        return prefix + rule.to + suffix;
    });
}

function createMap(input) {
    return input.map(parseLine);
}

function parseLine(line) {
    const [, from, to] = line.match(/(\w+) => (\w+)/);
    return { from, to };
}
