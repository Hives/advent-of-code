import { readStrings } from "../lib/reader.js";
import { flatMap } from "../lib/array.js";
import { pipe } from "../lib/fn.js";

const medicineMolecue = "CRnSiRnCaPTiMgYCaPTiRnFArSiThFArCaSiThSiThPBCaCaSiRnSiRnTiTiMgArPBCaPMgYPTiRnFArFArCaSiRnBPMgArPRnCaPTiRnFArCaSiThCaCaFArPBCaCaPTiTiRnFArCaSiRnSiAlYSiThRnFArArCaSiRnBFArCaCaSiRnSiThCaCaCaFYCaPTiBCaSiThCaSiThPMgArSiRnCaPBFYCaCaFArCaCaCaCaSiThCaSiRnPRnFArPBSiThPRnFArSiRnMgArCaFYFArCaSiRnSiAlArTiTiTiTiTiTiTiRnPMgArPTiTiTiBSiRnSiAlArTiTiRnPMgArCaFYBPBPTiRnSiRnMgArSiThCaFArCaSiThFArPRnFArCaSiRnTiBSiThSiRnSiAlYCaFArPRnFArSiThCaFArCaCaSiThCaCaCaSiRnPRnCaFArFYPMgArCaPBCaPBSiRnFYPBCaFArCaSiAl";

const puzzleInput = readStrings("day19.txt");

console.log(part1(puzzleInput));

function part1(input) {
    return pipe(
        input,
        createMap,
        flatMap((rule) => getAllSingleReplacements(medicineMolecue, rule)),
        (strings) => new Set(strings),
        (set) => set.size
    );
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
