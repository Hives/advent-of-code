package days.day11

tailrec fun run(chairs: List<String>): List<String> {
    val newChairs = newChairs(chairs)
    return if (newChairs == chairs) chairs
    else run(newChairs)
}

fun newChairs(floor: List<String>): List<String> =
    floor.mapIndexed { y, row ->
        row.mapIndexed { x, location ->
            val neighbours = getNeighbours(x, y, floor)
            when {
                location == 'L' && neighbours.count { it == '#' } == 0 -> '#'
                location == '#' && neighbours.count { it == '#' } >= 4 -> 'L'
                else -> location
            }
        }.joinToString("")
    }

private fun getNeighbours(x: Int, y: Int, chairs: List<String>): List<Char?> =
    listOf(
        Pair(-1, -1),
        Pair(0, -1),
        Pair(1, -1),
        Pair(-1, 0),
        Pair(1, 0),
        Pair(-1, 1),
        Pair(0, 1),
        Pair(1, 1),
    ).map { chairs.getOrNull(y + it.second)?.getOrNull(x + it.first) }
