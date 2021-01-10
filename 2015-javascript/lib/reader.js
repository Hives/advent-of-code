const fs = require('fs')

const readFile = fileName => fs.readFileSync(`inputs/${fileName}`, 'utf-8')

const readStrings = fileName => {
  const file = readFile(fileName)
  return file.trim().split("\n")
}

exports.readStrings = readStrings
