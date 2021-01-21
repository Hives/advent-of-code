export function removeValueFromArray(array, value) {
    const index = array.findIndex((element) => element === value);
    if (index === -1) {
        throw `Could not find value ${value} in array`;
    }
    return [...array.slice(0, index), ...array.slice(index + 1, array.size)];
}

export function sumArray(array) {
    return array.reduce((acc, current) => acc + current, 0);
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
