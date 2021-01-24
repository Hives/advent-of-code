export function pipe(arg, ...fns) {
    return fns.reduce((v, fn) => fn(v), arg);
}
