#! /usr/bin/env rakudo

my $input-file = open "inputs/day_02.txt", :r;
my @input-lines = $input-file.slurp.trim().split("\n");
$input-file.close;

sub letters($string) {
    $string.split("", :skip-empty);
}

sub contains-repeated-letter($string, $repeats) {
    my @letters = letters($string);
    my @letter-counts = @letters.unique.map({ @letters.grep($_).elems });
    $repeats (elem) @letter-counts
}

sub checksum(@ids) {
    my $contains-double = @ids.grep({ contains-repeated-letter($_, 2) }).elems;
    my $contains-triple = @ids.grep({ contains-repeated-letter($_, 3) }).elems;
    $contains-double * $contains-triple
}

sub differ-by-one-letter($a, $b) {
    zip(letters($a), letters($b)).grep({ $_[0] ne $_[1] }).elems == 1
}

sub find-pair-that-differs-by-one-letter(@ids) {
    my @pairs = @ids.combinations: 2;
    @pairs.grep({ differ-by-one-letter($_[0], $_[1])})
}

say checksum(@input-lines);
say find-pair-that-differs-by-one-letter(@input-lines)