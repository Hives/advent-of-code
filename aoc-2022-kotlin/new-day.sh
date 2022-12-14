#! /usr/bin/env bash

export DAY=$(date +"%d")

path="src/main/kotlin/days/day${DAY}"
input_path="src/main/resources"
file="day${DAY}.kt"

if [ -d $path ]; then
    echo "Directory ${path} already exists"
else
    echo "Creating folder ${path}"
    mkdir -p $path
fi;

echo "Downloading input"
# requires COOKIE to be defined in .env
source ./.env
curl "https://adventofcode.com/2022/day/${DAY}/input" \
  -H "cookie: ${COOKIE}" \
  -o "${input_path}/day${DAY}.txt"

echo "Creating empty example input file"
touch "${input_path}/day${DAY}-example.txt"

if [ -f "${path}/${file}" ]; then
    echo "File ${path}/${file} already exists"
else
    echo "Creating file ${path}/${file}"
    envsubst < template.kt.txt > "${path}/${file}"
fi;
