#! /usr/bin/env rakudo

my $input-file = open "inputs/day_18.txt", :r;
my @input-lines = $input-file.slurp.trim().split("\n");
$input-file.close;

my $test-input-file = open "inputs/day_18_test.txt", :r;
my @test-input-lines = $test-input-file.slurp.trim().split("\n");
$test-input-file.close;

sub parse(@lines) {
    @lines».&({ $_.comb }).list
}

sub neighbours(@grid, $x, $y) {
    my $max-y = @grid.elems;
    my $max-x = @grid[0].elems;
    return (($x - 1, $y - 1), ($x, $y - 1), ($x + 1, $y - 1), ($x - 1, $y), ($x + 1, $y),
            ($x - 1, $y + 1), ($x, $y + 1), ($x + 1, $y + 1))
            .grep({ $_[0] ~~ ^$max-x && $_[1] ~~ ^$max-y })
            .map({ @grid[$_[1]][$_[0]] })
}

sub tick(@state) {
    my @new-state;
    my $max-y = @state.elems;
    my $max-x = @state[0].elems;

    for ^$max-y -> $y {
        my @new-row;
        for ^$max-x -> $x {
            my $cell;
            given @state[$y][$x] {
                my @neighbours = neighbours(@state, $x, $y);
                when '.' {
                    $cell = @neighbours.grep({ $_ eq '|' }).elems >= 3 ?? '|' !! '.'
                }
                when '|' {
                    $cell = @neighbours.grep({ $_ eq '#' }).elems >= 3 ?? '#' !! '|'
                }
                when '#' {
                    $cell =
                            @neighbours.grep({ $_ eq '#' }).elems >= 1 &&
                                    @neighbours.grep({ $_ eq '|' }).elems >= 1
                            ?? '#'
                            !! '.'
                }
            }
            @new-row.append($cell)
        }
        @new-state.push(@new-row.List)
    }

    return @new-state.List;
}

sub clear {
    print qx[clear];
}

sub score(@state) {
    my $woods = @state.List.flat.grep({ $_ eq '|' }).elems;
    my $lumberyards = @state.List.flat.grep({ $_ eq '#' }).elems;
    return $woods * $lumberyards;
}

sub state-to-string(@state) {
    @state.List.flat.join
}

sub part1(@input) {
    my @state = parse(@input);
    for ^10 {
        @state = tick(@state);
    }
    say score(@state);
}

sub part2(@input) {
    my @state = parse(@input);
    my %history;

    #    clear();
    #    for @state { .say }

    my $n = 1;
    loop {
        my $c = $n++;
        say "n = $c";

        @state = tick(@state);

        my $state-string = state-to-string(@state);

        if $state-string ∈ %history.keys {
            for %history { .say }

            say "Detected loop at n = $c";
            my $loop-start = %history{$state-string}[0];
            say "Loop start = $loop-start";
            my $loop-length = $c - $loop-start;
            say "Loop length = $loop-length";
            my $final-position-in-loop = (1000000000 - $loop-start) % $loop-length;
            say "Final position in loop = $final-position-in-loop";
            my $final-position = $final-position-in-loop + $loop-start;
            say "Final position = $final-position";
            my $final-score = %history.values.grep({ $_[0] == $final-position });
            say $final-score;
            last
        }

        %history{$state-string} = $c, score(@state);
    }
}

#part1(@test-input-lines);
part2(@input-lines);
