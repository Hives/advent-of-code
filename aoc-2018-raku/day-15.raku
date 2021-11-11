#! /usr/bin/env rakudo

use Test;
use ValueType;

#my $input-file = open "inputs/day_XX.txt", :r;
#my @input-lines = $input-file.slurp.trim().split("\n");
#$input-file.close;

my @test1 = qq:to/END/.trim().split("\n").map({ $_.trim().comb });
#######
#E..G.#
#...#.#
#.G.#G#
#######
END

class Point does ValueType {
    has Int $.x;
    has Int $.y;

    method Str {
        "($!x, $!y)"
    }
    method gist {
        "($!x, $!y)"
    }
}

sub pnt(Int $x, Int $y) {
    Point.new(:$x, :$y)
}

multi infix:<+> (Point \a, Point \b) {
    Point.new(x => a.x + b.x, y => a.y + b.y)
}

sub parse(@input) {
    my @units;
    my @map;
    for 0 ..^ @input.elems -> $y {
        my $row;
        for 0 ..^ @input[$y].elems -> $x {
            my $square = @input[$y][$x];
            if $square eq "E" || $square eq "G" {
                @units.append($%(type => $square, hitpoints => 200, location => pnt($x, $y)));
                $row.append(".")
            } else {
                $row.append($square)
            }
        }
        @map.append($($row.List));
    }
    @units, @map.List;
}

sub reading-order(@points) {
    @points.sort({ $_.y, $_.x })
}

sub break-tie(@points) {
    reading-order(@points)[0]
}

sub neighbours(@points, @map) {
    my @neighbours;
    for @points -> $point {
        for ($point + $_ for (pnt(0, -1), pnt(-1, 0), pnt(1, 0), pnt(0, 1)))
                .grep({ @map[$_.y][$_.x] eq "." }) -> $neighbour {
            @neighbours.push($neighbour)
        }
    }
    @neighbours.unique().&reading-order;
}

sub find-nearest-target($start, @targets, @map) {
    my @visited;
    my @current = $start,;

    loop {
        @visited = flat(@visited, @current).unique;
        @current = neighbours(@current, @map).grep({ $_ ∉ @visited });
        my @found = @current.grep({ $_ ∈ @targets });
        if ?@found {
            return break-tie(@found)
        }
    }
}

#sub choose-destination(%unit, @units, @map) {
#    say @units;
#    my @targets = @units.grep({ $_<type> !eq %unit<type> });
#    my @in-range;
#    for @targets -> %target {
#        for neighbours(%target<location>, @map) -> $neighbour {
#            @in-range.push($neighbour)
#        }
#    }
#    #@targets.map({ neighbours($_<location>, @map) }).flat;
#
#    say @in-range;
#}

DOC CHECK {
    my @test1 = qq:to/END/.trim().split("\n").map({ $_.trim().comb });
        #######
        #E..G.#
        #...#.#
        #.G.#G#
        #######
        END

    my ($units, $map) = parse(@test1);

    subtest 'Points', {
        (pnt(1, 2).x).&is: 1;
        (pnt(1, 2).y).&is: 2;
        "{ pnt(1, 2) }".&is: "(1, 2)";
        pnt(1, 2).&is: pnt(1, 2);
        (pnt(1, 2) ∈ (pnt(1, 2), pnt(3, 4))).&is: True;
        (pnt(1, 2) + pnt(10, 20)).&is: pnt(11, 22);
    }

    subtest 'Reading order', {
        reading-order((pnt(3, 2), pnt(2, 1), pnt(1, 2))).&is:
                (pnt(2, 1), pnt(1, 2), pnt(3, 2))
    }

    subtest 'Breaking a tie', {
        break-tie((pnt(1, 0), pnt(0, 1))).&is: pnt(1, 0);
    }

    subtest 'Neighbours', {
        neighbours((pnt(2, 2),), $map).&is: (pnt(2, 1), pnt(1, 2), pnt(3, 2), pnt(2, 3));
        neighbours((pnt(5, 3),), $map).&is: (pnt(5, 2));
        neighbours((pnt(1, 1), pnt(3, 1)), $map).&is: (pnt(2, 1), pnt(4, 1), pnt(1, 2), pnt(3, 2));
    }

    subtest 'Find nearest target', {
        is(find-nearest-target(pnt(1, 1), (pnt(2, 2), pnt(1, 3), pnt(3, 1), pnt(3, 3)), $map),
                pnt(3, 1),
                "Tie-breaks nearest target if multiple targets exist"
           );
        # returns Nil if targets blocked by wall
        # returns Nil if targets blocked by other units of same type
    }

    #    subtest 'Choose destination', {
    #        choose-destination(%( type => "E"), $units, $map).&is: "wtf";
    #    }
}

say parse(@test1);