#! /usr/bin/env rakudo

my $input-serial-no = 7803;

sub hundreds-digit($n) {
    ($n % 1000) div 100
}

sub power-level($x, $y, $serial-no) {
    my $rack-id = $x + 10;
    hundreds-digit((($rack-id * $y) + $serial-no) * $rack-id) - 5;
}

sub create-summed-area-table($serial-no) {
    my @square = gather {
        for ^301 {
            take gather {
                for ^301 { take 0; }
            }.Array
        }
    }.Array;
    for 1 .. 300 -> $y {
        for 1 .. 300 -> $x {
            @square[$y][$x] =
                    @square[$y - 1][$x] + @square[$y][$x - 1] - @square[$y - 1][$x - 1] +
                    power-level($x, $y, $serial-no);
        }
    }
    @square.map({ $_.list }).list;
}

sub sum-sub-square($top-left-x, $top-left-y, $size, @summed-area-table) {
    @summed-area-table[$top-left-y + $size - 1][$top-left-x + $size - 1]
            - @summed-area-table[$top-left-y + $size - 1][$top-left-x - 1]
            - @summed-area-table[$top-left-y - 1][$top-left-x + $size - 1]
            + @summed-area-table[$top-left-y - 1][$top-left-x - 1];
}

sub part1($serial-no) {
    my @summed-area-table = create-summed-area-table($serial-no);
    my $size = 3;
    my $largest-total-power = 0;
    my $identifier;

    for 1 .. (300 - $size + 1) -> $x {
        for 1 .. (300 - $size + 1) -> $y {
            my $power = sum-sub-square($x, $y, $size, @summed-area-table);
            if $power > $largest-total-power {
                $largest-total-power = $power;
                $identifier = ($x, $y);
                say "$identifier - $power";
            }
        }
    }

    say "the answer: $identifier";
}


sub part2($serial-no) {
    my @summed-area-table = create-summed-area-table($serial-no);
    my $largest-total-power = 0;
    my $identifier;

    for 1 .. 300 -> $size {
        say "size $size";
        for 1 .. (300 - $size + 1) -> $x {
            for 1 .. (300 - $size + 1) -> $y {
                my $power = sum-sub-square($x, $y, $size, @summed-area-table);
                if $power > $largest-total-power {
                    $largest-total-power = $power;
                    $identifier = ($x, $y, $size);
                    say "$identifier - $power";
                }
            }
        }
    }

    say "the answer: $identifier";
}

DOC CHECK {
    use Test;
    subtest 'hundreds-digit', {
        hundreds-digit(1234).&is: 2;
        hundreds-digit(87654).&is: 6;
        hundreds-digit(54).&is: 0;
    }
    subtest 'power-level', {
        power-level(3, 5, 8).&is: 4;
        power-level(122, 79, 57).&is: -5;
        power-level(217, 196, 39).&is: 0;
        power-level(101, 153, 71).&is: 4;
    }
}

part1($input-serial-no);
part2($input-serial-no);