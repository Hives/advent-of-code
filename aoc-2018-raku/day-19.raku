#! /usr/bin/env rakudo

my $input-file = open "inputs/day_19.txt", :r;
my $puzzle-input = $input-file.slurp.trim().split("\n");
$input-file.close;

my $test-input-file = open "inputs/day_19_test.txt", :r;
my $test-puzzle-input = $test-input-file.slurp.trim().split("\n");
$test-input-file.close;

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

sub parse($input) {
    sub parse-opcode($string) {
        return $string if $string.comb[0] eq "#";

        my $r = ($string ~~ /(.+) ' ' (\d+) ' ' (\d+) ' ' (\d+)/);

        my $function = @opcodes.first({ "{ $_.name }" eq "$r[0]" });
        my @inputs = $r[1 .. 3].map({ +$_ });

        return ($function, @inputs.List),;
    }

    my $pointer-register = +($input[0].split(" ")[1]);

    my $program = $input[1 ..*]».&(-> $line {
        parse-opcode($line);
    });

    return $pointer-register, $program
}

sub increment-pointer($state, $pointer-register) {
    my $new-state = $state;
    $new-state[$pointer-register] += 1;
    return $new-state;
}

sub tick($program, $state, $pointer-register) {
    my $pointer = $state[$pointer-register];
    my ($function, $inputs) = $program[$pointer];
    my $output = $function($inputs, $state);

    return increment-pointer($output, $pointer-register);
}

sub part1(@input) {
    my ($pointer-register, $program) = parse(@input);

    my $state = (0, 0, 0, 0, 0, 0);

    my $count = 0;

    while $state[$pointer-register] < $program.elems {
        $count++;
        if $count %% 10_000 {
            say $count;
            say $state;
        }
        $state = tick($program, $state, $pointer-register);
    }

    say $state;
}

part1($puzzle-input);