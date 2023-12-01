#! /usr/bin/env bash

day=$(date +"%d")
day_no_leading_zeroes=$(echo $day | sed 's/^0*//')
year=$(date +"%Y")

path="src/main/kotlin/days/day${day}"
input_path="src/main/resources/day${day}"
file="day${day}.kt"

if [ -d $path ]; then
    echo "Directory ${path} already exists"
else
    echo "Creating folder ${path}"
    mkdir -p $path
fi;

if [ -d $input_path ]; then
    echo "Directory ${input_path} already exists"
else
    echo "Creating folder ${input_path}"
    mkdir -p $input_path
fi;

echo "Downloading input"
# requires COOKIE to be defined in .env
source ./.env
curl "https://adventofcode.com/${year}/day/${day_no_leading_zeroes}/input" \
  -H "cookie: ${COOKIE}" \
  -o "${input_path}/input.txt"

echo "Creating empty example input file"
touch "${input_path}/example-1.txt"

if [ -f "${path}/${file}" ]; then
    echo "File ${path}/${file} already exists"
else
    echo "Creating file ${path}/${file}"
    export DAY=$day
    envsubst < template.kt.txt > "${path}/${file}"
fi;
