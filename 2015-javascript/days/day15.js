import { max, zip, flip, sumArray, range, map, filter, reduce } from "../lib/array.js";
import { pipe } from "../lib/fn.js";

const exampleInput = [
    "Butterscotch: capacity -1, durability -2, flavor 6, texture 3, calories 8",
    "Cinnamon: capacity 2, durability 3, flavor -2, texture -1, calories 3",
];

const puzzleInput = [
    "Frosting: capacity 4, durability -2, flavor 0, texture 0, calories 5",
    "Candy: capacity 0, durability 5, flavor -1, texture 0, calories 8",
    "Butterscotch: capacity -1, durability 0, flavor 5, texture 0, calories 6",
    "Sugar: capacity 0, durability 0, flavor -2, texture 2, calories 1",
];

const ingredients = puzzleInput.map(parseLine);

console.log(`part 1: ${part1(ingredients)}`);
console.log(`part 2: ${part2(ingredients)}`);

function part1(ingredients) {
    return pipe(
        allWaysOfSummingTo(100, ingredients.length),
        map((quantities) => evaluateCookie(ingredients, quantities)),
        map((cookie) => cookie.taste),
        max
    );
}

function part2(ingredients) {
    return pipe(
        allWaysOfSummingTo(100, ingredients.length),
        map((quantities) => evaluateCookie(ingredients, quantities)),
        filter((cookie) => cookie.calories === 500),
        map((cookie) => cookie.taste),
        max
    );
}

function allWaysOfSummingTo(total, n) {
    if (n === 1) {
        return [[total]];
    }
    return range(0, total + 1).flatMap((a) =>
        allWaysOfSummingTo(total - a, n - 1).map((sum) => [a].concat(sum))
    );
}

function evaluateCookie(ingredients, quantities) {
    const ingredientsAndQuantities = zip(ingredients, quantities);

    return {
        taste: evaluateCookieTaste(ingredientsAndQuantities),
        calories: evaluateCookieCalories(ingredientsAndQuantities),
    };
}

function evaluateCookieTaste(ingredientsAndQuantities) {
    return pipe(
        ingredientsAndQuantities,
        map(([ingredient, quantity]) =>
                ingredient.tasteComponents.map((n) => n * quantity)
            ),
        flip,
        map(sumArray),
        map((n) => Math.max(n, 0)),
        reduce((acc, current) => acc * current, 1)
    );
}

function evaluateCookieCalories(ingredientsAndQuantities) {
    return ingredientsAndQuantities.reduce(
        (acc, [ingredient, quantity]) => acc + ingredient.calories * quantity,
        0
    );
}

function parseLine(line) {
    const [
        ,
        name,
        capacity,
        durability,
        flavor,
        texture,
        calories,
    ] = line.match(
        /^(\w+): capacity (-?\d+), durability (-?\d+), flavor (-?\d+), texture (-?\d+), calories (-?\d+)$/
    );

    return {
        tasteComponents: [capacity, durability, flavor, texture],
        calories,
    };
}
