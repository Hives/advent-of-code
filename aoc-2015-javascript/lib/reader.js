import fs from "fs";

export const readFile = (fileName) =>
  fs.readFileSync(`inputs/${fileName}`, "utf-8").trim();

export const readStrings = (fileName) => readFile(fileName).split("\n");

export const readStringsToArrays = (fileName) =>
  readStrings(fileName).reduce(
    (acc, current) => [...acc, current.split("")],
    []
  );
