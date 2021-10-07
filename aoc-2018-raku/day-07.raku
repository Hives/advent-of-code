#! /usr/bin/env rakudo

my $input-file = open "inputs/day_07.txt", :r;
my @input-lines = $input-file.slurp.trim().split("\n");
$input-file.close;

my @test-input-lines = "Step C must be finished before step A can begin.
Step C must be finished before step F can begin.
Step A must be finished before step B can begin.
Step A must be finished before step D can begin.
Step B must be finished before step E can begin.
Step D must be finished before step E can begin.
Step F must be finished before step E can begin.".split("\n");

sub parse(@lines) {
    @lines.map({ $_.split(" ") }).map({ ($_[1] => $_[7]) });
};

sub part1(@input) {
    my @instructions = parse(@input);

    my @letters = (flat @instructions.map({ $_.value }), @instructions.map({ $_.key })).unique;

    my %requirements = @letters.map(-> $letter {
        $letter => @instructions.grep({ $_.value eq $letter }).map({ $_.key })
    });

    my @available = %requirements.grep({ $_.value.elems == 0 }).map({ $_.key });
    
    my @completed;

    while @available.elems > 0 {
        @available = @available.sort.reverse;
        @completed.push(@available.pop);

        for @letters.grep({ $_ !∈ @completed && $_ !∈ @available }) -> $letter {
            if %requirements{$letter}.map({ $_ ∈ @completed }).all {
                @available.push($letter)
            }
        }
        @available = @available.unique;
    }

    @completed.join();
};

say part1(@input-lines);