#! /usr/bin/env rakudo

my ($a, $b, $c, $e, $f) = 0, 0, 0, 0, 0;

# part 1
$b = 943;

# part 2
# $b = 10551343;

$f = 1; # line 1
$e = 1; # line 2

loop {
    if ($b == $e * $f) { # line 4
        say $f;
        $a += $f; # line 7
    }

    $e += 1; # line 8

    if ($e > $b) { # line 9
        $f += 1; # line 12

        if ($f > $b) { # line 13
            last;
        } else {
            $e = 1; # line 2
        }
    }
}

say $a;
