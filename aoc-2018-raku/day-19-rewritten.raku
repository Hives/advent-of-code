#! /usr/bin/env rakudo

# my $b = 943;
my $b = 10551343;

my $c = 1;

my $t = 0;

while ($c <= $b) {
    if ($b %% $c) {
        say $c;
        $t += $c;
    }
    $c += 1;
}

say ": $t";
