const md5 = require('md5')

const test1 = "abcdef"
const test2 = "pqrstuv"
const puzzleInput = "bgvyzdsv"

function solution(secretKey, startsWith) {
    let n = 1
    while (md5(secretKey + n).slice(0, startsWith.length) != startsWith) {
        n++
    }
    return n
}

// warning - slow (30s)

console.log(solution(test1, "00000"))
console.log(solution(test2, "00000"))
console.log(solution(puzzleInput, "00000"))

console.log(solution(puzzleInput, "000000"))
