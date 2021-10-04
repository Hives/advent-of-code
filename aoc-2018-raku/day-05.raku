#! /usr/bin/env rakudo

my $input-file = open "inputs/day_05.txt", :r;
my $input = $input-file.slurp.trim();
$input-file.close;

my $test-input = "dabAcCaCBAcCcaDA";

sub annihilates($char1, $char2) {
    return False if $char2 === Any;
    ($char1 ne $char2) && ($char1.lc eq $char2.lc);
}

sub pass(@chars) {
    my @output;
    my $pointer = 0;
    while $pointer < @chars.elems {
        if (annihilates(@chars[$pointer], @chars[$pointer + 1])) {
            $pointer += 2;
        } else {
            @output.push(@chars[$pointer]);
            $pointer += 1;
        }
    }
    return @output;
}

sub reduce(@new-chars) {
    my @chars;
    my $iteration = 0;
    until @new-chars.elems == @chars.elems {
        say "iteration " ~ $iteration if $iteration %% 100;
        $iteration += 1;
        @chars = @new-chars;
        @new-chars = pass(@chars);
    }
    return @chars.elems;
}

sub part1($input) {
    my @chars = $input.comb();
    say reduce(@chars);
}

sub part2($input) {
    my @chars = $input.comb();
    my %results := {};
    for 'a' .. 'z' -> $removed_unit {
        say "removing " ~ $removed_unit;
        my @filtered_chars = @chars.grep({ $_.lc ne $removed_unit });
        %results{$removed_unit} = reduce(@filtered_chars);
    }
    say %results;
}

# warning - very slow
part1($input);
part2($input);
