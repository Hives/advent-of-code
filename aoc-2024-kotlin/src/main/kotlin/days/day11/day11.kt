package days.day11

import days.day09.printy
import kotlin.system.exitProcess
import lib.Reader
import lib.checkAnswer
import lib.time

fun main() {
    val input = Reader("/day11/input.txt").listOfLongs()
    val exampleInput = Reader("/day11/example-1.txt").listOfLongs()

    solve(exampleInput, 6).checkAnswer(22)
    solve(exampleInput, 25).checkAnswer(55312)

    exitProcess(0)

    time(message = "Part 1") {
        part1(input)
    }.checkAnswer(185205)

    time(message = "Part 2") {
        part2(input)
    }.checkAnswer(0)
}

fun part1(input: List<Long>) =
    solve(input, 25)

fun part2(input: List<Long>) =
    solve(input, 75)

fun solve(input: List<Long>, blinks: Int): Long {
    val zeroCounts = calcZeros(blinks)

    println(zeroCounts)

    val numbers = MyLinkedList<Long>()
    input.reversed().forEach { numbers.pushStart(it) }

    var total = 0L

    var count = 0;
    repeat(blinks) {
//        println("---")
        count += 1
//        numbers.printy()

        var node = numbers.head
        while (node != null) {
            val n = node.value
            when {
                n == 0L -> {
                    node.value = 1L
                    node = node.next
//                    node = numbers.delete(node)
//                    println("blinks left: ${blinks - count}")
//                    total += zeroCounts[blinks - count]!!
                }

                n.countDigits().isEven() -> {
                    val (n1, n2) = n.split()
                    node.value = n1
                    numbers.insertAfter(node, n2)
                    node = node.next?.next
                }

                else -> {
                    node.value = n * 2024
                    node = node.next
                }
            }
        }
    }

    return total + numbers.size
}

fun calcZeros(blinks: Int): MutableMap<Int, Long> {
    val numbers = MyLinkedList<Long>()
    numbers.pushStart(0)
    val counts = mutableMapOf(0 to 1L)
    var n = 0
    while (n <= blinks) {
        n += 1
        var node = numbers.head
        while (node != null) {
            val n = node.value
            when {
                n == 0L -> {
                    node.value = 1L
                    node = node.next
                }

                n.countDigits().isEven() -> {
                    val (n1, n2) = n.split()
                    node.value = n1
                    numbers.insertAfter(node, n2)
                    node = node.next?.next
                }

                else -> {
                    node.value = n * 2024
                    node = node.next
                }
            }
        }
        counts[n] = numbers.size.toLong()
    }

    return counts
}

fun Long.split(): Pair<Long, Long> {
    val s = this.toString()
    val half = s.length / 2
    return Pair(s.substring(0, half).toLong(), s.substring(half, s.length).toLong())
}

fun Long.countDigits() =
    this.toString().length

fun Int.isEven() =
    this % 2 == 0

data class MyLinkedListNode<T : Any>(
    var value: T,
    var next: MyLinkedListNode<T>? = null,
    var prev: MyLinkedListNode<T>? = null
)

class MyLinkedList<T : Any> {
    private var _head: MyLinkedListNode<T>? = null
    val head
        get() = _head

    private var _size = 0L
    val size
        get() = _size

    fun pushStart(value: T): MyLinkedList<T> {
        _head = MyLinkedListNode(value, _head, null)
        _size += 1
        return this
    }

    fun insertAfter(node: MyLinkedListNode<T>, value: T): MyLinkedListNode<T> {
        val newNode = MyLinkedListNode(value, node.next, node)
        node.next = newNode
        newNode.next?.prev = newNode
        _size += 1
        return newNode
    }

    fun delete(node: MyLinkedListNode<T>): MyLinkedListNode<T>? {
        node.prev?.next = node.next
        node.next?.prev = node.prev
        return node.next
    }

    fun printy() {
        var node = _head
        var output = ""
        while (node != null) {
            output += "${node.value} "
            node = node.next
        }
        println(output)
    }
}
