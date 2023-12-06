package lib

import lib.RangeBoundary.End
import lib.RangeBoundary.Start
import kotlin.math.max
import kotlin.math.min

fun LongRange.shift(n: Long): LongRange =
    (this.first + n)..(this.last + n)

fun LongRange.overlap(other: LongRange): LongRange? {
    if (this.first > other.last || this.last < other.first) return null
    val low = max(this.first, other.first)
    val high = min(this.last, other.last)
    return low..high
}

fun LongRange.subtract(other: LongRange): List<LongRange> {
    if (this.first > other.last || this.last < other.first) return listOf(this)
    val low = max(this.first, other.first)
    val high = min(this.last, other.last)
    val remainders = mutableListOf<LongRange>()
    if (low > this.first) remainders.add(this.first until low)
    if (high < this.last) remainders.add((high + 1)..this.last)
    return remainders
}

fun List<LongRange>.consolidate(): List<LongRange> {
    val points = this.flatMap { listOf(Start(it.first), End(it.last)) }.sortedBy(RangeBoundary::value)
    var depth = 0;
    var start: Long? = null
    val ranges = mutableListOf<LongRange>()
    points.forEach { point ->
        when (point) {
            is Start -> {
                depth += 1
                if (start == null) start = point.value
            }
            is End -> {
                depth -= 1
                if (depth == 0) {
                    ranges.add((start!!)..(point.value))
                    start = null
                }
            }
        }
    }
    return ranges
}

private sealed class RangeBoundary(open val value: Long) {
    class Start(override val value: Long) : RangeBoundary(value)
    class End(override val value: Long) : RangeBoundary(value)
}
