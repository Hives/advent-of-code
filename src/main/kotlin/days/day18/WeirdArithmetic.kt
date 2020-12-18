package days.day18

fun splitString(input: String) =
    input
        .replace("(", "( ")
        .replace(")", " )")
        .split(" ")

fun main() {
//    println(parsy(splitString("1 + 2 * 3 + 4 * 5 + 6")))
//    println(parsy(splitString("1 + (2 * 3) + (4 * (5 + 6))")))
//    println(parsy(splitString("5 + (8 * 3 + 9 + 3 * 4 * 3)")))
}

fun parsy(input: List<String>) = evaluateSymbols(input.map { Symbol.from(it) })

fun evaluateSymbols(input: List<Symbol>): Value =
    if (input.size == 1) input.single() as Value
    else when {
        input.contains(LParen) -> {
            input.evaluateFirstParentheses()
        }
        input.contains(And) -> {
            input.evaluateFirstInstanceOfOperator(And)
        }
        input.contains(Times) -> {
            input.evaluateFirstInstanceOfOperator(Times)
        }
        else -> throw Exception("Something bad happened")
    }.let { evaluateSymbols(it) }

fun List<Symbol>.evaluateFirstParentheses(): List<Symbol> {
    val lParenIndex = this.indexOf(LParen)
    val rParenIndex = this.findMatchingRParen(lParenIndex)

    val beforeParens = this.subList(0, lParenIndex)
    val betweenParens = this.subList(lParenIndex + 1, rParenIndex)
    val afterParens = this.subList(rParenIndex + 1, this.size)

    return beforeParens + evaluateSymbols(betweenParens) + afterParens
}

fun List<Symbol>.evaluateFirstInstanceOfOperator(operator: Operator): List<Symbol> {
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

fun parseSymbols(input: List<String>): Expression =
    if (input.size == 1) Value(input.single().toLong())
    else {
        if (input[input.size - 1] == ")") {
            var index = input.size
            var parenCount = 0
            do {
                index--
                if (input[index] == ")") parenCount++
                if (input[index] == "(") parenCount--
            } while (parenCount > 0)
            val lastSymbols = input.subList(index + 1, input.size - 1)
            if (index == 0) {
                parseSymbols(lastSymbols)
            } else {
                val operator = input[index - 1]
                val rest = input.subList(0, index - 1)
                Combination(parseSymbols(rest), Operator.from(operator), parseSymbols(lastSymbols))
            }
        } else {
            val last = input[input.size - 1]
            val operator = input[input.size - 2]
            val rest = input.subList(0, input.size - 2)
            Combination(parseSymbols(rest), Operator.from(operator), Value(last.toLong()))
        }
    }

sealed class Symbol {
    companion object {
        fun from(input: String) =
            when (input) {
                "(" -> LParen
                ")" -> RParen
                "+" -> And
                "*" -> Times
                else -> Value(input.toLong())
            }
    }
}

interface Expression {
    fun evaluate(): Value
}

data class Value(val value: Long) : Symbol(), Expression {
    override fun evaluate(): Value = this
}

data class Combination(val n1: Expression, val operator: Operator, val n2: Expression) : Expression {
    override fun evaluate(): Value {
        return operator.evaluate(n1.evaluate(), n2.evaluate())
    }
}

sealed class Operator : Symbol() {
    abstract fun evaluate(n1: Value, n2: Value): Value

    companion object {
        fun from(input: String) =
            when (input) {
                "+" -> And
                "*" -> Times
                else -> throw Exception("Bad operator")
            }
    }
}

object And : Operator() {
    override fun evaluate(n1: Value, n2: Value): Value =
        Value(n1.value + n2.value)
}

object Times : Operator() {
    override fun evaluate(n1: Value, n2: Value): Value =
        Value(n1.value * n2.value)
}

object LParen : Symbol()
object RParen : Symbol()

