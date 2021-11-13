#! /usr/bin/env rakudo

use Test;
use ValueType;

my $input-file = open "inputs/day_15.txt", :r;
my @input = $input-file.slurp.trim().split("\n").map({ $_.trim().comb });
$input-file.close;

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

my @combat-test = qq:to/END/.trim().split("\n").map({ $_.trim().comb });
#######
#.G...#
#...EG#
#.#.#G#
#..G#E#
#.....#
#######
END

my @acceptance-test2 = qq:to/END/.trim().split("\n").map({ $_.trim().comb });
#######
#G..#E#
#E#E.E#
#G.##.#
#...#E#
#...E.#
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
    my $unit-count = 0;
    for 0 ..^ @input.elems -> $y {
        my $row;
        for 0 ..^ @input[$y].elems -> $x {
            my $square = @input[$y][$x];
            if $square eq "E" || $square eq "G" {
                @units.append($%(
                    :id($unit-count += 1),
                    :type($square),
                    :location(pnt($x, $y)),
                    :hitpoints(200),
                    :attack-power(3)
                ));
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

sub move($id, $units-map is copy, @map) {
    return $units-map if ("$id" ∉ $units-map.keys);

    my %unit = $units-map{$id};
    my @enemy-locations = $units-map.values
            .grep({ $_<type> !eq %unit<type> })
            .map({ $_<location> });
    return $units-map if %unit<location> (elem) neighbours(@enemy-locations, @map);

    my $nearest-target = find-nearest-target(%unit, $units-map.values, @map);
    return $units-map if !$nearest-target;

    my $next-step = find-next-step(%unit, $nearest-target, $units-map.values, @map);
    %unit<location> = $next-step;
    $units-map{$id} = %unit;
    return $units-map;
}

sub attack($id, $units-map is copy, @map) {
    return $units-map if ("$id" ∉ $units-map.keys);

    my %unit = $units-map{$id};
    my $neighbouring-squares = neighbours((%unit<location>,), @map).List;
    my @enemies = $units-map.values.grep({ $_<type> !eq %unit<type> });
    my @attackable-enemies = @enemies.grep({ $_<location> ∈ $neighbouring-squares });

    return $units-map if !@attackable-enemies;

    my $lowest-hitpoints = @attackable-enemies
            .map({ $_<hitpoints> }).min;
    my @weakest-attackable-enemies = @attackable-enemies
            .grep({ $_<hitpoints> == $lowest-hitpoints });

    my %attacked-enemy = @weakest-attackable-enemies.&order-units[0];
    %attacked-enemy<hitpoints> -= %unit<attack-power>;
    if (%attacked-enemy<hitpoints> <= 0) {
        $units-map = %(@$units-map.grep({ $_.key !eq %attacked-enemy<id> }));
    } else {
        $units-map{%attacked-enemy<id>} = %attacked-enemy;
    }
    return $units-map;
}

sub unit-stats(%unit) {
    "{ %unit<type> }({ %unit<hitpoints> })"
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
        my $index = $col % 10;
        my $row = @map-mutable[$col].join;
        my $unit-stats = @units
                .grep({ $_<location>.y == $col })
                .sort({ $_<location>.x })
                .map(&unit-stats)
                .join(", ");
        say "$index $row $unit-stats"
    }
}

sub foo(@input, $printy) {
    my ($units, $map) = parse(@input);
    $printy && printy($map, $units);

    my $round = 0;
    loop {
        last if !$units.cache.grep({ $_<type> eq "E" }) || !$units.cache.grep({ $_<type> eq "G" });

        $round++;

        my @order = order-units($units).map({ $_<id> });
        my $units-map = Map.new($units.map({ $_<id> => $_ }));
        for @order -> $id {
            $units-map = move($id, $units-map, $map);
            $units-map = attack($id, $units-map, $map);
        }
        $units = $units-map.values;
        $printy && printy($map, $units);
    }

    my $evaluation = ($round - 1) * $units.map({ $_<hitpoints> }).sum;
    $printy && say "last round: $round";
    $printy && say "evaluation: $evaluation";
    return $evaluation;
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
        subtest "Unit moves towards enemy", {
            my @input = qq:to/END/.trim().split("\n").map({ $_.trim().comb });
                #####
                #E..#
                #..G#
                #####
                END

            my ($units, $map) = parse(@input);
            my $units-map = Map.new($units.map({ $_<id> => $_ }));
            my $result = move(1, $units-map, $map);

            $result<1><location>.&is: pnt(2, 1)
        }

        subtest "Unit doesn't move if in range of a target", {
            my @input = qq:to/END/.trim().split("\n").map({ $_.trim().comb });
                #####
                #EG.#
                #..G#
                #####
                END

            my ($units, $map) = parse(@input);
            my $units-map = Map.new($units.map({ $_<id> => $_ }));
            my $moved-units-map = move(1, $units-map, $map);

            $moved-units-map<1><location>.&is: pnt(1, 1)
        }

        subtest "Unit doesn't move if it can't find a path to an enemy", {
            my @input = qq:to/END/.trim().split("\n").map({ $_.trim().comb });
                #####
                #EG.#
                #G.G#
                #####
                END

            my ($units, $map) = parse(@input);
            my $units-map = Map.new($units.map({ $_<id> => $_ }));
            my $moved-units-map = move(1, $units-map, $map);

            $moved-units-map<1><location>.&is: pnt(1, 1)
        }

        subtest "Leaves as-is if unit doesn't exist", {
            my @input = qq:to/END/.trim().split("\n").map({ $_.trim().comb });
                #####
                #E..#
                #..G#
                #####
                END

            my ($units, $map) = parse(@input);
            my $units-map = Map.new($units.map({ $_<id> => $_ }));
            my $moved-units-map = move(100, $units-map, $map);

            $moved-units-map.&is: $units-map;
        }
    }

    subtest 'Attack', {
        subtest "Does nothing if unit doesn't exist", {
            my @input = qq:to/END/.trim().split("\n").map({ $_.trim().comb });
                #####
                #EE.#
                #..G#
                #####
                END

            my ($units, $map) = parse(@input);
            my $units-map = Map.new($units.map({ $_<id> => $_ }));
            my $result = attack(100, $units-map, $map);

            $result.&is: $units-map;
        }

        subtest "Does nothing if no enemy in range", {
            my @input = qq:to/END/.trim().split("\n").map({ $_.trim().comb });
                #####
                #EE.#
                #..G#
                #####
                END

            my ($units, $map) = parse(@input);
            my $units-map = Map.new($units.map({ $_<id> => $_ }));
            my $result = attack(1, $units-map, $map);

            $result.&is: $units-map;
        }

        subtest "Reduces enemy hitpoints by attack power", {
            my @input = qq:to/END/.trim().split("\n").map({ $_.trim().comb });
                #####
                #EG.#
                #...#
                #####
                END

            my ($units, $map) = parse(@input);
            my $units-map = Map.new($units.map({ $_<id> => $_ }));
            my $result = attack(1, $units-map, $map);

            $result<2><hitpoints>.&is: 197;
        }

        subtest "Picks enemy with lowest hitpoints", {
            my @input = qq:to/END/.trim().split("\n").map({ $_.trim().comb });
                #####
                #.E.#
                #EGE#
                #.E.#
                #####
                END

            my ($units, $map) = parse(@input);
            my $units-map = Map.new($units.map({ $_<id> => $_ }));
            $units-map<5><hitpoints> = 199;
            my $result = attack(3, $units-map, $map);

            $result<1><hitpoints>.&is: 200;
            $result<2><hitpoints>.&is: 200;
            $result<4><hitpoints>.&is: 200;
            $result<5><hitpoints>.&is: 196;
        }

        subtest "Picks enemy by reading order if they have same hitpoints", {
            my @input = qq:to/END/.trim().split("\n").map({ $_.trim().comb });
                #####
                #.E.#
                #EGE#
                #.E.#
                #####
                END

            my ($units, $map) = parse(@input);
            my $units-map = Map.new($units.map({ $_<id> => $_ }));
            $units-map<4><hitpoints> = 199;
            $units-map<5><hitpoints> = 199;
            my $result = attack(3, $units-map, $map);

            $result<1><hitpoints>.&is: 200;
            $result<2><hitpoints>.&is: 200;
            $result<4><hitpoints>.&is: 196;
            $result<5><hitpoints>.&is: 199;
        }

        subtest "Removes enemy if hitpoints < 0", {
            my @input = qq:to/END/.trim().split("\n").map({ $_.trim().comb });
                        #####
                        #GE.#
                        #####
                        END

            my ($units, $map) = parse(@input);
            my $units-map = Map.new($units.map({ $_<id> => $_ }));
            $units-map<2><hitpoints> = 2;
            my $result = attack(1, $units-map, $map);

            ($result<2>:exists).&is: False;
        }
    }

    subtest "Acceptance tests", {
        subtest "1", {
            my @input = qq:to/END/.trim().split("\n").map({ $_.trim().comb });
                        #######
                        #.G...#
                        #...EG#
                        #.#.#G#
                        #..G#E#
                        #.....#
                        #######
                        END

            foo(@input, False).&is: 27730;
        }

        subtest "2", {
            my @input = qq:to/END/.trim().split("\n").map({ $_.trim().comb });
                        #######
                        #G..#E#
                        #E#E.E#
                        #G.##.#
                        #...#E#
                        #...E.#
                        #######
                        END

            foo(@input, False).&is: 36334;
        }

        subtest "3", {
            my @input = qq:to/END/.trim().split("\n").map({ $_.trim().comb });
                        #######
                        #E..EG#
                        #.#G.E#
                        #E.##E#
                        #G..#.#
                        #..E#.#
                        #######
                        END

            foo(@input, False).&is: 39514;
        }

        subtest "4", {
            my @input = qq:to/END/.trim().split("\n").map({ $_.trim().comb });
                        #######
                        #E.G#.#
                        #.#G..#
                        #G.#.G#
                        #G..#.#
                        #...E.#
                        #######
                        END

            foo(@input, False).&is: 27755;
        }

        subtest "5", {
            my @input = qq:to/END/.trim().split("\n").map({ $_.trim().comb });
                        #######
                        #.E...#
                        #.#..G#
                        #.###.#
                        #E#G#G#
                        #...#G#
                        #######
                        END

            foo(@input, False).&is: 28944;
        }

        subtest "6", {
            my @input = qq:to/END/.trim().split("\n").map({ $_.trim().comb });
                        #########
                        #G......#
                        #.E.#...#
                        #..##..G#
                        #...##..#
                        #...#...#
                        #.G...G.#
                        #.....G.#
                        #########
                        END

            foo(@input, False).&is: 18740;
        }

    }
}

foo(@input, True);