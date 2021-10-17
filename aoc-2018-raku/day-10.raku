#! /usr/bin/env rakudo

my $test-file = open "inputs/day_10_test.txt", :r;
my @test-lines = $test-file.slurp.trim().split("\n");
$test-file.close;

my $input-file = open "inputs/day_10.txt", :r;
my @input-lines = $input-file.slurp.trim().split("\n");
$input-file.close;

sub parse-row($row) {
    if $row ~~ rx{ 'position=<'(.*)','(.*)'> velocity=<'(.*)','(.*)'>' } {
        { pos => [+$0, +$1], vel => [+$2, +$3] }
    }
}

sub calculate-positions(@input, $time) {
    @input.map: -> % (:@pos, :@vel) {
        @pos[0] + @vel[0] * $time,
        @pos[1] + @vel[1] * $time
    }
}

sub get-range(@positions) {
    my $xs = @positions.map({ $_[0] }).list;
    my $ys = @positions.map({ $_[1] }).list;
    ($xs.min, $ys.min), ($xs.max, $ys.max)
}

sub get-size(@positions) {
    my ($top-left, $bottom-right) = get-range(@positions);
    my $width = $bottom-right[0] - $top-left[0] + 1;
    my $height = $bottom-right[1] - $top-left[1] + 1;
    return $width * $height;
}

sub printy(@positions) {
    my ($top-left, $bottom-right) = get-range(@positions);
    my $width = $bottom-right[0] - $top-left[0] + 1;
    my $height = $bottom-right[1] - $top-left[1] + 1;

    my @output = gather {
        for ^$height {
            take (^$width).list.map({ "." }).Array;
        }
    }

    for @positions -> @ ($x, $y) {
        @output[$y - $top-left[1]][$x - $top-left[0]] = "#";
    }

    for @output { $_.join(" ").say }
}

sub go() {
    my @input = @input-lines.map(&parse-row);
    my @positionses;
    my $t = 10_000;
    @positionses[$t] = [calculate-positions(@input, $t)];
    loop {
        if $t %% 100 {
            say "t = $t";
            say "size = {get-size(@positionses[$t])}"
        }
        @positionses[$t + 1] = calculate-positions(@input, $t + 1);
        last if get-size(@positionses[$t + 1]) > get-size(@positionses[$t]);
        $t++
    }
    printy(@positionses[$t]);
    say $t;
}

go();
