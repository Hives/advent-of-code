#! /usr/bin/env rakudo

my ($b, $c, $e, $f) = 0, 0, 0, 0;

my $a = 0;

$b = 123;

my $seen = SetHash.new;

BITWISE-AND-TEST:
loop {
    $b = $b +& 456;
    if ($b == 72) {
        last BITWISE-AND-TEST;
    }
}

$b = 0;

# line 5 (+1)
$c = $b +| 65536;
$b = 10605201;

loop {
    # line 7 (+1)
    $f = $c +& 255;
    $b += $f;
    $b = $b +& 16777215;
    $b *= 65899;
    $b = $b +& 16777215;

    # 13
    if ($c < 256) {
#        say $b;
#        if $b ∈ $seen {
#            exit();
#        }
#        $seen.set($b);
        if ($b == $a) {
            exit()
        } else {
            $c = $b +| 65536;
            $b = 10605201;
        }
    } else {
        # $f = 0;

        # # 17 (+1)
        # LINE_17_LOOP:
        # loop {
        #     $e = 256 * ($f + 1);
        #     last LINE_17_LOOP if $e > $c;
        #     $f += 1;
        # }

        # $c = $f;

        my $t = $c / 256;
        $c = $t == floor($t) ?? ceiling($t) !! ceiling($t) - 1;
    }
}

#`[
solution:

from the translated version of the program in this file, we can see that the
program generates a sequence of numbers in register 1 ($b), and if it is equal
to the value in register 0 ($a) it ends. (line 40).

so letting the program run and printing out all the values of $b which are used
for the check on line 40 gives all the possible values of $a which will cause the
program to terminate.

the value for part 1 is the first value of $b at this point, the value for part 2
is the last value of $b before it starts to loop.
]