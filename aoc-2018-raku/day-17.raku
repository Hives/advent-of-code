#! /usr/bin/env rakudo

my $input-file = open "inputs/day_17.txt", :r;
my @input-lines = $input-file.slurp.trim().split("\n");
$input-file.close;

my $test-input-file = open "inputs/day_17_test.txt", :r;
my @test-input-lines = $test-input-file.slurp.trim().split("\n");
$test-input-file.close;

sub create-map(@input) {
    my @points;

    for @input -> $row {
        if $row ~~ /(x | y)'='(\d+)','.*'='(\d+)'..'(\d+)/ {
            say "$0 $1 $2 $3";
            for +$2 .. +$3 -> $i {
                if $0 eq 'x' {
                    @points.append($(+$1, $i));
                } else {
                    @points.append($($i, +$1));
                }
            }
        }
    }

    my @xs = @points.map({ $_[0] });
    my @ys = @points.map({ $_[1] });

    my $max-x = @xs.max() + 1;
    my $min-x = @xs.min() - 1;

    my @map;

    for 0 .. @ys.max() -> $y {
        my @row = ('.' for $min-x .. $max-x);
        my @xs-in-row = @points.grep({ $_[1] == $y }).map({ $_[0] });
        for @xs-in-row -> $x {
            @row[$x - $min-x] = '#';
        }
        if $y == 0 {
            @row[500 - $min-x] = '+';
        }
        @map.append($(@row));
    }

    my $fh;
    $fh = open 'map.txt', :w;
    for @map -> $row {
        $fh.say($row.join)
    }
    $fh.close;
}

create-map(@input-lines);

# this converts the input into a text file image of the map.
# i worked the rest out by hand 🙃
