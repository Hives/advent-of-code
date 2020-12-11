package days.day11

tailrec fun run2(chairs: List<String>): List<String> {
    val newChairs = newChairs2(chairs)
    return if (newChairs == chairs) chairs
    else run2(newChairs)
}

fun newChairs2(floor: List<String>): List<String> =
    floor.mapIndexed { y, row ->
        row.mapIndexed { x, location ->
            val visibleChairs = getVisibleChairs(x, y, floor)
            when {
                location == 'L' && visibleChairs.count { it == '#' } == 0 -> '#'
                location == '#' && visibleChairs.count { it == '#' } >= 5 -> 'L'
                else -> location
            }
        }.joinToString("")
    }

fun getVisibleChairs(x: Int, y: Int, floor: List<String>): List<Char?> =
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
        var location: Char?
        var r = 0
        do {
            r++
            location = floor.getOrNull(y + (it.second * r))?.getOrNull(x + (it.first * r))
        } while (location == '.')
        location
    }
