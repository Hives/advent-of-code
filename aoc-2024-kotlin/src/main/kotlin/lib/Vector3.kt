package lib

import kotlin.math.abs

data class Vector3(val x: Long, val y: Long, val z: Long) {
    constructor(x: Int, y: Int, z: Int) : this(x.toLong(), y.toLong(), z.toLong())

    val manhattanDistance
        get() = abs(x) + abs(y) + abs(z)

    operator fun times(n: Int) = Vector3(
        x = x * n,
        y = y * n,
        z = z * n
    )

    operator fun times(n: Long) = Vector3(
        x = x * n,
        y = y * n,
        z = z * n
    )

    operator fun plus(other: Vector3) = Vector3(
        x = this.x + other.x,
        y = this.y + other.y,
        z = this.z + other.z,
    )

    operator fun minus(other: Vector3) = Vector3(
        x = this.x - other.x,
        y = this.y - other.y,
        z = this.z - other.z,
    )

    override fun toString() = "v{$x, $y, $z}"

    val neighbours: Set<Vector3>
        get() = directions3D.map { this + it }.toSet()

    val surrounding: Set<Vector3>
        get() = allDirections3D.map { this + it }.toSet()
}

enum class Directions3D(val vector: Vector3) {
    UP(Vector3(0, 1, 0)),
    DOWN(Vector3(0, -1, 0)),
    LEFT(Vector3(-1, 0, 0)),
    RIGHT(Vector3(1, 0, 0)),
    FORWARD(Vector3(0, 0, 1)),
    BACKWARD(Vector3(0, 0, -1)),
}

val directions3D = Directions3D.values().map(Directions3D::vector)

val allDirections3D = listOf(
    // top
    Vector3(-1, -1, 1),
    Vector3(0, -1, 1),
    Vector3(1, -1, 1),
    Vector3(-1, 0, 1),
    Vector3(0, 0, 1),
    Vector3(1, 0, 1),
    Vector3(-1, 1, 1),
    Vector3(0, 1, 1),
    Vector3(1, 1, 1),
    // middle
    Vector3(-1, -1, 0),
    Vector3(0, -1, 0),
    Vector3(1, -1, 0),
    Vector3(-1, 0, 0),
    Vector3(1, 0, 0),
    Vector3(-1, 1, 0),
    Vector3(0, 1, 0),
    Vector3(1, 1, 0),
    // bottom
    Vector3(-1, -1, -1),
    Vector3(0, -1, -1),
    Vector3(1, -1, -1),
    Vector3(-1, 0, -1),
    Vector3(0, 0, -1),
    Vector3(1, 0, -1),
    Vector3(-1, 1, -1),
    Vector3(0, 1, -1),
    Vector3(1, 1, -1),
)
