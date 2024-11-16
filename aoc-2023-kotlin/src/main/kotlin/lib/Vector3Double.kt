package lib

import kotlin.math.pow
import kotlin.math.sqrt

data class Vector3Double(val x: Double, val y: Double, val z: Double) {
    constructor(x: Int, y: Int, z: Int) : this(x.toDouble(), y.toDouble(), z.toDouble())
    constructor(x: Long, y: Long, z: Long) : this(x.toDouble(), y.toDouble(), z.toDouble())

    operator fun times(n: Int) = Vector3Double(
        x = x * n,
        y = y * n,
        z = z * n
    )

    operator fun times(n: Long) = Vector3Double(
        x = x * n,
        y = y * n,
        z = z * n
    )

    operator fun times(n: Double) = Vector3Double(
        x = x * n,
        y = y * n,
        z = z * n
    )

    operator fun plus(other: Vector3Double) = Vector3Double(
        x = this.x + other.x,
        y = this.y + other.y,
        z = this.z + other.z,
    )

    operator fun minus(other: Vector3Double) = Vector3Double(
        x = this.x - other.x,
        y = this.y - other.y,
        z = this.z - other.z,
    )

    fun normalise() =
        this * (1.0 / sqrt(x.pow(2) + y.pow(2) + z.pow(2)))

    override fun toString() = "v{$x, $y, $z}"
}
