package days.day11

tailrec fun run(chairs: List<String>): List<String> {
    val newChairs = newChairs(chairs)
    return if (newChairs == chairs) chairs
    else run(newChairs)
}

fun newChairs(chairs: List<String>): List<String> =
    chairs.mapIndexed { y, row ->
        row.mapIndexed { x, chair ->
            val neighbours = getNeighbours(x, y, chairs)
            when {
                chair == 'L' && neighbours.count { it == '#' } == 0 -> '#'
                chair == '#' && neighbours.count { it == '#' } >= 4 -> 'L'
                else -> chair
            }
        }.joinToString("")
    }

private fun getNeighbours(x: Int, y: Int, chairs: List<String>): List<Char?> =
    listOf(
        Pair(x - 1, y - 1),
        Pair(x, y - 1),
        Pair(x + 1, y - 1),
        Pair(x - 1, y),
        Pair(x + 1, y),
        Pair(x - 1, y + 1),
        Pair(x, y + 1),
        Pair(x + 1, y + 1),
    ).map { chairs.getOrNull(it.second)?.getOrNull(it.first) }
