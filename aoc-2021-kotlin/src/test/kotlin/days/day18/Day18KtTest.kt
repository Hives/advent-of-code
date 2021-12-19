package days.day18

import days.day18.Token.Bracket.Close
import days.day18.Token.Bracket.Open
import days.day18.Token.Value
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class Day18KtTest : StringSpec({

    "parsing and unparsing" {
        val input = "[[[5,[4,2]],[[9,2],3]],[[[6,2],[6,8]],[[2,4],[9,4]]]]"
        parse(input).unparse() shouldBe input
    }

    "values" {
        Value(2) + Value(3) shouldBe Value(5)
    }

    "exploding example 1 (no regular number to left)" {
        val input = "[[[[[9,8],1],2],3],4]"
        val expected = "[[[[0,9],2],3],4]"

        explode(parse(input)).unparse() shouldBe expected
    }

    "exploding example 2 (no regular number to right)" {
        val input = "[7,[6,[5,[4,[3,2]]]]]"
        val expected = "[7,[6,[5,[7,0]]]]"

        explode(parse(input)).unparse() shouldBe expected
    }

    "exploding example 3 (only leftmost pair explodes)" {
        val input = "[[3,[2,[1,[7,3]]]],[6,[5,[4,[3,2]]]]]"
        val expected = "[[3,[2,[8,0]]],[9,[5,[4,[3,2]]]]]"

        explode(parse(input)).unparse() shouldBe expected
    }

    "split example" {
        val input = listOf(Open, Open, Value(15), Value(2), Close, Value(3), Close)
        val expected = "[[[7,8],2],3]"

        split(input).unparse() shouldBe expected
    }

    "reducing an expression" {
        val input = "[[[[[4,3],4],4],[7,[[8,4],9]]],[1,1]]"
        val expected = "[[[[0,7],4],[[7,8],[6,0]]],[8,1]]"

        reduceExpression(parse(input)).unparse() shouldBe expected
    }

    "adding and reducing" {
        val left = "[[[[4,3],4],4],[7,[[8,4],9]]]"
        val right = "[1,1]"
        val expected = "[[[[0,7],4],[[7,8],[6,0]]],[8,1]]"

        add(parse(left), parse(right)).unparse() shouldBe expected
    }

    "reducing a list" {
        val list = listOf(
            "[[[0,[4,5]],[0,0]],[[[4,5],[2,6]],[9,5]]]",
            "[7,[[[3,7],[4,3]],[[6,3],[8,8]]]]",
            "[[2,[[0,8],[3,4]]],[[[6,7],1],[7,[1,6]]]]",
            "[[[[2,4],7],[6,[0,5]]],[[[6,8],[2,8]],[[2,1],[4,5]]]]",
            "[7,[5,[[3,8],[1,4]]]]",
            "[[2,[2,2]],[8,[8,1]]]",
            "[2,9]",
            "[1,[[[9,3],9],[[9,0],[0,7]]]]",
            "[[[5,[7,4]],7],1]",
            "[[[[4,2],2],6],[8,7]]"
        ).map(::parse)

        list.reduce(::add).unparse() shouldBe "[[[[8,7],[7,7]],[[8,6],[7,7]]],[[[0,7],[6,6]],[8,7]]]"
    }

    "splitting an expression into its constituents" {
        val (left, right) = constituents(parse("[[9,1],[1,9]]"))
        left.unparse() shouldBe "[9,1]"
        right.unparse() shouldBe "[1,9]"
    }

    "splitting an expression into its constituents 2" {
        val (left, right) = constituents(parse("[[[[8,7],[7,7]],[[8,6],[7,7]]],[[[0,7],[6,6]],[8,7]]]"))
        left.unparse() shouldBe "[[[8,7],[7,7]],[[8,6],[7,7]]]"
        right.unparse() shouldBe "[[[0,7],[6,6]],[8,7]]"
    }

    "magnitude 1" {
        magnitude(listOf(Value(9))) shouldBe 9
    }

    "magnitude 2" {
        magnitude(parse("[9,1]")) shouldBe 29
    }

    "magnitude 3" {
        magnitude(parse("[[9,1],[1,9]]")) shouldBe 129
    }

    "magnitude 4" {
        magnitude(parse("[[[[8,7],[7,7]],[[8,6],[7,7]]],[[[0,7],[6,6]],[8,7]]]")) shouldBe 3488
    }

    val exampleInput = listOf("[[[0,[5,8]],[[1,7],[9,6]]],[[4,[1,2]],[[1,4],2]]]",
        "[[[5,[2,8]],4],[5,[[9,9],0]]]",
        "[6,[[[6,2],[5,6]],[[7,6],[4,7]]]]",
        "[[[6,[0,7]],[0,9]],[4,[9,[9,0]]]]",
        "[[[7,[6,4]],[3,[1,3]]],[[[5,5],1],9]]",
        "[[6,[[7,3],[3,2]]],[[[3,8],[5,7]],4]]",
        "[[[[5,4],[7,7]],8],[[8,3],8]]",
        "[[9,3],[[9,9],[6,[4,9]]]]",
        "[[2,[[7,7],7]],[[5,8],[[9,3],[0,2]]]]",
        "[[[[5,2],5],[8,[3,7]]],[[5,[7,5]],[4,4]]]")

    "part 1 example" {
        part1(exampleInput) shouldBe 4140
    }

    "part 2 example" {
        part2(exampleInput) shouldBe 3993
    }
})
