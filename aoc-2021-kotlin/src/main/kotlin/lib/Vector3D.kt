package lib

import kotlin.math.abs

data class Vector3D(val x: Int, val y: Int, val z: Int) : Comparable<Vector3D> {
    val manhattanDistance: Int
        get() = abs(x) + abs(y) + abs(z)

    operator fun times(n: Int) = Vector3D(
        x = x * n,
        y = y * n,
        z = y * n
    )

    operator fun plus(other: Vector3D) = Vector3D(
        x = this.x + other.x,
        y = this.y + other.y,
        z = this.z + other.z,
    )

    operator fun minus(other: Vector3D) = Vector3D(
        x = this.x - other.x,
        y = this.y - other.y,
        z = this.z - other.z,
    )

    override fun toString() = "v{$x, $y, $z}"

    override fun compareTo(other: Vector3D): Int {
        return metric() - other.metric()
    }

    private fun metric() = (1_000_000 * x) + (1_000 * y) + z
}