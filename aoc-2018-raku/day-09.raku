#! /usr/bin/env rakudo

my $input = ${ players => 413, last-marble => 71082 };

sub allocate-scores(%scores, $players) {
    my %allocated-scores;
    for %scores.keys -> $iteration {
        %allocated-scores{$iteration % $players} += %scores{$iteration}
    }
    return %allocated-scores;
}

sub brute-force(% (:$players, :$last-marble)) {
    # state after turn 23:
    my @placed = 2, 20, 10, 21, 5, 22, 11, 1, 12, 6, 13, 3, 14, 7, 15, 0, 16, 8, 17, 4, 18, 19;
    my %scores = 23 => 32;
    my $turn = 23;

    loop {
        say "turn $turn";
        # 22 normal turns
        $turn++;
        my @next_22_marbles = ($turn .. ($turn + 21)).list;
        @placed = (@placed[22 ..*], zip(@placed, @next_22_marbles)).flat;

        # 1 special turn
        $turn += 22;
        last if $turn > $last-marble;
        %scores{$turn} = $turn + @placed[*- 8];
        @placed = (@placed[*- 6 ..*], @placed[0 .. *- 9], @placed[*- 7]).flat;
    }

    allocate-scores(%scores, $players).values.max;
}

DOC CHECK {
    use Test;
    subtest 'Part 1', {
        brute-force(${ players => 9, last-marble => 25 }).&is: 32;
        brute-force(${ players => 10, last-marble => 1618 }).&is: 8317;
        brute-force(${ players => 13, last-marble => 7999 }).&is: 146373;
        brute-force(${ players => 17, last-marble => 1104 }).&is: 2764;
        brute-force(${ players => 21, last-marble => 6111 }).&is: 54718;
        brute-force(${ players => 30, last-marble => 5807 }).&is: 37305;
    }
}

# very slow (7m on my machine)
say brute-force($input);
