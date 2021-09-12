#! /usr/bin/env rakudo

my $input-file = open "inputs/day_XX.txt", :r;
my @input-lines = $input-file.slurp.trim().split("\n");
$input-file.close;
