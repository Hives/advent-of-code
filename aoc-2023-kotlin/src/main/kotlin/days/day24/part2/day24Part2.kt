package days.day24.part2

import lib.Reader
import lib.Vector3Double
import lib.checkAnswer
import lib.time
import kotlin.math.abs
import kotlin.system.exitProcess

fun main() {
    val input = Reader("/day24/input.txt").strings()
    val exampleInput = Reader("/day24/example-1.txt").strings()

    println(part2(exampleInput))

    exitProcess(0)

    time(message = "Part 2") {
        part2(input)
    }.checkAnswer(25261)
}

fun part2(input: List<String>): Long {
    val hailstones = input.map(::parse)

    val thrown = Line(Vector3Double(24, 13, 10), Vector3Double(-3, 1, 2))
    hailstones.forEach {
        println("--")
        println(it)
        println(testCollision(thrown, it))
    }

    // take 3 hailstones, h1, h2 + h3
    // for a time t1, find the location of h1. call it p1
    // project that point through the path of h2 onto the path of h3 to find p2 and p3
    // work out the velocity with which you would have to fire a particle from p1 to hit p2 at the point h2 is at that point
    // if that trajectory also hits p3 at the time when h3 is there, then you've probably found the trajectory you need
    // if not find out how far away h3 is from p3 at that point (d3)
    // use binary partition or something to minimise d3 to 0, then you've found the trajectory

    return -1
}

fun testCollision(h1: Line, h2: Line): Boolean {
    val collisionPoint = findIntersection(h1, h2)
    val t = (collisionPoint.x - h1.point.x) / h1.direction.x
    val p1 = h1.point + (h1.direction * t)
    val p2 = h2.point + (h2.direction * t)
    return p1 == p2
}

fun test(t: Long, h1: Line, h2: Line, h3: Line): Double {
    val point1 = h1.point + (h1.direction * t)

    val spanVector1 = h2.point - point1
    require(spanVector1 != Vector3Double(0, 0, 0)) { "spanVector1 was (0, 0, 0)" }
    val spanVector2 = h2.direction

    val point3 = findIntersection(h3, Plane(point1, spanVector1, spanVector2))

    val point2 = findIntersection(h2, Line(point1, point3 - point1))

    val t2_x = (point2 - h2.point).x / h2.direction.x
    val t2_y = (point2 - h2.point).y / h2.direction.y
    val t2_z = (point2 - h2.point).z / h2.direction.z
    checkCloseness(t2_x, t2_y, t2_z)
    val t2 = t2_x

    val velocity = (point2 - point1) * (1 / (t2 - t))

    val t3_x = (point3 - point1).x / velocity.x
    val t3_y = (point3 - point1).y / velocity.y
    val t3_z = (point3 - point1).z / velocity.z
    checkCloseness(t2_x, t3_y, t3_z)
    val t3 = t3_x

    val positionOfH3 = h3.point + (h3.direction * t3)

    return point3.x - positionOfH3.x
}

fun checkCloseness(a: Double, b: Double, c: Double) {
//    println("a - b: ${a - b}")
//    println("a - c: ${a - c}")
}

fun findIntersection(line: Line, plane: Plane): Vector3Double {
    // https://en.wikipedia.org/wiki/Line%E2%80%93plane_intersection#Parametric_form
    val planeNormal = plane.span1.cross(plane.span2)
    val numerator = planeNormal.dot(line.point - plane.point)
    val denominator = -1 * line.direction.dot(planeNormal)
    val t = numerator / denominator
    return line.point + (line.direction * t)
}

fun findIntersection(line1: Line, line2: Line): Vector3Double {
    // https://gamedev.stackexchange.com/a/44733
    val numerator =
        (line1.direction.x * (line2.point.y - line1.point.y)) + (line1.direction.y * (line1.point.x - line2.point.x))
    val denominator = (line2.direction.x * line1.direction.y) - (line2.direction.y * line1.direction.x)
    require(denominator != 0.00)
    val u = numerator / denominator
    return line2.point + (line2.direction * u)
}

fun Vector3Double.cross(other: Vector3Double) =
    Vector3Double(
        (this.y * other.z) - (this.z * other.y),
        (this.z * other.x) - (this.x * other.z),
        (this.x * other.y) - (this.y * other.x),
    )

fun Vector3Double.dot(other: Vector3Double) =
    (this.x * other.x) + (this.y * other.y) + (this.z * other.z)

fun parse(line: String): Line =
    line.split(" @ ").let { (p, v) ->
        Line(
            p.split(", ").let { (x, y, z) -> Vector3Double(x.toLong(), y.toLong(), z.toLong()) },
            v.split(", ").let { (x, y, z) -> Vector3Double(x.toLong(), y.toLong(), z.toLong()) },
        )
    }

data class Line(val point: Vector3Double, val direction: Vector3Double)
data class Plane(val point: Vector3Double, val span1: Vector3Double, val span2: Vector3Double)
