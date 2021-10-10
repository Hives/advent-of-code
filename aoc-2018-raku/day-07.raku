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

    @completed.join('');
};

sub part2(@input, $additional-step-time, $max-workers) {

    my @instructions = parse(@input);
    my @letters = (flat @instructions.map({ $_.value }), @instructions.map({ $_.key })).unique;
    my %requirements = @letters.map(-> $letter {
        $letter => @instructions.grep({ $_.value eq $letter }).map({ $_.key }).list
    });

    my %workers;
    my @completed;
    my @available;

    my $time = 0;

    sub step-time($letter) {
        $letter.parse-base(36) - 9 + $additional-step-time;
    }

    sub update-available() {
        my @newly-available = %requirements.kv.grep(-> $, $reqs {
            $reqs.map(-> $req { $req ∈ @completed }).all
        })
                .map({ $_[0] })
                .grep(-> $job { $job !∈ @completed && $job !∈ %workers.keys && $job !∈ @available });

        for @newly-available { @available.append($_) }
        @available = @available.sort;
    }

    loop {
        last if @completed.elems == @letters.elems;

        say "----";

        update-available();
        say "available: {@available}\n";

        while (%workers.keys < $max-workers && @available.elems > 0) {
            my $job = @available.shift;
            say "starting job $job";
            %workers{$job} = step-time($job);
        }

        say "workers:";
        say %workers;
        say "";

        my $least-remaining-time = %workers.values.min;
        for %workers.keys -> $job {
            my $remaining-time = %workers{$job} - $least-remaining-time;
            if $remaining-time == 0 {
                @completed.append($job);
                %workers{$job}:delete;
            } else {
                %workers{$job} = $remaining-time;
            }
        }
        $time += $least-remaining-time;

        say "completed {@completed.join('')} in $time seconds";
    }

    $time;
};

say part2(@test-input-lines, 0, 2);
say part2(@input-lines, 60, 5);
