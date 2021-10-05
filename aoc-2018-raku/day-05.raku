#! /usr/bin/env rakudo

my $input-file = open "inputs/day_05.txt", :r;
my $input = $input-file.slurp.trim();
$input-file.close;

my $test-input = "dabAcCaCBAcCcaDA";

sub annihilates($char1, $char2) {
    return False if $char1 === Any;
    return False if $char2 === Any;
    ($char1 ne $char2) && ($char1.lc eq $char2.lc);
}

sub react($input) {
    my $letters = $input;
    my $old-letters = "";
    until $old-letters.chars == $letters.chars {
        $old-letters = $letters;
        $letters = pass($letters);
    }
    return $letters.chars;
}

sub pass($input) {
    my $pointer = 0;
    my @chars = $input.comb();

    my @blocks = 0;

    while $pointer < @chars.elems {
        if (annihilates(@chars[$pointer], @chars[$pointer + 1])) {
            my $block-end = $pointer;
            my $block-start = $pointer + 1;
            while (($block-end - 1 > @blocks[*- 1]) && annihilates(@chars[$block-end - 1], @chars[$block-start + 1])) {
                $block-end -= 1;
                $block-start += 1;
            }
            @blocks.append($block-end - 1);
            @blocks.append($block-start + 1);
            $pointer = $block-start + 1;
        } else {
            $pointer += 1;
        }
    }

    @blocks.append(@chars.elems - 1);

    @blocks.batch(2).map({ $input.substr($_[0] .. $_[1]) }).join;
}

sub part1($input) {
    say react($input);
}

sub part2($input) {
    my %results := {};
    for 'a' .. 'z' -> $removed_unit {
        say "removing " ~ $removed_unit;
        my $filtered_input = $input.comb().grep({ $_.lc ne $removed_unit }).join();
        %results{$removed_unit} = react($filtered_input);
        say "reacted length: " ~ %results{$removed_unit};
    }
    say %results;
    say "shortest: " ~ %results.min({ $_.value })
}

part1($input);
part2($input);
