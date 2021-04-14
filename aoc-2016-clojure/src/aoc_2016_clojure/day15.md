# Day 15

https://adventofcode.com/2016/day/15

No code for this one. I worked out that given my puzzle input:

```
Disc #1 has 7 positions; at time=0, it is at position 0.
Disc #2 has 13 positions; at time=0, it is at position 0.
Disc #3 has 3 positions; at time=0, it is at position 2.
Disc #4 has 5 positions; at time=0, it is at position 2.
Disc #5 has 17 positions; at time=0, it is at position 0.
Disc #6 has 19 positions; at time=0, it is at position 7.
```

the problem was equivalent to solving this system of equations:

```
t ≡ -1 mod 7
t ≡ -2 mod 13
t ≡ -2 mod 3
t ≡ -1 mod 5
t ≡ -5 mod 17
t ≡ -13 mod 19
```

and the extra disc for part 2 was equivalent to adding this equation to the
system:

```
t ≡ -7 mod 11
```

I solved these equations by plugging them into an online Chinese Remainder
Theorem calculator:

https://www.dcode.fr/chinese-remainder

🤷
