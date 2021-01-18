const puzzleInput = "hxbxwxba";

let password = nextPassword(puzzleInput)
while(!validatePassword(password)) {
   password = nextPassword(password)
}
console.log(`part 1: ${password}`)

password = nextPassword(password)
while(!validatePassword(password)) {
   password = nextPassword(password)
}
console.log(`part 2: ${password}`)

function validatePassword(password) {
   return [containsStraight, noConfusingLetters, containsTwoPairs].reduce(
      (acc, validator) => acc && validator(password),
      true
   );
}

function containsStraight(string) {
   return new RegExp(threeLetterStraights().join("|")).test(string);
}

function noConfusingLetters(string) {
   return !/i|o|l/g.test(string)
}

function containsTwoPairs(string) {
   return /(.)\1.*((?:(?!\1).))\2/g.test(string);
}

function threeLetterStraights() {
   return "abcdefghijklmnopqrstuvwx"
      .split("")
      .map((c) => c + nextChar(c) + nextChar(nextChar(c)));
}

function nextChar(c) {
   return String.fromCharCode(c.charCodeAt(0) + 1);
}

function nextPassword(password) {
   const passwordAsNumber = passwordToNumber(password)
   return numberToPassword(passwordAsNumber + 1)
}

function numberToPassword(n) {
   return padString(n.toString(26), 8, '0')
      .split("")
      .map((n) => parseInt(n, 26))
      .map(alphaIndexToLetter)
      .join("");
}

function passwordToNumber(password) {
   const passwordBase26 = password
      .split("")
      .map(letterToAlphaIndex)
      .map((i) => i.toString(26))
      .join("");

   return parseInt(passwordBase26, 26);
}

function letterToAlphaIndex(c) {
   return c.charCodeAt(0) - 97;
}

function alphaIndexToLetter(i) {
   return String.fromCharCode(i + 97);
}

function padString(string, length, char) {
   return char.repeat(length - string.length) + string
}
