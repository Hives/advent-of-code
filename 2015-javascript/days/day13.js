import { readStrings } from "../lib/reader.js";
import { permutations } from "../lib/array.js";

const exampleInput = readStrings("day13-example.txt");
const puzzleInput = readStrings("day13.txt");

console.log(`part 1: ${findOptimalArrangement(puzzleInput)} (should be 709)`);
console.log(
    `part 2: ${findOptimalArrangementWithMe(puzzleInput)} (should be 668)`
);

function findOptimalArrangement(input) {
    const map = createHappinessMap(input);
    return findOptimalArrangementFromMap(map);
}

function findOptimalArrangementWithMe(input) {
    const map = addMeIntoMap(createHappinessMap(input));
    return findOptimalArrangementFromMap(map);
}

function findOptimalArrangementFromMap(map) {
    const arrangements = permutations(Object.keys(map));
    const happinessScores = arrangements.map((arrangement) =>
        evaluateArrangement(arrangement, map)
    );
    return happinessScores.reduce(
        (acc, current) => (current > acc ? current : acc),
        0
    );
}

function addMeIntoMap(map) {
    return Object.keys(map).reduce(
        (acc, sitter) => ({ ...acc, [sitter]: { ...map[sitter], Me: 0 } }),
        {
            Me: Object.keys(map).reduce(
                (acc, neighbour) => ({ ...acc, [neighbour]: 0 }),
                {}
            ),
        }
    );
}

function evaluateArrangement(punters, map) {
    return punters
        .map((sitter, index) => ({
            sitter,
            neighbour1: punters[(index - 1 + punters.length) % punters.length],
            neighbour2: punters[(index + 1 + punters.length) % punters.length],
        }))
        .reduce(
            (acc, { sitter, neighbour1, neighbour2 }) =>
                acc + map[sitter][neighbour1] + map[sitter][neighbour2],
            0
        );
}

function createHappinessMap(lines) {
    const relationships = lines.map(parseLine);

    const punters = Array.from(
        new Set([...relationships.map((r) => r.sitter)])
    );

    return punters.reduce(
        (acc, sitter) => ({
            ...acc,
            [sitter]: punters
                .filter((punter) => punter !== sitter)
                .reduce(
                    (acc2, neighbour) => ({
                        ...acc2,
                        [neighbour]: relationships.find(
                            (r) =>
                                r.sitter === sitter && r.neighbour === neighbour
                        ).happiness,
                    }),
                    {}
                ),
        }),
        {}
    );
}

function parseLine(line) {
    const [, sitter, gainOrLose, happiness, neighbour] = line.match(
        /^(\w+) would (\w+) (\d+) happiness units by sitting next to (\w+).$/
    );

    return {
        sitter,
        neighbour,
        happiness: parseInt(happiness) * (gainOrLose === "gain" ? 1 : -1),
    };
}
