#! /usr/bin/env rakudo

my @test-input-lines = "[1518-11-01 00:00] Guard #10 begins shift
[1518-11-01 00:05] falls asleep
[1518-11-01 00:25] wakes up
[1518-11-01 00:30] falls asleep
[1518-11-01 00:55] wakes up
[1518-11-01 23:58] Guard #99 begins shift
[1518-11-02 00:40] falls asleep
[1518-11-02 00:50] wakes up
[1518-11-03 00:05] Guard #10 begins shift
[1518-11-03 00:24] falls asleep
[1518-11-03 00:29] wakes up
[1518-11-04 00:02] Guard #99 begins shift
[1518-11-04 00:36] falls asleep
[1518-11-04 00:46] wakes up
[1518-11-05 00:03] Guard #99 begins shift
[1518-11-05 00:45] falls asleep
[1518-11-05 00:55] wakes up".split("\n");

my $input-file = open "inputs/day_04_sorted.txt", :r;
my @input-lines = $input-file.slurp.trim().split("\n");
$input-file.close;

sub parse-line($line) {
    if $line ~~ /'['(\d+)'-'(\d+)'-'(\d+)' '(\d+)':'(\d+)'] '(.+)/ {
        my $activity =
                $5 eq "falls asleep" ?? "SLEEP" !!
                $5 eq "wakes up" ?? "WAKE" !!
                "START";

        my $id = $activity eq "START" ?? $5.split(" ")[1].substr(1) !! Nil;

        return {
            minute => +$4,
            :$activity,
            :$id
        }
    }
}

sub go(@input) {
    my @activity = @input.map: { parse-line($_) };

    my %deets := {};
    my $id;
    my $sleep-minute;

    for @activity {
        my %observation = $_;
        given %observation{'activity'} {
            when "START" {
                $id = %observation{'id'};
                if !($id ∈ %deets) {
                    %deets{$id} := {total => 0, minutes => {}};
                }
            };
            when "SLEEP" {
                $sleep-minute = %observation{'minute'}
            };
            when "WAKE" {
                my $wake-minute = %observation{'minute'};
                my $nap-time = $wake-minute - $sleep-minute;
                %deets{$id}{'total'} += $nap-time;

                for $sleep-minute ..^ $wake-minute -> $m {
                    %deets{$id}{'minutes'}{$m} += 1;
                }
            };
        };
    }

    say "Part 1";
    my $sleepy-deets = %deets.max({$_.value{'total'}}); 
    my $sleepy-id = $sleepy-deets.key;
    say "Sleepiest guard: " ~ $sleepy-id;
    my $sleepy-minute = $sleepy-deets.value{'minutes'}.max({$_.value}).key;
    say "Sleepiest minute: " ~ $sleepy-minute;

    say "Answer: " ~ ($sleepy-id * $sleepy-minute);

    say "Part 2";
    my $sleepy-deets2 = %deets
        .grep({$_.value{'total'} > 0})
        .max({$_.value{'minutes'}.max({$_.value}).value});
    my $sleepy-id2 = $sleepy-deets2.key;
    say "Sleepiest guard: " ~ $sleepy-id2;
    my $sleepy-minute2 = $sleepy-deets2.value{'minutes'}.max({$_.value}).key;
    say "Sleepiest minute: " ~ $sleepy-minute2;

    say "Answer: " ~ ($sleepy-id2 * $sleepy-minute2);
}

go(@input-lines)
