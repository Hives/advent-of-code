#! /usr/bin/env rakudo

my $input = ${ players => 413, last-marble => 71082 };
my $test1 = ${ players => 9, last-marble => 25 };
my $test2 = ${ players => 10, last-marble => 1618 };
my $test3 = ${ players => 13, last-marble => 7999 };
my $test4 = ${ players => 17, last-marble => 1104 };
my $test5 = ${ players => 21, last-marble => 6111 };
my $test6 = ${ players => 30, last-marble => 5807 };

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

sub go(% (:$players, :$last-marble)) {
    my @placed = [0];
    my $next-marble = 0;
    my $pointer = 0;
    my $player = 0;
    my %scores;

    while ($next-marble++ < $last-marble) {
        say $next-marble if $next-marble %% 500;
        if $next-marble %% 23 {
            %scores{$player} += $next-marble;
            $pointer = ($pointer - 7) % @placed.elems;
            %scores{$player} += @placed[$pointer];
            @placed = delete($pointer, @placed);
            $pointer %= @placed.elems;
        } else {
            $pointer = ($pointer + 2) % @placed.elems;
            @placed = insert($next-marble, $pointer, @placed);
        }

        $player = ($player + 1) % $players;

    }

    say "max score: {%scores.values.max}";
}

go($test1);
go($test2);
#go($test3);
go($test4);
go($test5);
go($test6);
