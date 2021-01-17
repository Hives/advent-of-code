export function removeValueFromArray(array, value) {
    const index = array.findIndex((element) => element === value);
    if (index === -1) {
        throw `Could not find value ${value} in array`;
    }
    return [...array.slice(0, index), ...array.slice(index + 1, array.size)];
}
