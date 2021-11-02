#! /usr/bin/env rakudo

my $input-file = open "inputs/day_12.txt", :r;
my @input-lines = $input-file.slurp.trim().split("\n");
$input-file.close;

my $test-file = open "inputs/day_12_test.txt", :r;
my @test-lines = $test-file.slurp.trim().split("\n");
$test-file.close;

sub parse_input(@lines) {
    my $initial_state = @lines[0].split(": ")[1];
    my $rules = @lines[2 ..*].grep({ $_.contains("=> #") }).map({ $_.split(" => ") }).Set;
    return $initial_state, $rules
}

sub one_step($state, $first_index, $rules) {
    my $expanded_state = "....$state....";
    my $next = (0 .. ($expanded_state.chars - 5))
            .map(-> $n { substr($expanded_state, $n, 5) })
            .map(-> $chunk { $chunk ∈ $rules ?? "#" !! "." })
            .join;
    my $next_trimmed = ($next ~~ rx/ '#'.*'#' /).Str;

    return $next_trimmed, ($first_index + $next.index("#") - 2);
}

sub iterate($initial, $rules, $iterations) {
    say $initial;
    my $current = $initial;
    my $first_index = 0;
    for ^$iterations {
        ($current, $first_index) = one_step($current, $first_index, $rules);
        my $n = $_ + 1;
        # after a certain point the plants just move one pot to the right every turn.
        # after this point the magic formula below gives the score.
        # manually evaluate the magic formula for n = 50,000,000,000 to get part 2.
        my $magic_formula = $n * 42 + 428;
        say "$n: $first_index, $current";
        say "$magic_formula ~ {sum_plant_values($current, $first_index)}";
    }
    return $current, $first_index;
}

sub sum_plant_values(Str $state, $first_index) {
    reduce -> $acc, $next {
        my ($pot, $index) = $next;
        $pot eq "#" ?? $acc + $index !! $acc
    }, 0, |zip($state.comb, $first_index ..*)
}

sub go(@input, $iterations) {
    my ($initial, $rules) = parse_input(@input);
    my ($final, $first_index) = iterate($initial, $rules, $iterations);
    sum_plant_values($final, $first_index)
}

sub part1() {
    say go(@input-lines, 20)
}

part1();
say go(@input-lines, 125);