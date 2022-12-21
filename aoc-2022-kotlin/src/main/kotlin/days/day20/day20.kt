package days.day20

import lib.Reader
import lib.checkAnswer
import lib.time
import kotlin.math.sign

fun main() {
    val input = Reader("day20.txt").strings()
    val exampleInput = Reader("day20-example.txt").strings()

    time(message = "Part 1", warmUpIterations = 2, iterations = 5) {
        part1(input)
    }.checkAnswer(9866)

    time(message = "Part 2", warmUpIterations = 2, iterations = 5) {
        part2(input)
    }.checkAnswer(12374299815791)
}

fun part1(input: List<String>): Long {
    val numbers = parse(input)
    val circle = Circle()
    numbers.forEach { circle.add(it) }
    numbers.forEach {
        if (it.value != 0L) {
            circle.find(it)
            circle.delete()
            circle.rotate(it.value - 1)
            circle.add(it)
        }
    }
    circle.findZero()
    var g = 0L
    repeat(3) {
        circle.rotate(1000)
        g += circle.current!!.value.value
    }
    return g
}

fun part2(input: List<String>): Long {
    val numbers = parse(input).map { it.copy(value = it.value * 811589153) }
    val circle = Circle()
    numbers.forEach { circle.add(it) }
    repeat(10) {
        numbers.forEach {
            if (it.value != 0L) {
                circle.find(it)
                circle.delete()
                circle.rotate(it.value - 1)
                circle.add(it)
            }
        }
    }
    circle.findZero()
    var g = 0L
    repeat(3) {
        circle.rotate(1000)
        g += circle.current!!.value.value
    }
    return g
}

data class Number(val index: Int, val value: Long)

fun parse(input: List<String>): List<Number> =
    input.mapIndexed { index, s -> Number(index = index, value = s.toLong()) }

data class Node(val value: Number, var next: Node? = null, var prev: Node? = null) {
    override fun toString(): String {
        return value.toString()
    }
}

class Circle {
    private var head: Node? = null
    private var tail: Node? = null
    var current: Node? = null
    var size: Int = 0

    fun print() {
        val storedCurrent = current
        current = head
        do {
            print("$current, ")
            forward()
        } while (current != head)
        println()
        current = storedCurrent
    }

    fun find(n: Number) {
        require(current != null)
        do {
            forward()
        } while (current?.value != n)
    }

    fun findZero() {
        do {
            forward()
        } while (current?.value?.value != 0L)
    }

    fun delete() {
        val prev = current?.prev
        val next = current?.next

        if (prev != null) {
            prev.next = next
        } else {
            head = next
        }

        if (next != null) {
            next.prev = prev
        } else {
            tail = prev
        }

        current = next ?: head
        size -= 1
    }

    fun add(n: Number) {
        val newNode = Node(n)

        if (current == null) {
            current = newNode
            head = newNode
            tail = newNode
        } else {
            val prev = current!!
            val next = current!!.next

            prev.next = newNode
            newNode.prev = prev

            newNode.next = next
            if (next == null) {
                tail = newNode
            } else {
                next.prev = newNode
            }

            current = newNode
        }

        size += 1
    }

    tailrec fun rotate(n: Long) {
        val foo = n % size
        when {
            foo.sign > 0 -> {
                forward()
                rotate(foo - 1)
            }

            foo.sign < 0 -> {
                backward()
                rotate(foo + 1)
            }
        }
    }

    fun forward(): Node? {
        current = current?.next ?: head
        return current
    }

    fun backward(): Node? {
        current = current?.prev ?: tail
        return current
    }
}
