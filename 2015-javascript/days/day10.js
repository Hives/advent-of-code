function lookAndSay(input) {
    return input.split("").reduce((acc, current) => {
        if (acc.length === 0) {
            return 1 + current;
        }
        if (acc[acc.length - 1] === current) {
            return (
                acc.slice(0, acc.length - 2) +
                (parseInt(acc[acc.length - 2]) + 1) +
                current
            );
        }
        return `${acc}${1}${current}`;
    }, "");
}

function lookAndSayRegex(input) {
    /* "Inspiration" taken from the Perl script on this page:
     *    https://oeis.org/A006715
     * linked to from Wiki:
     *    https://en.wikipedia.org/wiki/Look-and-say_sequence
     */
    return input.replace(/(.)\1*/g, match => match.length + match[0])
}

const puzzleInput = "1113222113";

let output = puzzleInput;
for (let i = 1; i <= 50; i++) {
    output = lookAndSayRegex(output);
    console.log(`i = ${i}, length = ${output.length}`);
}
