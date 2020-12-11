package days.day11

tailrec fun run2(chairs: List<String>): List<String> {
    val newChairs = newChairs2(chairs)
    return if (newChairs == chairs) chairs
    else run2(newChairs)
}

fun newChairs2(chairs: List<String>): List<String> =
    chairs.mapIndexed { y, row ->
        row.mapIndexed { x, chair ->
            val visibleSeats = getVisibleSeats(x, y, chairs)
            when {
                chair == 'L' && visibleSeats.count { it == '#' } == 0 -> '#'
                chair == '#' && visibleSeats.count { it == '#' } >= 5 -> 'L'
                else -> chair
            }
        }.joinToString("")
    }

fun getVisibleSeats(x: Int, y: Int, chairs: List<String>): List<Char?> =
    listOf(
        Pair(-1, -1),
        Pair(0, -1),
        Pair(1, -1),
        Pair(-1, 0),
        Pair(1, 0),
        Pair(-1, 1),
        Pair(0, 1),
        Pair(1, 1),
    ).map {
        var chair: Char?
        var i = 0
        do {
            i++
            chair = chairs.getOrNull(y + (it.second * i))?.getOrNull(x + (it.first * i))
        } while (chair == '.')
        chair
    }
