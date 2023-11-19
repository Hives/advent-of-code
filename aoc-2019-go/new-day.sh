#! /usr/bin/env bash

set -euo pipefail

day="day${1}"

mkdir "$day"
cd "$day"
go mod init "$day"
cp ../template.go.txt "./${day}.go"
go mod edit -replace reader=../lib/reader
go mod edit -replace aoc=../lib/aoc
go mod edit -replace intcode=../lib/intcode
go mod tidy
touch input.txt
