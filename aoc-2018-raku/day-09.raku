#! /usr/bin/env rakudo

my $input = ${ players => 413, last-marble => 71082 };

sub allocate-scores(%scores, $players) {
    my %allocated-scores;
    for %scores.keys -> $iteration {
        %allocated-scores{$iteration % $players} += %scores{$iteration}
    }
    return %allocated-scores;
}

sub insert($n, $i, @array) {
    my @start = @array[^$i];
    my @end = @array[$i ..*];
    @start.append($n).append(@end);
}

sub delete($i, @array) {
    my @start = @array[^$i];
    my @end = @array[$i ^..*];
    @start.append(@end);
}

sub brute-force_1(% (:$players, :$last-marble)) {
    # calculates each successive state

    my @placed = [0];
    my $next-marble = 0;
    my $pointer = 0;
    my $player = 0;
    my %scores;

    while ($next-marble++ < $last-marble) {
        if $next-marble %% 23 {
            $pointer = ($pointer - 7) % @placed.elems;
            %scores{$next-marble} = $next-marble + @placed[$pointer];
            @placed = delete($pointer, @placed);
            $pointer %= @placed.elems;
        } else {
            $pointer = ($pointer + 2) % @placed.elems;
            @placed = insert($next-marble, $pointer, @placed);
        }
    }

    $player = ($player + 1) % $players;
    allocate-scores(%scores, $players).values.max;
}

sub brute-force_2(% (:$players, :$last-marble)) {
    # calculates 22 "normal" turns in one go, then calculates 1 scoring turn with the different rule

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

        # 1 scoring turn
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
        brute-force_1(${ players => 9, last-marble => 25 }).&is: 32;
        brute-force_1(${ players => 10, last-marble => 1618 }).&is: 8317;
#        brute-force_1(${ players => 13, last-marble => 7999 }).&is: 146373;
        brute-force_1(${ players => 17, last-marble => 1104 }).&is: 2764;
#        brute-force_1(${ players => 21, last-marble => 6111 }).&is: 54718;
#        brute-force_1(${ players => 30, last-marble => 5807 }).&is: 37305;
        brute-force_2(${ players => 9, last-marble => 25 }).&is: 32;
        brute-force_2(${ players => 10, last-marble => 1618 }).&is: 8317;
        brute-force_2(${ players => 13, last-marble => 7999 }).&is: 146373;
        brute-force_2(${ players => 17, last-marble => 1104 }).&is: 2764;
        brute-force_2(${ players => 21, last-marble => 6111 }).&is: 54718;
        brute-force_2(${ players => 30, last-marble => 5807 }).&is: 37305;
    }
}

# very slow (7m on my machine)
say brute-force_2($input);
