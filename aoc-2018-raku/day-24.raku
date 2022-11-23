#! /usr/bin/env rakudo

my $input-file = open "inputs/day_24.txt", :r;
my @input = $input-file.slurp.trim();
$input-file.close;

my $test-input-file = open "inputs/day_24_test.txt", :r;
my @test-input = $test-input-file.slurp.trim();
$test-input-file.close;

my $debug = True;
sub debug($text) {
    say $text if $debug;
}

class Group {
    has Str $.side;
    has Int $.units is rw;
    has Int $.hitpoints-per-unit;
    has Int $.attack-damage is rw;
    has Str $.attack-flavour;
    has Int $.initiative;
    has List $.weaknesses;
    has List $.immunities;

    method effective-power() {
        self.units * self.attack-damage
    }

    method calculate-damage-from(Group $other) {
        return 0 if ($other.attack-flavour ∈ self.immunities);
        return $other.effective-power() * 2 if ($other.attack-flavour ∈ self.weaknesses);
        return $other.effective-power();
    }

    method take-damage-from(Group $other) {
        my $damage = self.calculate-damage-from($other);
        my $dead-units = ($damage / self.hitpoints-per-unit).floor;
        self.units = max(self.units - $dead-units, 0);
    }
}

sub parse-attributes($attributes-string) {
    my %attributes = %('immune' => (), 'weak' => ());
    return %attributes if $attributes-string eq "";
    my $r = $attributes-string ~~ /'(' (.+) ') ' /;
    for "$r[0]".split("; ") {
        my $r2 = $_ ~~ /(.+) ' to ' (.+)/;
        %attributes{"$r2[0]"} = "$r2[1]".split(", ").List
    };
    return %attributes;
}

sub parse-group($side, $line) {
    my $r = $line ~~
            /(\d+) ' units each with ' (\d+) ' hit points ' (.*) 'with an attack that does ' (\d+) ' ' (.+) ' damage at initiative ' (\d+)/;
    my %attributes = parse-attributes("$r[2]");
    debug %attributes;
    return Group.new(
            :$side,
            units => +$r[0],
            hitpoints-per-unit => +$r[1],
            attack-damage => +$r[3],
            attack-flavour => "$r[4]",
            initiative => +$r[5],
            weaknesses => %attributes{'weak'},
            immunities => %attributes{'immune'},
            )
}

sub parse(@input) {
    my ($immune-system, $infection) = @input.split("\n\n");

    my @immune-system-lines = $immune-system.split("\n");
    @immune-system-lines.shift();

    my @infection-lines = $infection.split("\n");
    @infection-lines.shift();

    return %(
        'immune-system', @immune-system-lines.map({ parse-group('Immune System', $_) }),
        'infection', @infection-lines.map({ parse-group('Infection', $_) })
    )
}

sub fight(%armies, $boost) {
    my @immune-system;
    my @infection;
    for @(%armies{'immune-system'}) {
        @immune-system.push($_);
    };
    for @(%armies{'infection'}) {
        @infection.push($_);
    };

    for @immune-system {
        $_.attack-damage += $boost;
    }

    my $round = 0;

    repeat {
        $round+= 1;

        # targeting
        my @immune-system-targets;
        my @infection-targets;
        for @immune-system {
            @immune-system-targets.push($_);
        }
        for @infection {
            @infection-targets.push($_);
        }

        my @attackers-and-targets;

        sub get-targets(@attackers, @defenders) {
            debug "-----------------------------------";
            my @attackers-sorted = @attackers.sort({ -$_.initiative }).sort({ -$_.effective-power() });
            for @attackers-sorted {
                debug "->";
                debug $_;
                debug "{ $_.side } { $_.units }";
                debug $_.effective-power();
            }

            my @defenders-local = @defenders;
            for @attackers-sorted -> $attacker {
                debug "- - - - - -";
                debug "-> attacker:";
                debug $attacker;
                debug "";
                if (@defenders-local.grep({ $_.calculate-damage-from($attacker) > 0 }).elems > 0) {
                    @defenders-local = @defenders-local
                            .sort({ $_.initiative })
                            .sort({ $_.effective-power() })
                            .sort({ $_.calculate-damage-from($attacker) });
                    debug "-> defenders:";
                    for @defenders-local {
                        debug $_;
                        debug "damage: { $_.calculate-damage-from($attacker) }";
                        debug "";
                    }
                    my $target = @defenders-local.pop();
                    debug "-> selected target:";
                    debug $target;
                    @attackers-and-targets.push(($attacker, $target),)
                }
            }
        }
        get-targets(@immune-system, @infection-targets);
        get-targets(@infection, @immune-system-targets);

        # attacking
        debug "---------";
        debug "attackers and targets";
        for @attackers-and-targets.sort({ -$_[0].initiative }) {
            debug "";
            debug $_[0];
            debug "attacks";
            debug $_[1];
            $_[1].take-damage-from($_[0]);
        }

        debug "?????\n";

        @immune-system = @immune-system.grep({ $_.units > 0 });
        @infection = @infection.grep({ $_.units > 0 });

        debug "AT THE END OF ROUND $round:\n";
        for @immune-system { debug($_) };
        for @infection { debug($_) };
    } while @immune-system.elems > 0 && @infection.elems > 0;

    if (@immune-system.elems == 0) {
        return "INFECTION WON", @infection.map({ $_.units }).sum
    } else {
        return "IMMUNE SYSTEM WON", @immune-system.map({ $_.units }).sum
    }
}

sub part_1(%armies) {
    say fight(%armies, 0)
}

sub part_2(%armies) {
    $debug = False;
    my $boost = 42;
    say fight(%armies, $boost)
}

my %armies = parse(@input);

part_2(%armies);
