import { readStrings } from "../lib/reader.js";

const exampleInput = [
    "Comet can fly 14 km/s for 10 seconds, but then must rest for 127 seconds.",
    "Dancer can fly 16 km/s for 11 seconds, but then must rest for 162 seconds.",
];

const puzzleInput = readStrings("day14.txt");

console.log(`part 1: ${part1(puzzleInput, 2503)}`);

console.log(`part 2: ${part2(puzzleInput, 2503)}`);

function part1(input, time) {
    return Math.max(...input.map(parseLine).map(distanceTravelledIn(time)));
}

function part2(input, endTime) {
    function go(raceState) {
        if (raceState.time === endTime) {
            return raceState;
        }
        return go(incrementOneSecond(raceState, rules));
    }

    const rules = parseInput(input);

    const initialState = {
        time: 0,
        status: Object.keys(rules).reduce(
            (acc, reindeer) => ({
                ...acc,
                [reindeer]: { distance: 0, points: 0 },
            }),
            {}
        ),
    };

    const finalStatus = go(initialState).status;

    return Math.max(
        ...Object.values(finalStatus).map((reindeer) => reindeer.points)
    );
}

function incrementOneSecond({ time, status }, rules) {
    const newDistances = Object.keys(rules).reduce(
        (acc, reindeer) => ({
            ...acc,
            [reindeer]:
                status[reindeer].distance +
                distanceTravelledInASecond(rules[reindeer], time),
        }),
        {}
    );

    const maxDistance = Math.max(...Object.values(newDistances));
    const winners = Object.keys(newDistances).filter(
        (reindeer) => newDistances[reindeer] === maxDistance
    );

    const newStatus = Object.keys(status).reduce(
        (acc, reindeer) => ({
            ...acc,
            [reindeer]: {
                distance: newDistances[reindeer],
                points:
                    status[reindeer].points +
                    (winners.includes(reindeer) ? 1 : 0),
            },
        }),
        {}
    );

    return { time: time + 1, status: newStatus };
}

function distanceTravelledInASecond({ speed, flyTime, restTime }, t) {
    const period = flyTime + restTime;
    return t % period < flyTime ? speed : 0;
}

function distanceTravelledIn(duration) {
    return function ({ speed, flyTime, restTime }) {
        const period = flyTime + restTime;
        const completeIterations = Math.floor(duration / period);
        const remainder = duration % period;
        return (
            completeIterations * flyTime * speed +
            Math.min(flyTime, remainder) * speed
        );
    };
}

function parseInput(input) {
    return input.map(parseLine).reduce(
        (acc, { reindeer, speed, flyTime, restTime }) => ({
            ...acc,
            [reindeer]: { speed, flyTime, restTime },
        }),
        {}
    );
}

function parseLine(line) {
    const [, reindeer, speed, flyTime, restTime] = line.match(
        /^(\w+) can fly (\d+) km\/s for (\d+) seconds, but then must rest for (\d+) seconds.$/
    );

    return {
        reindeer,
        speed: parseInt(speed),
        flyTime: parseInt(flyTime),
        restTime: parseInt(restTime),
    };
}
