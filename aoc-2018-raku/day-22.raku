#! /usr/bin/env rakudo

#my $depth = 510;
#my $target = (10, 10),;

my $depth = 11541;
my $target = (14, 778),;

#my $buffer = 1;
my $buffer = 50;

my @geologic_indexes = (0 .. ($target[0] + $buffer))».&(-> $x {
    $x * 16807;
}),;

for 1 .. ($target[1] + $buffer) -> $y {
    my @row;
    for 0 .. ($target[0] + $buffer) -> $x {
        if ($x == 0) {
            @row.append($y * 48271);
        } elsif ($x == $target[0] && $y == $target[1]) {
            @row.append(0);
        } else {
            my $erosion_level_left = (@row[$x - 1] + $depth) % 20183;
            my $erosion_level_up = (@geologic_indexes[$y - 1][$x] + $depth) % 20183;
            @row.append($erosion_level_left * $erosion_level_up)
        }
    }
    my $row_list = @row.List;
    @geologic_indexes.append($row_list);
}

my @region_types;

for 0 .. ($target[1] + $buffer) -> $y {
    my @row;
    for 0 .. ($target[0] + $buffer) -> $x {
        my $geologic_index = @geologic_indexes[$y][$x];
        my $erosion_level = ($geologic_index + $depth) % 20183;
        @row.append($erosion_level % 3);
    }
    my $row_list = @row.List;
    @region_types.append($row_list);
}

sub valid_tools($x, $y) {
    my $type = @region_types[$y][$x];
    given $type {
        when 0 { ("climbing_gear", "torch") }
        when 1 { ("climbing_gear", "neither") }
        when 2 { ("torch", "neither") }
        default { die "Unkown tool type: $type" }
    }
}

sub find_neighbours($x, $y) {
    (($x - 1, $y), ($x, $y - 1), ($x + 1, $y), ($x, $y + 1)).grep({
        $_[0] >= 0 && $_[0] <= $target[0] + $buffer && $_[1] >= 0 && $_[1] <= $target[1] + $buffer
    })
}

my $infinity = 1000000000;

my $unvisited = SetHash.new;

for 0 .. (@region_types.List.elems - 1) -> $y {
    for 0 .. (@region_types.List[$y].elems - 1) -> $x {
        my @valid_tools = valid_tools($x, $y);
        for @valid_tools -> $tool {
            my $state = ($x, $y, $tool);
            $unvisited{"$state"} = True;
        }
    }
}

for $unvisited.keys { .say };

sub initial_dists($types) {
    my %dists;
    for 0 .. ($types.elems - 1) -> $y {
        for 0 .. ($types[$y].elems - 1) -> $x {
            my @valid_tools = valid_tools($x, $y);
            for @valid_tools -> $tool {
                my $state = ($x, $y, $tool);
                %dists{$state} = $infinity;
            }
        }
    }
    return %dists;
}

sub distance($state1, $state2) {
    my $curent_tool = $state1[2];
    my $current_valid_tools = valid_tools($state1[0], $state1[1]);
    my $target_tool = $state2[2];
    if ($target_tool eq $curent_tool) {
        1
    } elsif ($target_tool (elem) $current_valid_tools) {
        8
    } else {
        $infinity
    }
}

sub string_to_state($string) {
    my ($x, $y, $tool) = split(" ", $string);
    (+$x, +$y, $tool);
}

my %dists = initial_dists(@region_types.List);

my $target_state = ($target[0], $target[1], "torch");

my $current = (0, 0, "torch");
%dists{$current} = 0;

say "--";

loop {
    say $current;
    say %dists{$current};

    my @neighbours = find_neighbours($current[0], $current[1]);

    for @neighbours -> $neighbour {
        my @valid_tools = valid_tools($neighbour[0], $neighbour[1]);
        for @valid_tools -> $tool {
            my $new_state = ($neighbour[0], $neighbour[1], $tool);
            my $distance = distance($current, $new_state) + %dists{$current};
            if ($distance < %dists{$new_state}) {
                %dists{$new_state} = $distance
            }
        }
    }

    $unvisited.unset("$current");

    my $current_string = $unvisited.keys.min(-> $state {
        %dists{$state};
    });
    $current = string_to_state($current_string);

    last if $current[0] == $target[0] && $current[1] == $target[1]
}

say $current;