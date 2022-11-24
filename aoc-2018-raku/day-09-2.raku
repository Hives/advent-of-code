#! /usr/bin/env rakudo

# inputs;
my $players = 413;
#my $last-marble = 71082;
my $last-marble = 22;

class Node {
    has $.value is required;
    has $.prev is rw;
    has $.next is rw;
}

class Circle {
    has Node $.head is rw;
    has Node $.tail is rw;
    has Node $.current is rw;

    method push($value) {
        my $new-node = Node.new(:$value);
        $new-node.next = self.head;
        if (!$new-node.next) {
            self.tail = $new-node;
        }

        if (self.head) {
            self.head.prev = $new-node
        }
        self.head = $new-node;

        self.current = $new-node;
    }

    method rotate($n) {
        if ($n > 0) {
            if (self.current.next) {
                self.current = self.current.next
            } else {
                self.current = self.head;
            }
            self.rotate($n - 1);
        }
        if ($n < 0) {
            if (self.current.prev) {
                self.current = self.current.prev
            } else {
                self.current = self.tail;
            }
            self.rotate($n + 1);
        }
    }

    method insert-after-current(Int $value) {
        my $prev = self.current;
        my $next = self.current.next;
        my $new = Node.new(:$value);

        $prev.next = $new;
        $new.prev = $prev;

        $new.next = $next;
        if ($next) {
            $next.prev = $new
        } else {
            self.tail = $new
        }
    }

    method print() {
        if (!self.head) {
            say "empty";
        } else {
            my $start = self.current;
            repeat {
                print "{ self.current.value } ";
                self.rotate(1)
            } while !(self.current === $start);
            print "\n";
        }
    }
}

my $circle = Circle.new();
$circle.push(0);

for (1 .. $last-marble) -> $marble {
    $circle.rotate(2);
    $circle.insert-after-current($marble);
}

$circle.current.print()
