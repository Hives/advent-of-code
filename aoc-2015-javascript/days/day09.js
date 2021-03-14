import readStrings from "../lib/reader.js";
import { removeValueFromArray } from "../lib/array.js";

const input = readStrings("day09.txt");

function findAllJourneyDistances(input) {
    const locationMap = createMap(input);
    const locations = Object.keys(locationMap);
    const journeys = createJourneys(locations);

    const distances = journeys.map((journey) =>
        journey.slice(1, journey.length).reduce(
            (acc, location) => ({
                current: location,
                runningTotal:
                    acc.runningTotal + locationMap[acc.current][location],
            }),
            { current: journey[0], runningTotal: 0 }
        ).runningTotal
    );

    return distances
}

function createMap(input) {
    const map = {};

    input.forEach((description) => {
        const [, start, end, distance] = description.match(
            /(.+) to (.+) = (\d+)/
        );
        if (!map[start]) {
            map[start] = {};
        }
        if (!map[end]) {
            map[end] = {};
        }
        map[start][end] = parseInt(distance);
        map[end][start] = parseInt(distance);
    });

    return map;
}

function createJourneys(locations) {
    if (locations.length === 1) {
        return [locations];
    }

    return locations.flatMap((location) =>
        createJourneys(
            removeValueFromArray(locations, location)
        ).map((partialJourney) => [location, ...partialJourney])
    );
}

console.log(Math.min(...findAllJourneyDistances(input)))
console.log(Math.max(...findAllJourneyDistances(input)))
