package days.day19

import dev.forkhandles.tuples.Tuple4
import lib.Reader
import lib.Vector3
import lib.checkAnswer

fun main() {
    val input = Reader("day19.txt").string()
    val exampleInput = Reader("day19-example.txt").string()

    val stuff = firstThing(input)
    part1(stuff).checkAnswer(398)
    part2(stuff).checkAnswer(10965)
}

fun firstThing(input: String): List<Triple<Scanner, Orientation, Vector3>> {
    val scanners = parse(input)

    val scanner0 = scanners[0]
    val coupleDistances0 = scanner0.getRelativeDistances(Orientation.A)

    val unorientedScanners = scanners.drop(1).associateWith { scanner ->
        Orientation.values().map { orientation ->
            Triple(scanner, orientation, scanner.getRelativeDistances(orientation))
        }
    }

    val orientedScanners = mutableListOf(Tuple4(scanner0, Orientation.A, coupleDistances0, Vector3(0, 0, 0)))

    return orientScanners(orientedScanners, unorientedScanners)
}

fun part1(stuff: List<Triple<Scanner, Orientation, Vector3>>) =
    stuff.flatMap { (scanner, orientation, offset) ->
        scanner.beacons.map { orientation.orient(it) - offset }
    }.distinct().size

fun part2(stuff: List<Triple<Scanner, Orientation, Vector3>>): Int? {
    val offsets = stuff.map { (_, _, offset) -> offset }

    return offsets.flatMap { offset1 ->
        offsets.map { offset2 ->
            (offset2 - offset1).manhattanDistance
        }
    }.maxOrNull().also { println(it) }
}

tailrec fun orientScanners(
    orientedScanners: List<Tuple4<Scanner, Orientation, Map<Set<Vector3>, Vector3>, Vector3>>,
    unorientedScanners: Map<Scanner, List<Triple<Scanner, Orientation, Map<Set<Vector3>, Vector3>>>>,
): List<Triple<Scanner, Orientation, Vector3>> {
    println("unoriented: ${unorientedScanners.size}")
    if (unorientedScanners.isEmpty()) {
        return orientedScanners.map { (scanner, orientation, _, offset) ->
            Triple(scanner, orientation, offset)
        }
    } else {
        val newlyOriented = mutableListOf<Tuple4<Scanner, Orientation, Map<Set<Vector3>, Vector3>, Vector3>>()

        unorientedScanners.values.forEach {
            var match: Tuple4<Scanner, Orientation, Map<Set<Vector3>, Vector3>, Vector3>? = null

            it.forEach outer@{ (scanner, orientation, coupleDistances) ->
                orientedScanners.forEach { (_, _, establishedCoupleDistances, establishedOffset) ->
                    val matchedDistances = establishedCoupleDistances.values.intersect(coupleDistances.values)
                    val matchCount = matchedDistances.size
                    if (matchCount > 50) {
                        if (match != null) throw Exception("Uh oh 1")

                        val matchedDistance = matchedDistances.first()
                        val establishedPoints =
                            establishedCoupleDistances.filterValues { value -> value == matchedDistance }.keys.single()
                        val points = coupleDistances.filterValues { value -> value == matchedDistance }.keys.single()

                        val (start1, end1) = establishedPoints.toList().sorted()
                        val (start2, end2) = points.toList().sorted()

                        val offset = start2 - start1

                        match = Tuple4(scanner, orientation, coupleDistances, establishedOffset + offset)
                        return@outer
                    }
                }
            }

            if (match != null) {
                newlyOriented.add(match as Tuple4<Scanner, Orientation, Map<Set<Vector3>, Vector3>, Vector3>)
            }
        }

        val allOriented = orientedScanners + newlyOriented
        val stillUnoriented =
            unorientedScanners.filterKeys { scanner -> scanner !in allOriented.map { (scanner, _, _) -> scanner } }

        return orientScanners(allOriented, stillUnoriented)
    }
}

data class Scanner(val id: Int, val beacons: List<ScannedBeacon>) {
    fun getRelativeDistances(orientation: Orientation): Map<Set<Vector3>, Vector3> {
        val orientedBeacons = beacons.map(orientation.orient)

        return orientedBeacons.flatMap { first: Vector3 ->
            orientedBeacons.mapNotNull { second: Vector3 ->
                if (first == second) null
                else setOf(first, second) to if (first > second) first - second else second - first
            }
        }.toMap()
    }
}

enum class Orientation(val orient: (ScannedBeacon) -> Vector3) {
    // facing y, z is up
    A({ (one, two, three) -> Vector3(one, two, three) }),

    // facing y, x is up
    B({ (one, two, three) -> Vector3(-three, two, one) }),

    // facing y, z is down
    C({ (one, two, three) -> Vector3(-one, two, -three) }),

    // facing y, x is down
    D({ (one, two, three) -> Vector3(three, two, -one) }),

    // facing -y, z is up
    E({ (one, two, three) -> Vector3(-one, -two, three) }),

    // facing -y, x is up
    F({ (one, two, three) -> Vector3(three, -two, one) }),

    // facing -y, z is down
    G({ (one, two, three) -> Vector3(one, -two, -three) }),

    // facing -y, x is down
    H({ (one, two, three) -> Vector3(-three, -two, -one) }),

    // facing x, z is up
    I({ (one, two, three) -> Vector3(-two, one, three) }),

    // facing x, y is down
    J({ (one, two, three) -> Vector3(-three, one, -two) }),

    // facing x, z is down
    K({ (one, two, three) -> Vector3(two, one, -three) }),

    // facing x, y is up
    L({ (one, two, three) -> Vector3(three, one, two) }),

    // facing -x, z is up
    M({ (one, two, three) -> Vector3(two, -one, three) }),

    // facing -x, y is down
    N({ (one, two, three) -> Vector3(three, -one, -two) }),

    // facing -x, z is down
    O({ (one, two, three) -> Vector3(-two, -one, -three) }),

    // facing -x, y is up
    P({ (one, two, three) -> Vector3(-three, -one, two) }),

    // facing z, x is up
    Q({ (one, two, three) -> Vector3(two, three, one) }),

    // facing z, y is up
    R({ (one, two, three) -> Vector3(-one, three, two) }),

    // facing z, x is down
    S({ (one, two, three) -> Vector3(-two, three, -one) }),

    // facing z, y is down
    T({ (one, two, three) -> Vector3(one, three, -two) }),

    // facing -z, x is up
    U({ (one, two, three) -> Vector3(-two, -three, one) }),

    // facing -z, y is up
    V({ (one, two, three) -> Vector3(one, -three, two) }),

    // facing -z, x is down
    W({ (one, two, three) -> Vector3(two, -three, -one) }),

    // facing -z, y is down
    X({ (one, two, three) -> Vector3(-one, -three, -two) }),
}

typealias ScannedBeacon = Triple<Int, Int, Int>

fun parse(input: String): List<Scanner> =
    input.split("\n\n")
        .map { block ->
            val id = block.lines().first().substringAfter("--- scanner ").substringBefore(" ---").toInt()

            val points = block.lines().drop(1).map { position ->
                position.split(",")
                    .map(String::toInt)
                    .let { (a, b, c) -> Triple(a, b, c) }
            }

            Scanner(id, points)
        }
