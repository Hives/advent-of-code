package days.day19

import dev.forkhandles.tuples.Tuple4
import lib.Reader
import lib.Vector3
import lib.checkAnswer
import lib.combinations

fun main() {
    val input = Reader("day19.txt").string()
    val exampleInput = Reader("day19-example.txt").string()

    val exampleStuff = firstThing(exampleInput)
    part1(exampleStuff).checkAnswer(79)
    part2(exampleStuff).checkAnswer(3621)

    val stuff = firstThing(input)
    part1(stuff).checkAnswer(398)
    part2(stuff).checkAnswer(10965)
}

fun firstThing(input: String): List<Triple<List<Vector3>, Orientation, Vector3>> {
    val scanners = parse(input)

    val unorientedScanners = scanners.drop(1).associateWith { scanner ->
        Orientation.values().map { orientation ->
            Triple(scanner, orientation, getPairwiseDistances(scanner, orientation))
        }
    }

    val orientedScanners = mutableListOf(Tuple4(
        scanners[0],
        Orientation.A,
        getPairwiseDistances(scanners[0], Orientation.A),
        Vector3(0, 0, 0)
    ))

    return orientScanners(orientedScanners, unorientedScanners)
}

fun part1(scannerStuff: List<Triple<List<Vector3>, Orientation, Vector3>>) =
    scannerStuff.flatMap { (scanner, orientation, offset) ->
        scanner.map { orientation.orient(it) - offset }
    }.distinct().size

fun part2(scannerStuff: List<Triple<List<Vector3>, Orientation, Vector3>>): Int? =
    scannerStuff.map { (_, _, offset) -> offset }
        .combinations(2)
        .map { (first, second) -> second - first }
        .map(Vector3::manhattanDistance)
        .maxOrNull()

tailrec fun orientScanners(
    orientedScanners: List<Tuple4<List<Vector3>, Orientation, Map<Set<Vector3>, Vector3>, Vector3>>,
    unorientedScanners: Map<List<Vector3>, List<Triple<List<Vector3>, Orientation, Map<Set<Vector3>, Vector3>>>>,
): List<Triple<List<Vector3>, Orientation, Vector3>> {
    println("unoriented: ${unorientedScanners.size}")
    if (unorientedScanners.isEmpty()) {
        return orientedScanners.map { (scanner, orientation, _, offset) ->
            Triple(scanner, orientation, offset)
        }
    } else {
        val newlyOriented = mutableListOf<Tuple4<List<Vector3>, Orientation, Map<Set<Vector3>, Vector3>, Vector3>>()

        unorientedScanners.values.forEach {
            var match: Tuple4<List<Vector3>, Orientation, Map<Set<Vector3>, Vector3>, Vector3>? = null

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

                        val (start1, _) = establishedPoints.toList().sorted()
                        val (start2, _) = points.toList().sorted()

                        val offset = start2 - start1

                        match = Tuple4(scanner, orientation, coupleDistances, establishedOffset + offset)
                        return@outer
                    }
                }
            }

            if (match != null) {
                newlyOriented.add(match as Tuple4<List<Vector3>, Orientation, Map<Set<Vector3>, Vector3>, Vector3>)
            }
        }

        val allOriented = orientedScanners + newlyOriented
        val stillUnoriented =
            unorientedScanners.filterKeys { scanner -> scanner !in allOriented.map { (scanner, _, _) -> scanner } }

        return orientScanners(allOriented, stillUnoriented)
    }
}

fun getPairwiseDistances(beacons: List<Vector3>, orientation: Orientation): Map<Set<Vector3>, Vector3> =
    beacons.map(orientation.orient).sorted()
        .combinations(2)
        .associate { (first, second) ->
            setOf(first, second) to second - first
        }

enum class Orientation(val orient: (Vector3) -> Vector3) {
    A({ Vector3(it.x, it.y, it.z) }), // facing y, z is up
    B({ Vector3(-it.z, it.y, it.x) }), // facing y, x is up
    C({ Vector3(-it.x, it.y, -it.z) }), // facing y, z is down
    D({ Vector3(it.z, it.y, -it.x) }), // facing y, x is down

    E({ Vector3(-it.x, -it.y, it.z) }), // facing -y, z is up
    F({ Vector3(it.z, -it.y, it.x) }), // facing -y, x is up
    G({ Vector3(it.x, -it.y, -it.z) }), // facing -y, z is down
    H({ Vector3(-it.z, -it.y, -it.x) }), // facing -y, x is down

    I({ Vector3(-it.y, it.x, it.z) }), // facing x, z is up
    J({ Vector3(-it.z, it.x, -it.y) }), // facing x, y is down
    K({ Vector3(it.y, it.x, -it.z) }), // facing x, z is down
    L({ Vector3(it.z, it.x, it.y) }), // facing x, y is up

    M({ Vector3(it.y, -it.x, it.z) }), // facing -x, z is up
    N({ Vector3(it.z, -it.x, -it.y) }), // facing -x, y is down
    O({ Vector3(-it.y, -it.x, -it.z) }), // facing -x, z is down
    P({ Vector3(-it.z, -it.x, it.y) }), // facing -x, y is up

    Q({ Vector3(it.y, it.z, it.x) }), // facing z, x is up
    R({ Vector3(-it.x, it.z, it.y) }), // facing z, y is up
    S({ Vector3(-it.y, it.z, -it.x) }), // facing z, x is down
    T({ Vector3(it.x, it.z, -it.y) }), // facing z, y is down

    U({ Vector3(-it.y, -it.z, it.x) }), // facing -z, x is up
    V({ Vector3(it.x, -it.z, it.y) }), // facing -z, y is up
    W({ Vector3(it.y, -it.z, -it.x) }), // facing -z, x is down
    X({ Vector3(-it.x, -it.z, -it.y) }), // facing -z, y is down
}

fun parse(input: String): List<List<Vector3>> =
    input.split("\n\n")
        .map { block ->
            block.lines().drop(1).map { line ->
                line.split(",")
                    .map(String::toInt)
                    .let { (x, y, z) -> Vector3(x, y, z) }
            }
        }
