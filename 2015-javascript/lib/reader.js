import fs from 'fs'

const readFile = fileName => fs.readFileSync(`inputs/${fileName}`, 'utf-8')

export default fileName => {
  const file = readFile(fileName)
  return file.trim().split("\n")
}

