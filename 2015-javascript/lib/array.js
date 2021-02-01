export function removeValueFromArray(array, value) {
    const index = array.findIndex((element) => element === value);
    if (index === -1) {
        throw `Could not find value ${value} in array`;
    }
    return [...array.slice(0, index), ...array.slice(index + 1, array.size)];
}

export function removeValuesFromArray(array, values) {
    return values.reduce((acc, current) => removeValueFromArray(acc, current), [
        ...array,
    ]);
}

export function permutations(array) {
    if (array.length === 1) {
        return array;
    }
    return array.flatMap((selected) => {
        return permutations(array.filter((it) => it !== selected)).map(
            (subList) => {
                return [selected].concat(subList);
            }
        );
    });
}

export function zip(array1, array2) {
    const length = Math.min(array1.length, array2.length);
    const output = [];
    for (let i = 0; i < length; i++) {
        output.push([array1[i], array2[i]]);
    }
    return output;
}

export function flip(twoDimArray) {
    const output = [];
    for (let i = 0; i < twoDimArray[0].length; i++) {
        const row = [];
        for (let j = 0; j < twoDimArray.length; j++) {
            row.push(twoDimArray[j][i]);
        }
        output.push(row);
    }
    return output;
}

export function range(start, end) {
    const output = [];
    for (let i = start; i < end; i++) {
        output.push(i);
    }
    return output;
}

export function sum(array) {
    return array.reduce((acc, current) => acc + current, 0);
}

export function sort(array) {
    return array.slice().sort((a, b) => a - b);
}

export function sortBy(fn) {
    return function (array) {
        return array.slice().sort(fn);
    };
}

export function reverse(array) {
    return array.slice().reverse();
}

export function max(array) {
    return array
        .slice(1, array.length)
        .reduce((acc, current) => (current > acc ? current : acc), array[0]);
}

export function min(array) {
    return array
        .slice(1, array.length)
        .reduce((acc, current) => (current < acc ? current : acc), array[0]);
}

export function map(fn) {
    return function (array) {
        return array.map(fn);
    };
}

export function flatMap(fn) {
    return function (array) {
        return array.flatMap(fn);
    };
}

export function filter(fn) {
    return function (array) {
        return array.filter(fn);
    };
}

export function find(fn) {
    return function (array) {
        return array.find(fn);
    };
}

export function reduce(fn, initial) {
    return function (array) {
        return array.reduce(fn, initial);
    };
}
