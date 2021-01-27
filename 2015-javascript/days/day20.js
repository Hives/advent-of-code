import { pipe } from "../lib/fn.js";
import { range, sum, filter } from "../lib/array.js";

const puzzleInput = 34000000;

console.log(`part 1: ${doIt(puzzleInput, calculatePresentsPart1)}`);
console.log(`part 2: ${doIt(puzzleInput, calculatePresentsPart2)}`);

function doIt(limit, calculatePresents) {
    let doorNumber = 0;
    let presents;
    let largestPresents = 0;

    do {
        doorNumber++;
        presents = calculatePresents(doorNumber);

        if (presents > largestPresents) largestPresents = presents;

        if (doorNumber % 50000 === 0) {
            console.log(`${doorNumber}, largest so far: ${largestPresents}`);
        }
    } while (presents < limit);

    return doorNumber;
}

function calculatePresentsPart1(doorNumber) {
    return pipe(doorNumber, getFactors, sum, (n) => n * 10);
}

function calculatePresentsPart2(doorNumber) {
    return pipe(
        doorNumber,
        getFactors,
        filter((factor) => doorNumber / factor <= 50),
        sum,
        (n) => n * 11
    );
}

function getFactors(n) {
    const factors = [];

    const sqrt = Math.sqrt(n);

    range(1, sqrt).forEach((i) => {
        if (n % i === 0) {
            factors.push(i);
            factors.push(n / i);
        }
    });

    if (n % sqrt === 0) factors.push(sqrt);

    return factors;
}
