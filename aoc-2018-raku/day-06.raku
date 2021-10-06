#! /usr/bin/env rakudo

my $input-file = open "inputs/day_06.txt", :r;
my @input-lines = $input-file.slurp.trim().split("\n");
$input-file.close;

my @test-input-lines = "1, 1
1, 6
8, 3
3, 4
5, 5
8, 9".split("\n");

sub parse(@lines) {
    return @lines.map({ $_.split(", ").map({ +$_ }) });
}

sub distance(@point1, @point2) {
    return abs(@point2[0] - @point1[0]) + abs(@point2[1] - @point1[1]);
}

sub find-closest(@point, @points) {
    my %distances = @points.map({ $_ => distance(@point, $_) });
    my $min-distance = %distances.map({ $_.value }).min();
    my $closest = %distances.grep({ $_.value == $min-distance });
    if $closest.elems == 1 {
        return $closest[0].key;
    }
    return Nil;
}

sub part1(@input) {
    my @points = parse(@input);

    my $min-x = @points.min({ $_[0] })[0];
    my $max-x = @points.max({ $_[0] })[0];
    my $min-y = @points.min({ $_[1] })[1];
    my $max-y = @points.max({ $_[1] })[1];

    my $closest-points = {};

    for $min-y .. $max-y -> $y {
        say "y: $y";
        for $min-x .. $max-x -> $x {
            $closest-points{"$x $y"} = find-closest([$x, $y], @points);
        }
    }

    my @points-whose-areas-touch-the-edges = $closest-points.grep({
        my ($x, $y) = $_.key.split(" ").map({ +$_ });
        $x == $min-x || $x == $max-x || $y == $min-y || $y == $max-y
    }).map({ $_.value }).grep({ $_.DEFINITE });

    say @points-whose-areas-touch-the-edges;
    say @points;

    my @points-whose-areas-dont-touch-the-edges = @points.grep({ !("$_" (elem) @points-whose-areas-touch-the-edges) });

    return @points-whose-areas-dont-touch-the-edges.map(-> $point {
        $closest-points.grep({
            $_.value.DEFINITE && $_.value eq "$point";
        }).elems
    }).max();
}

sub part2(@input, $total-distance) {
    my @points = parse(@input);

    my $min-x = @points.min({ $_[0] })[0];
    my $max-x = @points.max({ $_[0] })[0];
    my $min-y = @points.min({ $_[1] })[1];
    my $max-y = @points.max({ $_[1] })[1];

    say $min-x ~ ", " ~ $max-x ~ ", " ~ $min-y ~ ", " ~ $max-y;

    my $total-distances = {};

    for $min-y .. $max-y -> $y {
        say "y: $y";
        for $min-x .. $max-x -> $x {
            $total-distances{"$x $y"} = @points.map({ distance([$x, $y], $_) }).sum();
        }
    }

    say $total-distances.grep({ $_.value < $total-distance }).elems;
}

#say part1(@input-lines)
say part2(@input-lines, 10_000)