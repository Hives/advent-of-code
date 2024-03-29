#! /usr/bin/env rakudo

my $input-file = open "inputs/day_08.txt", :r;
my @input = $input-file.slurp.trim().split(" ").map(+*);
$input-file.close;

my @test-input = "2 3 0 3 10 11 12 1 1 0 1 99 2 1 1 2".split(" ").map(+*);

sub build-nodes(@ ($child-count, $metadata-count, *@rest)) {
    say "remaining: {@rest.elems}";

    my @children;
    while @children.elems < $child-count {
        my ($node, @new-rest) := build-nodes(@rest);
        @children.append($node);
        @rest = @new-rest;
    }

    return
            ${ children => [@children], metadata => @rest[^$metadata-count] },
            @rest[$metadata-count .. *- 1];
}

sub sum-metadata(% (:$children, :$metadata)) {
    $metadata.sum + $children.map({ sum-metadata($_) }).sum
}

sub sum-values(% (:$children, :$metadata)) {
    if $children.elems == 0 {
        return $metadata.sum
    }

    $metadata.map({ $children[$_ - 1] }).grep({ $_.defined }).map({ sum-values($_) }).sum
}

sub go(@numbers) {
    my (%node, $) := build-nodes(@numbers);

    say %node;

    say "part 1: { sum-metadata(%node) }";

    say "part 2: { sum-values(%node) }";
}

say go(@test-input);
# slow
say go(@input);
