#! /usr/bin/env bash

if [ $# -eq 0 ]; then
  year=$(date +"%Y")
  day=$(date +"%d")
elif [ -z $2 ]; then
  year=$(date +"%Y")
  day=$1
else
  year=$1
  day=$2
fi

echo "Setting up day ${day}, from year ${year}"

day_no_leading_zeroes=$(echo $day | sed 's/^0*//')

path="src/main/kotlin/days/day${day}"

if [ -d $path ]; then
    echo "Directory ${path} already exists"
else
    echo "Creating folder ${path}"
    mkdir -p $path
fi;

input_path="src/main/resources/day${day}"
if [ -d $input_path ]; then
    echo "Directory ${input_path} already exists"
else
    echo "Creating folder ${input_path}"
    mkdir -p $input_path
fi;

input_file="${input_path}/input.txt"
if [ -f $input_file ]; then
    echo "File ${input_file} already exists"
else
    echo "Downloading input"
    # requires COOKIE to be defined in .env
    source ./.env
    curl "https://adventofcode.com/${year}/day/${day_no_leading_zeroes}/input" \
      -H "cookie: ${COOKIE}" \
      -o $input_file
fi;

input_example="${input_path}/example-1.txt"
if [ -f $input_example ]; then
    echo "File ${input_example} already exists"
else
    echo "Creating empty example input file"
    touch $input_example
fi;

input_gitkeep="${input_path}/.gitkeep"
if [ -f $input_gitkeep ]; then
    echo "File ${input_gitkeep} already exists"
else
    touch $input_gitkeep
fi;

answers_file="${input_path}/answers.txt"
if [ -f $answers_file ]; then
    echo "File ${answers_file} already exists"
else
    echo "Creating file ${answers_file}"
    cp template.answers.txt $answers_file
fi;

solution_file="${path}/day${day}.kt"
if [ -f $solution_file ]; then
    echo "File ${solution_file} already exists"
else
    echo "Creating file ${solution_file}"
    export DAY=$day
    envsubst < template.day.kt.txt > $solution_file
fi;

