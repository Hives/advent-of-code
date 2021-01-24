import { readStrings } from "../lib/reader.js";
import { pipe } from "../lib/fn.js";
import { map, filter } from "../lib/array.js";

console.log(go(readStrings("day16.txt"), checkSue1));
console.log(go(readStrings("day16.txt"), checkSue2));

function go(input, checkSue) {
    const validSue = {
        children: 3,
        cats: 7,
        samoyeds: 2,
        pomeranians: 3,
        akitas: 0,
        vizslas: 0,
        goldfish: 5,
        trees: 3,
        cars: 2,
        perfumes: 1,
    };

    return pipe(
        input,
        map(parseInput),
        map(checkSue(validSue)),
        filter((sue) => sue.isValid)
    );
}

function checkSue1(validSue) {
    return function (sue) {
        const isValid = Object.keys(validSue).reduce((acc, key) => {
            return checkKey(sue, validSue, key, equals) ? acc : false;
        }, true);

        return { ...sue, isValid };
    };
}

function checkSue2(validSue) {
    return function (sue) {
        const isValid = Object.keys(validSue).reduce((acc, key) => {
            if (key === "cats" || key === "trees") {
                return checkKey(sue, validSue, key, greaterThan) ? acc : false;
            }
            if (key === "pomeranians" || key === "goldfish") {
                return checkKey(sue, validSue, key, lessThan) ? acc : false;
            }
            return checkKey(sue, validSue, key, equals) ? acc : false;
        }, true);

        return { ...sue, isValid };
    };
}

function checkKey(sue, validSue, key, compare) {
    if (sue[key] === undefined) {
        return true;
    }
    return compare(sue[key], validSue[key]);
}

function equals(a, b) {
    return a === b;
}

function greaterThan(a, b) {
    return a > b;
}

function lessThan(a, b) {
    return a < b;
}

function parseInput(line) {
    const [, index, name1, value1, name2, value2, name3, value3] = line.match(
        /^Sue (\d+): (\w+): (\d+), (\w+): (\d+), (\w+): (\d+)$/
    );

    return {
        index: parseInt(index),
        [name1]: parseInt(value1),
        [name2]: parseInt(value2),
        [name3]: parseInt(value3),
    };
}
