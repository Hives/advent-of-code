#! /usr/bin/env rakudo

my $input-serial-no = 7803;

sub hundreds-digit($n) {
    ($n % 1000) div 100
}

sub power-level($x, $y, $serial-no) {
    my $rack-id = $x + 10;
    hundreds-digit((($rack-id * $y) + $serial-no) * $rack-id) - 5;
}

sub power-levels($serial-no) {
    my %power-levels;
    for 1 .. 300 -> $y {
        for 1 .. 300 -> $x {
            %power-levels{"$x,$y"} = power-level($x, $y, $serial-no);
        }
    }
    %power-levels;
}

sub calc-square-power-level($top-left-x, $top-left-y, $serial-no, $size) {
    gather {
        for $top-left-x ..^ ($top-left-x + $size) -> $x {
            for $top-left-y ..^ ($top-left-y + $size) -> $y {
                take power-level($x, $y, $serial-no)
            }
        }
    }.sum
}

sub square-power-level($top-left-x, $top-left-y, %power-levels, $size) {
    gather {
        for $top-left-x ..^ ($top-left-x + $size) -> $x {
            for $top-left-y ..^ ($top-left-y + $size) -> $y {
                take %power-levels{"$x,$y"};
            }
        }
    }.sum
}

sub part1($serial-no) {
    my $size = 3;
    #    my @power-levels = power-levels($serial-no);
    my @co-ords = gather {
        for 1 .. (300 - $size + 1) -> $x {
            for 1 .. (300 - $size + 1) -> $y {
                take ($x, $y)
            }
        }
    }
    @co-ords.max(-> @ ($x, $y) {
        #        square-power-level($x, $y, @power-levels, $size)
        calc-square-power-level($x, $y, $serial-no, $size)
    });
}

# did size 1-5, calculating, in 26s
# did size 1-5, looking things up, in 38s

sub part2($serial-no) {
    my $largest-total-power = 0;
    my $identifier;

    my $start-time = now;

    my $fh = open "day-11-output.txt", :w;
    sub fsay($text) {
        $fh.say($text);
        $fh.flush;
    }

    for 1 .. 300 -> $size {
        fsay("---");
        fsay("size $size");
        fsay("---");
        for 1 .. (300 - $size + 1) -> $x {
            for 1 .. (300 - $size + 1) -> $y {
                my $power = calc-square-power-level($x, $y, $serial-no, $size);
                if $power > $largest-total-power {
                    $largest-total-power = $power;
                    $identifier = ($x, $y, $size);
                    fsay("$identifier - $power");
                }
            }
        }
    }

    fsay("--");

    fsay("time: { now - $start-time }");

    fsay("the answer: $identifier");

    $fh.close;
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

#say part1($input-serial-no);
say part2($input-serial-no);