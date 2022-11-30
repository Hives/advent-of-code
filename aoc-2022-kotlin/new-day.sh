#! /usr/bin/env bash

export DAY=$(date +"%d")

path="src/main/kotlin/days/day${DAY}"
file="day${DAY}.kt"

if [ -d $path ]; then
    echo "Directory ${path} already exists"
else
    echo "Creating folder ${path}"
    mkdir -p $path
fi;

if [ -f "${path}/${file}" ]; then
    echo "File ${path}/${file} already exists"
else
    echo "Creating file ${path}/${file}"
    envsubst < template.kt.txt > "${path}/${file}"
fi;
