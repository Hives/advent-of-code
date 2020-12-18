package days.day18

import days.day18.Symbol.LParen
import days.day18.Symbol.Operator
import days.day18.Symbol.Operator.Plus
import days.day18.Symbol.Operator.Times
import days.day18.Symbol.RParen
import days.day18.Symbol.Value

fun stringToSymbols(input: String) =
    input
        .replace("(", "( ")
        .replace(")", " )")
        .split(" ")
        .map { Symbol.from(it) }

fun evaluateSymbols1(input: List<Symbol>): Value =
    if (input.size == 1) input.single() as Value
    else when {
        input.contains(LParen) -> {
            val (before, between, after) = input.splitOnParentheses()
            before + evaluateSymbols1(between) + after
        }
        input.contains(Plus) || input.contains(Times) -> {
            input.evaluateFirstOperator()
        }
        else -> throw Exception("Something bad happened")
    }.let { evaluateSymbols1(it) }

fun evaluateSymbols2(input: List<Symbol>): Value =
    if (input.size == 1) input.single() as Value
    else when {
        input.contains(LParen) -> {
            val (before, between, after) = input.splitOnParentheses()
            before + evaluateSymbols2(between) + after
        }
        input.contains(Plus) -> {
            input.evaluateFirstInstanceOf(Plus)
        }
        input.contains(Times) -> {
            input.evaluateFirstInstanceOf(Times)
        }
        else -> throw Exception("Something bad happened")
    }.let { evaluateSymbols2(it) }

fun List<Symbol>.splitOnParentheses(): Triple<List<Symbol>, List<Symbol>, List<Symbol>> {
    val lParenIndex = this.indexOf(LParen)
    val rParenIndex = this.findMatchingRParen(lParenIndex)

    val beforeParens = this.subList(0, lParenIndex)
    val betweenParens = this.subList(lParenIndex + 1, rParenIndex)
    val afterParens = this.subList(rParenIndex + 1, this.size)

    return Triple(beforeParens, betweenParens, afterParens)
}

fun List<Symbol>.evaluateFirstOperator(): List<Symbol> {
    val i = this.indexOfFirst { it is Operator }

    val start = this.subList(0, i - 1)
    val operatorAndNeighbours = this.subList(i - 1, i + 2)
    val end = this.subList(i + 2, this.size)

    return start + evaluateSimpleExpression(operatorAndNeighbours) + end
}

fun List<Symbol>.evaluateFirstInstanceOf(operator: Operator): List<Symbol> {
    val i = this.indexOf(operator)

    val start = this.subList(0, i - 1)
    val operatorAndNeighbours = this.subList(i - 1, i + 2)
    val end = this.subList(i + 2, this.size)

    return start + evaluateSimpleExpression(operatorAndNeighbours) + end
}

fun evaluateSimpleExpression(expression: List<Symbol>): Value {
    val (n1, operator, n2) = expression
    return (operator as Operator).evaluate(n1 as Value, n2 as Value)
}

fun List<Symbol>.findMatchingRParen(lParenIndex: Int): Int {
    var parenCount = 1
    var rParenIndex = lParenIndex
    do {
        rParenIndex++
        if (this[rParenIndex] == LParen) parenCount++
        if (this[rParenIndex] == RParen) parenCount--
    } while (parenCount > 0)
    return rParenIndex
}

sealed class Symbol {
    data class Value(val value: Long) : Symbol()

    object LParen : Symbol()
    object RParen : Symbol()

    sealed class Operator : Symbol() {
        abstract fun evaluate(n1: Value, n2: Value): Value

        object Plus : Operator() {
            override fun evaluate(n1: Value, n2: Value): Value =
                Value(n1.value + n2.value)
        }

        object Times : Operator() {
            override fun evaluate(n1: Value, n2: Value): Value =
                Value(n1.value * n2.value)
        }
    }

    companion object {
        fun from(input: String) =
            when (input) {
                "(" -> LParen
                ")" -> RParen
                "+" -> Plus
                "*" -> Times
                else -> Value(input.toLong())
            }
    }
}