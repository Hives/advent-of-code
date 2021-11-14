#! /usr/bin/env rakudo

my $input-file1 = open "inputs/day_16_1.txt", :r;
my $puzzle-input = $input-file1.slurp.trim();
$input-file1.close;

my $input-file2 = open "inputs/day_16_2.txt", :r;
my @program = $input-file2.slurp.trim().split("\n").map({ $_.split(" ").map({ +$_ }) });
$input-file2.close;

my $test-input = "Before: [3, 2, 1, 1]
9 2 1 2
After:  [3, 2, 2, 1]";

sub parse($input) {
    sub extract-values($string) {
        ($string ~~ rx{ "[" (.*) "]" })[0].split(", ").map({ +$_ });
    }

    $input.split("\n\n").map(-> $block {
        my $lines = $block.split("\n");
        my $instruction = $lines[1].split(" ").map({ +$_ });
        %(
            :before(extract-values($lines[0])),
            :opcode-number($instruction[0]),
            :opcode-params($instruction[1 .. 3]),
            :after(extract-values($lines[2]))
        )
    })
}

sub addr(@inputs, @state is copy) {
    @state[@inputs[2]] = @state[@inputs[0]] + @state[@inputs[1]];
    @state
}
sub addi(@inputs, @state is copy) {
    @state[@inputs[2]] = @state[@inputs[0]] + @inputs[1];
    @state
}
sub mulr(@inputs, @state is copy) {
    @state[@inputs[2]] = @state[@inputs[0]] * @state[@inputs[1]];
    @state
}
sub muli(@inputs, @state is copy) {
    @state[@inputs[2]] = @state[@inputs[0]] * @inputs[1];
    @state
}
sub banr(@inputs, @state is copy) {
    @state[@inputs[2]] = @state[@inputs[0]] +& @state[@inputs[1]];
    @state
}
sub bani(@inputs, @state is copy) {
    @state[@inputs[2]] = @state[@inputs[0]] +& @inputs[1];
    @state
}
sub borr(@inputs, @state is copy) {
    @state[@inputs[2]] = @state[@inputs[0]] +| @state[@inputs[1]];
    @state
}
sub bori(@inputs, @state is copy) {
    @state[@inputs[2]] = @state[@inputs[0]] +| @inputs[1];
    @state
}
sub setr(@inputs, @state is copy) {
    @state[@inputs[2]] = @state[@inputs[0]];
    @state
}
sub seti(@inputs, @state is copy) {
    @state[@inputs[2]] = @inputs[0];
    @state
}
sub gtir(@inputs, @state is copy) {
    @state[@inputs[2]] = @inputs[0] > @state[@inputs[1]] ?? 1 !! 0;
    @state
}
sub gtri(@inputs, @state is copy) {
    @state[@inputs[2]] = @state[@inputs[0]] > @inputs[1] ?? 1 !! 0;
    @state
}
sub gtrr(@inputs, @state is copy) {
    @state[@inputs[2]] = @state[@inputs[0]] > @state[@inputs[1]] ?? 1 !! 0;
    @state
}
sub eqir(@inputs, @state is copy) {
    @state[@inputs[2]] = @inputs[0] == @state[@inputs[1]] ?? 1 !! 0;
    @state
}
sub eqri(@inputs, @state is copy) {
    @state[@inputs[2]] = @state[@inputs[0]] == @inputs[1] ?? 1 !! 0;
    @state
}
sub eqrr(@inputs, @state is copy) {
    @state[@inputs[2]] = @state[@inputs[0]] == @state[@inputs[1]] ?? 1 !! 0;
    @state
}

my @opcodes = &addr, &addi, &mulr, &muli, &banr, &bani, &borr, &bori, &setr, &seti, &gtir, &gtri, &gtrr, &eqir, &eqri,
              &eqrr;

sub part1($input) {
    my @samples = parse($input);

    @samples.map(-> %sample {
        @opcodes.grep(-> &opcode {
            &opcode(%sample<opcode-params>, %sample<before>) eq %sample<after>
        }).elems
    }).grep({ $_ >= 3 }).elems
}

sub part2($input) {
    my @samples = parse($input);
    my %possibilities;

    for @samples -> %sample {
        my $opcode-number = %sample<opcode-number>;
        my $possible-opcodes = @opcodes.grep(-> &opcode {
            &opcode(%sample<opcode-params>, %sample<before>) eq %sample<after>
        }).Set;
        %possibilities{$opcode-number} =
                !%possibilities{$opcode-number} ??
                $possible-opcodes !!
                %possibilities{$opcode-number} ∩ $possible-opcodes;
    };

    my %opcode-map;

    while %possibilities.keys.elems > 0 {
        my @solved = %possibilities.grep({ $_.value.elems == 1 });

        for @solved -> %s {
            my $opcode-number = %s.key;
            my &opcode = %s.value.keys[0];
            %opcode-map{$opcode-number} = &opcode;

            %possibilities{$opcode-number}:delete;

            for %possibilities.keys -> $key {
                %possibilities{$key} = %possibilities{$key} (-) Set.new(&opcode);
            }
        }
    }

    my $state = (0, 0, 0, 0);

    for @program -> $instruction {
        my &opcode = %opcode-map{$instruction[0]};
        my $params = $instruction[1 .. 3];
        $state = &opcode($params, $state);
    }

    $state[0];
}

say part1($puzzle-input);
say part2($puzzle-input);