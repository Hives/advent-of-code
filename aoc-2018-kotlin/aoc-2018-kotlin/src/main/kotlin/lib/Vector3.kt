package lib

import kotlin.math.abs

data class Vector3(val x: Int, val y: Int, val z: Int) : Comparable<Vector3> {
    val manhattanDistance: Int
        get() = abs(x) + abs(y) + abs(z)

    operator fun times(n: Int) = Vector3(
        x = x * n,
        y = y * n,
        z = y * n
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

    override fun compareTo(other: Vector3): Int {
        return metric() - other.metric()
    }

    private fun metric() = (1_000_000 * x) + (1_000 * y) + z
}