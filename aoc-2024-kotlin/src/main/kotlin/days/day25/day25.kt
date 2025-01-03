package days.day25

import lib.Grid
import lib.Reader
import lib.checkAnswer
import lib.flip
import lib.time

fun main() {
    val input = Reader("/day25/input.txt").string()
    val exampleInput = Reader("/day25/example-1.txt").string()

    time(message = "Part 1") {
        part1(input)
    }.checkAnswer(3508)
}

fun part1(input: String) =
    input.split("\n\n")
        .map { block -> block.split("\n").map(String::toList) }
        .partition { it[0].all { c -> c == '#' } }
        .let { (allLocks, allKeys) ->
            val allLockHeights = allLocks.map(::getHeights)
            val allKeyHeights = allKeys.map(::getHeights)
            allLockHeights.sumOf { lockHeights ->
                allKeyHeights.count { keyHeights ->
                    keyHeights.zip(lockHeights).all { (keyHeight, lockHeight) ->
                        keyHeight + lockHeight < 6
                    }
                }
            }
        }

fun getHeights(lists: Grid<Char>) =
    lists.flip().map { it.count { c -> c == '#' } - 1 }

