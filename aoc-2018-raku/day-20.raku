#! /usr/bin/env rakudo

my $input-file = open "inputs/day_20.txt", :r;
my @input-lines = $input-file.slurp.trim().split("\n");
$input-file.close;

sub get-shortest-paths(@input) {
    my %shortest-distances;

    my $x = 0;
    my $y = 0;
    my $doors-passed = 0;

    my @stack;

    for @input {
        given $_ {
            when "E" {
                $x += 1;
                $doors-passed += 1;
            }
            when "W" {
                $x -= 1;
                $doors-passed += 1;
            }
            when "N" {
                $y += 1;
                $doors-passed += 1;
            }
            when "S" {
                $y -= 1;
                $doors-passed += 1;
            }
            when "(" {
                @stack.push(($ = $x, $ = $y),);
            }
            when ")" {
                @stack.pop;
            }
            when "|" {
                ($x, $y) = @stack[*- 1];
            }
        }

        my $key = "$x,$y";

        if %shortest-distances{$key}:exists {
            if %shortest-distances{$key} > $doors-passed {
                %shortest-distances{$key} = $doors-passed;
            } else {
                $doors-passed = %shortest-distances{$key};
            }
        } else {
            %shortest-distances{$key} = $doors-passed;
        }
    }

    return %shortest-distances;
}

sub part1(@input) {
    my %shortest-distances = get-shortest-paths(@input);
    return %shortest-distances.values.max
}

sub part2(@input) {
    my %shortest-distances = get-shortest-paths(@input);
    return %shortest-distances.values.grep({ $_ >= 1000 }).elems
}

sub parse(@input) {
    return @input.comb[1..*- 1];
}

DOC CHECK {
    use Test;
    subtest 'Part 1', {
        part1('WNE'.comb).&is: 3;
        part1('ENWWW(NEEE|SSE(EE|N))'.comb).&is: 10;
        part1('ENNWSWW(NEWS|)SSSEEN(WNSE|)EE(SWEN|)NNN'.comb).&is: 18;
    }
}

say part1(parse(@input-lines));
say part2(parse(@input-lines));