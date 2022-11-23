#! /usr/bin/env rakudo

my $input-file = open "inputs/day_25.txt", :r;
my @input-lines = $input-file.slurp.trim().split("\n");
$input-file.close;

sub parse-line($line) {
    $line.split(",").map({ +$_ })
}

sub manhattan-distance($point1, $point2) {
    (^4).map({ abs($point1[$_] - $point2[$_]) }).sum
}

my $constellation-count = 0;

my @outstanding-points = @input-lines.map({ parse-line($_) });

while @outstanding-points.elems > 0 {
    my @current-constellation = @outstanding-points.pop();

    while @current-constellation.elems > 0 {
        my $current = @current-constellation.pop();
        my @adjacent = @outstanding-points.grep({ manhattan-distance($current, $_) <= 3 });
        @outstanding-points = @outstanding-points.grep({ manhattan-distance($current, $_) > 3 });
        for @adjacent { @current-constellation.push($_) }
    }

    $constellation-count+= 1;
}

say $constellation-count;
