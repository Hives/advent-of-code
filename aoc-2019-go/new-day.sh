#! /usr/bin/env bash

set -euo pipefail

day="day${1}"

mkdir $day
cd $day
go mod init $day
cp ../template.go.txt "./${day}.go"
go mod edit -replace reader=../reader
go mod edit -replace aoc=../aoc
go mod tidy
touch input.txt
