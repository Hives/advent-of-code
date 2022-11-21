#! /usr/bin/env rakudo

my $input-file = open "inputs/day_23.txt", :r;
my @input-lines = $input-file.slurp.trim().split("\n");
$input-file.close;

my $test-input-file = open "inputs/day_23_test.txt", :r;
my @test-input-lines = $test-input-file.slurp.trim().split("\n");
$test-input-file.close;

my $test2-input-file = open "inputs/day_23_test_2.txt", :r;
my @test2-input-lines = $test2-input-file.slurp.trim().split("\n");
$test2-input-file.close;

class Point {
    has Int $.x;
    has Int $.y;
    has Int $.z;

    method manhattan() {
        return self.x + self.y + self.z;
    }
}

class Box {
    has Int $.x-min;
    has Int $.x-max;
    has Int $.y-min;
    has Int $.y-max;
    has Int $.z-min;
    has Int $.z-max;

    method is-unit() {
        return self.x-min eq self.x-max && self.y-min eq self.y-max && self.z-min eq self.z-max
    }

    method top-left-back() {
        return Point.new(
                x => self.x-min,
                y => self.y-min,
                z => self.z-min
                )
    }

    method octants() {
        my $size = self.x-max - self.x-min;

        sub mid-point-floor($min, $max) {
            (($max - $min) / 2).floor + $min
        }

        return (
        # back top left
        Box.new(
                x-min => self.x-min,
                x-max => mid-point-floor(self.x-min, self.x-max),

                y-min => self.y-min,
                y-max => mid-point-floor(self.y-min, self.y-max),

                z-min => self.z-min,
                z-max => mid-point-floor(self.z-min, self.z-max),
                ),

        # back top right
        Box.new(
                x-min => mid-point-floor(self.x-min, self.x-max) + 1,
                x-max => self.x-max,

                y-min => self.y-min,
                y-max => mid-point-floor(self.y-min, self.y-max),

                z-min => self.z-min,
                z-max => mid-point-floor(self.z-min, self.z-max),
                ),

        # back bottom left
        Box.new(
                x-min => self.x-min,
                x-max => mid-point-floor(self.x-min, self.x-max),

                y-min => mid-point-floor(self.y-min, self.y-max),
                y-max => self.y-max,

                z-min => self.z-min,
                z-max => mid-point-floor(self.z-min, self.z-max),
                ),

        # back bottom right
        Box.new(
                x-min => mid-point-floor(self.x-min, self.x-max) + 1,
                x-max => self.x-max,

                y-min => mid-point-floor(self.y-min, self.y-max) + 1,
                y-max => self.y-max,

                z-min => self.z-min,
                z-max => mid-point-floor(self.z-min, self.z-max),
                ),

        # front top left
        Box.new(
                x-min => self.x-min,
                x-max => mid-point-floor(self.x-min, self.x-max),

                y-min => self.y-min,
                y-max => mid-point-floor(self.y-min, self.y-max),

                z-min => mid-point-floor(self.z-min, self.z-max) + 1,
                z-max => self.z-max,
                ),

        # front top right
        Box.new(
                x-min => mid-point-floor(self.x-min, self.x-max) + 1,
                x-max => self.x-max,

                y-min => self.y-min,
                y-max => mid-point-floor(self.y-min, self.y-max),

                z-min => mid-point-floor(self.z-min, self.z-max) + 1,
                z-max => self.z-max,
                ),

        # front bottom left
        Box.new(
                x-min => self.x-min,
                x-max => mid-point-floor(self.x-min, self.x-max),

                y-min => mid-point-floor(self.y-min, self.y-max) + 1,
                y-max => self.y-max,

                z-min => mid-point-floor(self.z-min, self.z-max) + 1,
                z-max => self.z-max,
                ),

        # front bottom right
        Box.new(
                x-min => mid-point-floor(self.x-min, self.x-max) + 1,
                x-max => self.x-max,

                y-min => mid-point-floor(self.y-min, self.y-max) + 1,
                y-max => self.y-max,

                z-min => mid-point-floor(self.z-min, self.z-max) + 1,
                z-max => self.z-max,
                ),
        )
    }

