package days.day22

import lib.Reader
import lib.Vector3
import lib.checkAnswer
import lib.time
import kotlin.system.exitProcess

fun main() {
    val input = Reader("/day22/input.txt").strings()
    val exampleInput = Reader("/day22/example-1.txt").strings()

    time(message = "Part 1", warmUpIterations = 0, iterations = 1) {
        part1(input)
    }.checkAnswer(459)

    time(message = "Part 2", warmUpIterations = 0, iterations = 1) {
        part2(input)
    }.checkAnswer(75784)
}

fun part1(input: List<String>): Int {
    val bricks = input.map(::parse)

    val (supportedByMap, supportingMap) = createMaps(bricks)

    val potential = supportedByMap.values.filter { it.size >= 2 }.flatten().toSet()
    val supportiveButCanBeDeleted = potential.count {
        supportedByMap.all { (brick, supportedBy) ->
            brick.isFloor() || (supportedBy - listOf(it).toSet()).isNotEmpty()
        }
    }
    val notSupportive = supportingMap.count { it.value.isEmpty() }

    return supportiveButCanBeDeleted + notSupportive
}

fun part2(input: List<String>): Int {
    val bricks = input.map(::parse)

    val (supportedByMap, supportingMap) = createMaps(bricks)

    return supportingMap.keys.sumOf {
        println(it)
        val mutableSupportedByMap = supportedByMap.map { (k, v) -> k to v.toMutableSet() }
        val unprocessed = mutableListOf(it)
        val falling = mutableSetOf<Brick>()
        while (unprocessed.isNotEmpty()) {
            println(unprocessed.size)
            val brick = unprocessed.removeLast()
            mutableSupportedByMap.forEach { (other, supportedBy) ->
                if (supportedBy == setOf(brick)) {
                    unprocessed.add(other)
                    falling.add(other)
                }
                supportedBy.remove(brick)
            }
        }
        falling.size
    }
}

fun createMaps(bricks: List<Brick>): Pair<Map<Brick, List<Brick>>, Map<Brick, List<Brick>>> {
    var unsettled = bricks;

    val settledBricks = mutableListOf<Brick>()
    val settledMunged = mutableSetOf<Vector3>()

    val floor = (0..9).flatMap { x -> (0..9).map { y -> (Vector3(x, y, 0)) } }.toSet()
    settledMunged.addAll(floor)

    while (unsettled.isNotEmpty()) {
        println(unsettled.size)
        val (noneBelow, someBelow) =
            unsettled.partition { it.hasNoneBelowIt(unsettled) }
        noneBelow.map {
            var brick = it
            while (true) {
                val newBrick = brick.down()
                if (newBrick.any { newBrickCube -> settledMunged.contains(newBrickCube) }) break
                else brick = newBrick
            }
            settledBricks.add(brick)
            settledMunged.addAll(brick)
        }
        unsettled = someBelow
    }

    require(settledBricks.size == bricks.size)
    require(settledBricks.toSet().isSettled(floor))

    val supportedByMap = (settledBricks + setOf(floor)).getSupportedByMap()
    val supportingMap = settledBricks.getSupportingMap()

    return Pair(supportedByMap, supportingMap)
}

fun Brick.isFloor() = first().z == 0L

fun Set<Brick>.isSettled(floor: Brick): Boolean {
    val munged = (this + setOf(floor)).flatten()
    return this.all { brick ->
        brick.any { cube ->
            cube.copy(z = cube.z - 1) in (munged - brick)
        }
    }
}

fun Brick.up(): Brick {
    val newBrick = mutableSetOf<Vector3>()
    this.forEach { newBrick.add(it.copy(z = it.z + 1)) }
    return newBrick
}

fun Brick.down(): Brick {
    val newBrick = mutableSetOf<Vector3>()
    this.forEach { newBrick.add(it.copy(z = it.z - 1)) }
    return newBrick
}

fun Collection<Brick>.getSupportedByMap() =
    this.associateWith { brick ->
        (this - setOf(brick)).filter { otherBrick ->
            brick.isSupportedBy(otherBrick)
        }
    }

fun Collection<Brick>.getSupportingMap() =
    this.associateWith { brick ->
        (this - setOf(brick)).filter { otherBrick ->
            brick.isSupporting(otherBrick)
        }
    }

fun Brick.isSupporting(other: Brick) =
    this.any { cube ->
        other.any { otherCube ->
            cube + Vector3(0, 0, 1) == otherCube
        }
    }

fun Brick.isSupportedBy(other: Brick) =
    this.any { cube ->
        other.any { otherCube ->
            cube - Vector3(0, 0, 1) == otherCube
        }
    }

fun Brick.hasNoneBelowIt(bricks: List<Brick>) =
    (bricks - setOf(this)).none {
        it.isBelow(this)
    }

fun Brick.isBelow(other: Brick) =
    this.any { cube ->
        other.any { otherCube ->
            cube.x == otherCube.x && cube.y == otherCube.y && cube.z < otherCube.z
        }
    }

fun parse(line: String): Set<Vector3> =
    line.split("~").map { it.split(",").map(String::toInt) }.let { (a, b) ->
        when {
            a[0] != b[0] -> (a[0] to b[0]).toInts().map { Vector3(it, a[1], a[2]) }
            a[1] != b[1] -> (a[1] to b[1]).toInts().map { Vector3(a[0], it, a[2]) }
            a[2] != b[2] -> (a[2] to b[2]).toInts().map { Vector3(a[0], a[1], it) }
            else -> listOf(Vector3(a[0], a[1], a[2]))
        }
    }.toSet()

fun Pair<Int, Int>.toInts() =
    if (first < second) first..second
    else second..first

typealias Brick = Set<Vector3>
