#! /usr/bin/env rakudo

my @test-input-lines = "+1, -2, +3, +1".split(", ");

my $input-file = open "inputs/day_01.txt", :r;
my @input-lines = $input-file.slurp.trim().split("\n");
$input-file.close;

sub parse(@lines) {
    map -> $p {
        my $sign = substr($p, 0, 1);
        my $number = +substr($p, 1);
        $number * ($sign eq "+" ?? 1 !! -1)
    }, @lines
}

my @puzzle-input = parse(@input-lines);

sub find-first-repeated-freq(@changes) {
    my %frequencies = SetHash.new;
    my $current = 0;
    my $pointer = 0;

    until %frequencies{$current} {
        %frequencies{$current}++;
        $current += @changes[$pointer];
        $pointer = ($pointer + 1) % @changes.elems
    }

    return $current
}

say (@puzzle-input.sum);
say (find-first-repeated-freq(@puzzle-input));