    method is-in-range-of($nanobot) {
        my $p = $nanobot.position;

        my $d-x;
        if ($p.x > self.x-max) {
            $d-x = $p.x - self.x-max
        } elsif ($p.x < self.x-min) {
            $d-x = self.x-min - $p.x
        } else {
            $d-x = 0;
        }

        my $d-y;
        if ($p.y > self.y-max) {
            $d-y = $p.y - self.y-max
        } elsif ($p.y < self.y-min) {
            $d-y = self.y-min - $p.y
        } else {
            $d-y = 0;
        }

        my $d-z;
        if ($p.z > self.z-max) {
            $d-z = $p.z - self.z-max
        } elsif ($p.z < self.z-min) {
            $d-z = self.z-min - $p.z
        } else {
            $d-z = 0;
        }

        my $distance = $d-x + $d-y + $d-z;

        return $distance <= $nanobot.range;
    }
}

sub count-intersections($box, @nanobots) {
    return @nanobots.grep(-> $nanobot { $box.is-in-range-of($nanobot) }).elems;
}

sub get-big-box(@nanobots) {
    my $min-x = 0;
    my $min-y = 0;
    my $min-z = 0;
    my $max-x = 0;
    my $max-y = 0;
    my $max-z = 0;
    for @nanobots -> $nanobot {
        my $p = $nanobot.position;
        if $p.x > $max-x { $max-x = $p.x }
        if $p.x < $min-x { $min-x = $p.x }
        if $p.y > $max-y { $max-y = $p.y }
        if $p.y < $min-y { $min-y = $p.y }
        if $p.z > $max-z { $max-z = $p.z }
        if $p.z < $min-z { $min-z = $p.z }
    }
    return Box.new(
            x-min => $min-x,
            x-max => $max-x,
            y-min => $min-y,
            y-max => $max-y,
            z-min => $min-z,
            z-max => $max-z,
            )
}

class Nanobot {
    has Str $.id;
    has Point $.position;
    has Int $.range;

    method range-includes($point) {
        return abs(self.position.x - $point.x) + abs(self.position.y - $point.y) + abs(self.position.z - $point
                .z) <= self.range
    }
}

sub parse($input) {
    sub parse-line($index, $input-line) {
        my $r = $input-line ~~ /'pos=<' (\-?\d+) ',' (\-?\d+) ',' (\-?\d+) '>, r=' (\d+)/;
        return Nanobot.new(
                id => "$index",
                position => Point.new(
                        x => +$r[0],
                        y => +$r[1],
                        z => +$r[2]),
                range => +$r[3]);
    }
    return $input.kv.map(-> $index, $line { parse-line($index, $line) });
}

sub part_1(@nanobots) {
    my $strongest = @nanobots.max({ $_.range });
    return @nanobots.grep({ $strongest.range-includes($_.position) }).elems
}

sub part_2(@nanobots) {
    sub get_best_octant($box) {
        my @octants = $box.octants();
        return @octants.max(-> $octant { @nanobots.grep(-> $nanobot { $octant.is-in-range-of($nanobot) }).elems });
    }

    my @queue;
    my $best-box;
    my $best-score = 0;
    my $distance = 9999999999;

    $best-box = get-big-box(@nanobots);

    loop {
        my $score;

        my @octants = $best-box.octants();

        for @octants -> $octant {
            @queue.push(($octant, count-intersections($octant, @nanobots)),)
        }

        @queue = @queue.grep({ $_[1] > $best-score });
        last if @queue.elems eq 0;
        @queue = @queue.sort({ $_[1] });

        repeat {
            ($best-box, $score) = @queue.pop();
            if ($best-box.is-unit()) {
                my $best-box-distance = $best-box.top-left-back().manhattan();
                if ($score >= $best-score && $best-box-distance < $distance) {
                    $best-score = $score;
                    $distance = $best-box-distance;
                }
            }
        } while $best-box.is-unit();
    }

    return $distance;
}

my @nanobots = parse(@input-lines);

say part_1(@nanobots);
say part_2(@nanobots);
