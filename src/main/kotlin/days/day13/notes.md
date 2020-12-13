# Day 13 solution notes

I used a standard algorithm that applies the [Chinese Remainder Theorem][chinese-remainder-wiki] for this one. Here's how that works.

## The puzzle

[Full instructions are here](Instructions.txt).

The input is a list of bus id numbers. Each bus departs at time t equal to integer multiples of its id number. So the bus with id 7 departs at t equals 7, 14, 21 etc.

The puzzle is to find the lowest value of time t so that each bus in the input departs at t plus the bus's position in the input list. Also, some positions in the list are blank, and we don't care about those.

So if the input list is [7, 5, 8, blank], we need to find the smallest time t such that:
- a number 7 bus leaves at time t + 0
- a number 5 bus leaves at time t + 1
- a number 8 bus leaves at time t + 2
- we don't care what happens at time t + 3

The answer in this case is 14 (I made the example up to be simple). Here's a picture showing when buses depart in this example:
```
    |   buses
  t |  7  5  8
  -------------
  0 |  x  x  x
  1 |
  2 |
  3 |
  4 |
  5 |     x
  6 |
  7 |  x
  8 |        x
  9 |
 10 |     x
 11 |
 12 |
 13 |
 14 |  x        <- our answer
 15 |     x
 16 |        x
```

So t = 14 satisfies a number 7 bus leaving at t + 0, a 5 at t + 1, and an 8 at t + 2.

We can see that the puzzle is equivalent to saying:
- The remainder when dividing t by 7 should be 0
- The remainder when dividing t by 5 should be 5 - 1 = 4
- The remainder when dividing t by 8 should be 8 - 2 = 6

This is equivalent to solving the following equations for t:

t = 0 mod 7  
t = 4 mod 5  
t = 6 mod 8

Or in general:

t = (busNumber - busIndex) mod busNumber

At this point a dusty synapse fires in my brain for the first time in 20 years, and I google the Chinese Remainder Theorem...

## Chinese Remainder Theorem

The Chinese Remainder Theorem says:

> Let n<sub>1</sub>, ..., n<sub>k</sub> be positive integers, which are pairwise coprime (i.e. any two  of them have no common factors).
> 
> Then for any given integers a<sub>1</sub>, ..., a<sub>k</sub> there exists an integer x solving the following system of equations:
> 
> x = a<sub>1</sub> mod n<sub>1</sub>  
> x = a<sub>2</sub> mod n<sub>2</sub>  
> ...  
> x = a<sub>k</sub> mod n<sub>k</sub>
> 
> And furthermore, the solution is unique in the range 1...N where N is the product of all the n<sub>i</sub>.

Well that looks like our system of equations for t above, with the n<sub>i</sub> corresponding to the bus numbers, and the a<sub>i</sub> corresponding to the bus number, minus the bus's index in the input.

One other thing to note - Chinese Remainder Theorem requires the n<sub>i</sub> to be coprime. Checking the puzzle input reveals all the bus numbers are prime, so this condition is satisfied.

Ok, so now I google "chinese remainder theorem solution algorithm" or similar...

## Chinese Remainder Theorem algorithm

There are a number of algorithms to find the solution to a set of equations which satisfy the conditions of the CRT. [This was the simplest explanation I found][crt-algo].

*Disclaimer: I don't understand why this works, but it does...*

> Define N to be the product of your n<sub>i</sub>. Since we know the n<sub>i</sub> are coprime, it follows that any n<sub>i</sub> is coprime with N/n<sub>i</sub>. So the greatest common divisor of n<sub>i</sub> and N/n<sub>i</sub> is 1.
> 
> This means we can use the Extended Euclidean Algorithm to find integers r<sub>i</sub> and s<sub>i</sub> such that:
> 
> - r<sub>i</sub> n<sub>i</sub> + s<sub>i</sub> N n<sub>i</sub> = 1
> 
> Then a solution x to the set of equations is equal to the sum of this expression for all a<sub>i</sub> and n<sub>i</sub>:
> - a<sub>i</sub> s<sub>i</sub> N / n<sub>i</sub>
> 
> And the minimal solution is:
> 
> - x mod N

## Extended Euclidean Algorithm

> Wait, you mentioned an Extended Euclidean Algorithm as well, so we have to use that too?

Yes, I'm afraid so. The (non-extended) Euclidean Algorithm is a way to construct the greatest common divisor (GCD) for two numbers. The Extended Euclidean Algorithm uses the results of the Euclidean Algorithm to express the GCD as a linear combination of the original two numbers.

So if your two numbers are a and b, the Euclidean Algorithm with give you the greatest common divisor, G, and the Extended Euclidean Algorithm will give you two numbers r and s such that you can write:

- G = (r * a) + (s * b)

I watched [this excellent video][extended-euclid-vid] explaining how the Extended Euclidean Algorithm works, and unlike the Chinese Remainder Theorem algorithm, I felt like I understood this part.

## The solution

So to solve the puzzle, just implement these two algorithms, and plug in your input. Simple!

[chinese-remainder-wiki]: https://en.wikipedia.org/wiki/Chinese_Remainder_Theorem
[crt-algo]: https://rosettacode.org/wiki/Chinese_remainder_theorem
[extended-euclid-vid]: https://www.youtube.com/watch?v=hB34-GSDT3k