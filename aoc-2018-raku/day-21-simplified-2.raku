my ($b, $c, $e, $f) = 0, 0, 0, 0;

my $a = 0;

# test stuff ?!

$b = 0;
$c = $b +| 65536;
$b = 10605201;
$f = $c +& 255;
$b += $f;
$b = $b +& 16777215;
$b *= 65899;
$b = $b +& 16777215;

if ($c < 256) {
    goto 27
}

$f = 0;

$e = $f + 1;
$e *= 256;
if ($e > $b) {
}