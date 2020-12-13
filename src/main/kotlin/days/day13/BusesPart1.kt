package days.day13

import kotlin.math.ceil

fun doPart1(timestamp: Int, busIds: List<Int>) =
    busIds
        .map { Pair(it, getFirstBusDepartureAfterTime(it, timestamp)) }
        .minByOrNull { it.second }
        ?.let { (it.second - timestamp) * it.first }

fun getFirstBusDepartureAfterTime(busId: Int, time: Int) = (ceil(time.toDouble() / busId) * busId).toInt()
