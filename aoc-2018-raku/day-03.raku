#! /usr/bin/env rakudo

my @test-input-lines = "#1 @ 1,3: 4x4
#2 @ 3,1: 4x4
#3 @ 5,5: 2x2".split("\n");

my $input-file = open "inputs/day_03.txt", :r;
my @input-lines = $input-file.slurp.trim().split("\n");
$input-file.close;

sub parse-claim($claim) {
    if $claim ~~ /'#'(\d+)' @ '(\d+)','(\d+)': '(\d+)'x'(\d+)/ {
        id => +$0,
        x => +$1,
        y => +$2,
        width => +$3,
        height => +$4
    }
}

sub parse-claims(@claims) {
    @claims.map({ parse-claim($_) })
}

sub go(@input, $size) {
    my @claims = parse-claims(@input);

    my @fabric[$size;$size] = [0 xx $size] xx $size;

    for @claims {
        my %claim = $_;
        for %claim{'x'} .. (%claim{'x'} + %claim{'width'} - 1) -> $x {
            for %claim{'y'} .. (%claim{'y'} + %claim{'height'} - 1) -> $y {
                @fabric[$x;$y] += 1;
            }
        }
    }

    say "Part 1:";
    say @fabric.grep({ $_ > 1 }).elems;

    for @claims {
        my %claim = $_;
        my $not-ones = 0;

        for %claim{'x'} .. (%claim{'x'} + %claim{'width'} - 1) -> $x {
            for %claim{'y'} .. (%claim{'y'} + %claim{'height'} - 1) -> $y {
                if @fabric[$x;$y] > 1 {
                    $not-ones += 1
                }
            }
        }

        if $not-ones == 0 {
            say "Part 2:";
            say %claim{'id'};
        }
    }
}

go(@input-lines, 1_000);
