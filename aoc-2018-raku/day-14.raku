#! /usr/bin/env rakudo

my $input = 290431;

sub create-new-recipe($r1, $r2) {
    "{ $r1 + $r2 }".comb.map({ +$_ })
}

sub part1($threshold) {
    my $elf1 = 0;
    my $elf2 = 1;
    my @recipes = (3, 7);

    while (@recipes.elems < $threshold + 10) {
        my @new-recipes = create-new-recipe(@recipes[$elf1], @recipes[$elf2]);
        @recipes.append(@new-recipes);
        $elf1 = ($elf1 + 1 + @recipes[$elf1]) % @recipes.elems;
        $elf2 = ($elf2 + 1 + @recipes[$elf2]) % @recipes.elems;
    }

    @recipes[*- 10 .. *- 1].join()
}

sub part2($input) {
    my $elf1 = 0;
    my $elf2 = 1;
    my @recipes = (3, 7);
    my $input-length = "$input".chars;

    loop {
        my @new-recipes = create-new-recipe(@recipes[$elf1], @recipes[$elf2]);
        @recipes.append(@new-recipes);
        if (@recipes.elems > $input-length) {
            my $blah1 = @recipes[*-($input-length + 1) .. *-2];
            my $blah2 = @recipes[*-$input-length .. *-1];
            if ($blah1.join() eq $input) {
                return @recipes.elems - $input-length - 1;
            }
            if ($blah2.join() eq $input) {
                return @recipes.elems - $input-length;
            }
        }
        $elf1 = ($elf1 + 1 + @recipes[$elf1]) % @recipes.elems;
        $elf2 = ($elf2 + 1 + @recipes[$elf2]) % @recipes.elems;
    }
}

#say part1($input)
say part2(51589);
say part2(515891);
say part2(92510);
say part2(91677);
say part2(916779);
say part2(59414);
say part2($input);
