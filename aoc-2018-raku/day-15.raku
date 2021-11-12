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

my @movement-test = qq:to/END/.trim().split("\n").map({ $_.trim().comb });
#########
#G..G..G#
#.......#
#.......#
#G..E..G#
#.......#
#.......#
#G..G..G#
#########
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
    my $unit-count = 0;
    for 0 ..^ @input.elems -> $y {
        my $row;
        for 0 ..^ @input[$y].elems -> $x {
            my $square = @input[$y][$x];
            if $square eq "E" || $square eq "G" {
                @units.append($%(:id($unit-count += 1), :type($square), :hitpoints(200), :location(pnt($x, $y))));
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

sub order-units(@units) {
    @units.sort: { $_<location>.y, $_<location>.x }
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

sub find-nearest($start, @endpoints, @occupied, @map) {
    my @visited;
    my @current = $start,;

    loop {
        @visited = flat(@visited, @current).unique;
        @current = neighbours(@current, @map).grep({ $_ ∉ @visited && $_ ∉ @occupied });
        return Nil if !@current;

        my @found = @current.grep({ $_ ∈ @endpoints });
        return break-tie(@found) if ?@found
    }

}

sub find-nearest-target(%unit, @units, @map) {
    my $start = %unit<location>;
    my @occupied = @units.map({ $_<location> });
    my @enemy-locations = @units.grep({ $_<type> !eq %unit<type> }).map({ $_<location> });
    my @targets = neighbours(@enemy-locations, @map);

    find-nearest($start, @targets, @occupied, @map);
}

sub find-next-step(%unit, $target, @units, @map) {
    my $possible-next-steps = neighbours(@(%unit<location>), @map).List;
    return $target if $target ∈ $possible-next-steps;
    my @occupied = @units.map({ $_<location> });
    find-nearest($target, $possible-next-steps, @occupied, @map)
}

sub move(%unit is copy, @units, @map) {
    my @enemy-locations =
            @units.grep({ $_<type> !eq %unit<type> }).map({ $_<location> });
    return %unit if %unit<location> (elem) neighbours(@enemy-locations, @map);

    my $nearest-target = find-nearest-target(%unit, @units, @map);
    return %unit if !$nearest-target;

    my $next-step = find-next-step(%unit, $nearest-target, @units, @map);
    %unit<location> = $next-step;
    return %unit;
}

sub printy(@map, @units) {
    my @map-mutable = @map.map({ $_.Array });
    for @units -> %unit {
        my $location = %unit<location>;
        @map-mutable[$location.y][$location.x] = %unit<type>;
    }
    say "=====";
    say "  { (0 ..^ @map[0].elems).values.map({ $_ % 10 }).join }";
    for 0 ..^ @map-mutable.elems -> $col {
        say "{ $col % 10 } { @map-mutable[$col].join }"
    }
    say "=====";
}

sub foo(@input) {
    my ($units, $map) = parse(@input);
    printy($map, $units);

    my $n = 0;
    while ($n += 1) < 5 {
        my @order = order-units($units).map({ $_<id> });
        my $units-map = Map.new($units.map({ $_<id> => $_ }));
        for @order -> $id {
            my $unit = $units-map{$id};
            $units-map{$id} = move($unit, $units-map.values, $map);
        }
        $units = $units-map.values;
        printy($map, $units);
    }

}

DOC CHECK {
    my @test1 = qq:to/END/.trim().split("\n").map({ $_.trim().comb });
        #######
        #E..G.#
        #...#.#
        #.G.#G#
        #######
        END

    my ($units, $map) = parse(@test1);

    my @test2 = qq:to/END/.trim().split("\n").map({ $_.trim().comb });
        #######
        #E..#.#
        #...#.#
        #...#G#
        #######
        END

    my $map2 = parse(@test2)[1];

    my @test3 = qq:to/END/.trim().split("\n").map({ $_.trim().comb });
        #######
        #E..E.#
        #..EGE#
        #...E.#
        #######
        END

    my ($units3, $map3) = parse(@test3);

    subtest 'Points', {
        is(pnt(1, 2).x, 1, "x co-ord");
        is(pnt(1, 2).y, 2, "y co-ord");
        is(pnt(1, 2).Str, "(1, 2)", "to string");
        is(pnt(1, 2), pnt(1, 2), "equality (think this test uses `eq`, which compares as strings)");
        is(pnt(1, 2) ∈ (pnt(1, 2), pnt(-1, -1)), True, "can identify member of collection");
        is(pnt(1, 2) + pnt(10, 20), pnt(11, 22), "addition");
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
        my %unit = %(:location(pnt(1, 1)), :type("E"));
        is(find-nearest-target(%unit, $units, $map),
                pnt(3, 1),
                "Tie-breaks nearest target if multiple targets exist");
        is(find-nearest-target(%unit, (%(:type("G"), :location(pnt(5, 3))),), $map2),
                Nil,
                "Returns Nil if no target can be reached");
        is(find-nearest-target(%unit, $units3, $map3),
                Nil,
                "Returns Nil if target blocked by friendly units");
    }

    subtest 'Find next step', {
        my %unit = %(:location(pnt(2, 1)));
        is(find-next-step(%unit, pnt(4, 2), (), $map3),
                pnt(3, 1),
                "Returns the next step towards a target"),

        my @big-test = qq:to/END/.trim().split("\n").map({ $_.trim().comb });
            #######
            #.....#
            #.....#
            #.....#
            #.....#
            #.....#
            #######
            END

        my ($, $big-map) = parse(@big-test);
        is(find-next-step(%(:location(pnt(3, 2))), pnt(3, 1), (), $big-map),
                pnt(3, 1),
                "Can get the next step if target is a neighbour"),

        # what if there are units in the way?
    }

    subtest 'Move', {
        my @test = qq:to/END/.trim().split("\n").map({ $_.trim().comb });
            #####
            #EG.#
            #..G#
            #####
            END

        my ($units, $map) = parse(@test);
        my %unit = $units.grep({ $_<type> eq "E" })[0];
        is(move(%unit, $units, $map),
                %unit,
                "Unit doesn't move if in range of a target"),

        my @test2 = qq:to/END/.trim().split("\n").map({ $_.trim().comb });
            #####
            #EG.#
            #G.G#
            #####
            END

        my ($units2, $map2) = parse(@test2);
        my %unit2 = $units2.grep({ $_<id> == 4 })[0];
        is(move(%unit2, $units2, $map2),
                %unit2,
                "Unit doesn't move if it can't find a path to an enemy"),
    }
}

foo(@movement-test);