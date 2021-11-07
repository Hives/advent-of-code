#! /usr/bin/env rakudo

my $input-file = open "inputs/day_13.txt", :r;
my @input = $input-file.slurp.split("\n").map({ $_.comb });
$input-file.close;

my @test-input = "/->-\\
|   |  /----\\
| /-+--+-\\  |
| | |  | v  |
\\-+-/  \\-+--/
  \\------/".split("\n").map({ $_.comb });

my @test-input2 = "/>-<\\
|   |
| /<+-\\
| | | v
\\>+</ |
  |   ^
  \\<->/".split("\n").map({ $_.comb });

sub get-carts(@map) {
    my @cart-chars = '^', '>', 'v', '<';
    my @carts;
    my $n = 0;
    for 0 .. @map.elems -> $y {
        for 0 .. @map[$y].elems -> $x {
            if @map[$y][$x] ∈ @cart-chars {
                my $cart = @map[$y][$x];
                my @direction =
                        $cart eq '^' ?? (0, -1) !!
                        $cart eq '>' ?? (1, 0) !!
                                $cart eq 'v' ?? (0, 1) !!
                                (-1, 0);
                @carts.append($%(location => ($x, $y), :@direction, next-turn => 'left', id => $n += 1))
            }
        }
    }
    return @carts;
}

sub sort-carts(@carts) {
    @carts.sort: { $^a<y>, $^a<x> }
}

sub turn-left(@direction) {
    my ($x, $y) = @direction;
    ($y, -$x)
}

sub turn-right(@direction) {
    my ($x, $y) = @direction;
    (-$y, $x)
}

sub add-vectors(@v1, @v2) {
    (@v1[0] + @v2[0], @v1[1] + @v2[1])
}

sub move-cart(%cart, @map) {
    my $location = %cart<location>;
    my $direction = %cart<direction>;
    my $next-turn = %cart<next-turn>;
    my $id = %cart<id>;

    $location = add-vectors($location, $direction);

    my $track = @map[$location[1]][$location[0]];

#    if $id == 4 {
#        say %cart;
#        say $track;
#    }

    given $track {
        when '/' {
            if $direction[0] == 0 {
                $direction = turn-right($direction)
            } else {
                $direction = turn-left($direction)
            }
        }
        when '\\' {
            if $direction[0] == 0 {
                $direction = turn-left($direction)
            } else {
                $direction = turn-right($direction)
            }
        }
        when '+' {
            given $next-turn {
                when 'left' {
                    $direction = turn-left($direction);
                    $next-turn = 'straight';
                }
                when 'straight' {
                    $next-turn = 'right';
                }
                when 'right' {
                    $direction = turn-right($direction);
                    $next-turn = 'left';
                }
                default {}
            }
        }
        default {}
    }

    return %(:$location, :$direction, :$next-turn, :$id)
}

sub tick1(@map, @carts) {
    my @sorted-carts = sort-carts(@carts);
    for 0 ..^ @sorted-carts.elems -> $n {
        my $moved-cart = move-cart(@sorted-carts[$n], @map);
        if @sorted-carts.map({ $_<location> }).grep($moved-cart<location>) {
            die "Boom: { $moved-cart<location> }"
        }
        @sorted-carts[$n] = $moved-cart;
    }
    @sorted-carts;
}

sub remove-crashed(@carts) {
    my @locations = @carts.map({ $_<location> });
    my @new-carts = @carts.grep(-> $cart { @locations.grep($cart<location>).elems == 1 });
    if @new-carts.elems < @carts.elems {
        say "💣 Boom"
    }
    @new-carts;
}

sub tick2(@map, @carts) {
    my @sorted-carts = sort-carts(@carts);

    for 0 ..^ @sorted-carts.elems -> $n {
        my $moved-cart = move-cart(@sorted-carts[$n], @map);
        @sorted-carts[$n] = $moved-cart;
    }

    remove-crashed(@sorted-carts);
}

sub part1(@input) {
    my @carts = get-carts(@input);
    loop {
        @carts = tick1(@input, @carts);
    }
}

sub part2(@input) {
    my @carts = get-carts(@input);
    say "Total carts: {@carts.elems}";
    my $i = 0;
    loop {
        $i += 1;
        if $i %% 500 { say $i }
        @carts = tick2(@input, @carts);
        if @carts.elems == 1 {
            return @carts;
        }
    }
}

#part1(@input);
say part2(@test-input2);
#say part2(@input);
