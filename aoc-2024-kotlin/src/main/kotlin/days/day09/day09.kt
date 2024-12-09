package days.day09

import days.day09.Block.File
import days.day09.Block.Gap
import lib.Reader
import lib.checkAnswer
import lib.time

fun main() {
    val input = Reader("/day09/input.txt").digits()
    val exampleInput = Reader("/day09/example-1.txt").digits()

    time(message = "Part 1") {
        part1(input)
    }.checkAnswer(6323641412437)

    time(message = "Part 2", warmUp = 5, iterations = 5) {
        part2(input)
    }.checkAnswer(6351801932670)
}

fun part1(input: List<Int>): Long {
    var fileSystem = input.flatMapIndexed { index, n ->
        List(n) { if (index.isEven()) index / 2 else null }
    }.toMutableList()

    var pointer = 0

    while (pointer < fileSystem.size) {
        if (fileSystem[pointer] == null) {
            var last: Int? = null
            while (last == null) last = fileSystem.removeLast()
            if (pointer < fileSystem.size) {
                fileSystem[pointer] = last
            } else {
                fileSystem.add(last)
            }
        }
        pointer++
    }

    return fileSystem.foldIndexed(0L) { index, acc, n -> acc + (index * n!!) }
}

fun part2(input: List<Int>): Long {
    var fileSystem = input.mapIndexed { index, n ->
        if (index.isEven()) File(n, index / 2) else Gap(n)
    }.toMutableList()

    var pointer = fileSystem.size - 1

    while (pointer >= 0) {
//        printy(fileSystem)

        if (fileSystem[pointer] is Gap) {
            pointer--
        }
        var file = fileSystem[pointer] as File
//        file.also(::println)
        val target = (0..pointer - 1).find {
            val block = fileSystem[it]
            block is Gap && block.length >= file.length
        }
//        target.also(::println)
        if (target != null) {
            val gap = fileSystem[target]
//            println(gap)
            fileSystem[target] = file
            fileSystem[pointer] = Gap(file.length)
            val spare = gap.length - file.length
            if (spare > 0) {
                fileSystem.add(target + 1, Gap(spare))
            }
        }
        pointer--
    }

    val expanded = expand(fileSystem)
    val total = expanded.foldIndexed(0L) { index, acc, n ->
        if (n == null) {
            acc
        } else {
            acc + (index * n)
        }
    }

    return total
}

fun expand(fs: List<Block>): List<Int?> =
    fs.fold(emptyList<Int?>()) { acc, block ->
        when (block) {
            is File -> acc + List(block.length) { block.id }
            is Gap -> acc + List(block.length) { null }
        }
    }


fun printy(fs: List<Block>) {
    expand(fs).map { if (it == null) "." else it.toString() }.joinToString("")
}

fun Int.isEven() = this % 2 == 0

sealed class Block(open val length: Int) {
    data class Gap(override val length: Int) : Block(length)
    data class File(override val length: Int, val id: Int) : Block(length)
}
