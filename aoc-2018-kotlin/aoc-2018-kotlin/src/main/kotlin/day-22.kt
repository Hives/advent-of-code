import java.util.PriorityQueue

fun main() {
//    val target = Vector(10, 10)
//    val depth = 510

    val target = Vector(14, 778)
    val depth = 11_541

    val regions = Regions(target, depth)

    println("part 1: ${regions.riskLevel()}")
    println("part 2: ${dijk(target, regions)}")
}

private fun dijk(target: Vector, regions: Regions): Int {
    val initial = State(
        location = Vector(0, 0),
        tool = Tool.TORCH,
    )

    val queue = PriorityQueue<VisitedState>()
    queue.add(VisitedState(state = initial, cost = 0))

    val minimumCosts = mutableMapOf(initial to 0)

    while (queue.isNotEmpty()) {
        val current = queue.poll()

        if (current.state == State(target, Tool.TORCH)) return current.cost

        val accessibleNeighbours = current.state.location.neighbours
            .filterNot { it.x < 0 || it.y < 0 }
            .filter { regions.getTerrain(it).validTools.contains(current.state.tool) }
            .map { location ->
                val state = State(location, current.state.tool)
                VisitedState(state, current.cost + 1)
            }

        val alternativeTools = regions.getTerrain(current.state.location).validTools
            .filterNot { it == current.state.tool }
            .map { tool ->
                val state = State(current.state.location, tool)
                VisitedState(state, current.cost + 7)
            }

        (accessibleNeighbours + alternativeTools).forEach {
            if (minimumCosts.getOrDefault(it.state, Int.MAX_VALUE) > it.cost) {
                minimumCosts[it.state] = it.cost
                queue.add(VisitedState(it.state, it.cost))
            }
        }
    }

    throw Exception("Oh no")
}

private data class State(
    val location: Vector,
    val tool: Tool
)

private data class VisitedState(
    val state: State,
    val cost: Int
): Comparable<VisitedState> {
    override fun compareTo(other: VisitedState) = this.cost.compareTo(other.cost)
}

private class Regions(private val target: Vector, private val depth: Int) {
    private val regions = mutableMapOf<Vector, Region>()

    fun getTerrain(location: Vector) =
        Terrain.fromErosionLevel(getRegion(location).erosionLevel)

    fun riskLevel() = (0..target.x).flatMap { x ->
        (0 .. target.y).map { y ->
            getTerrain(Vector(x, y)).value
        }
    }.sum()

    private fun getRegion(location: Vector): Region {
        regions[location]?.also { return it }

        val geologicIndex = when {
            location == Vector(0, 0) -> 0
            location == target -> 0
            location.y == 0 -> location.x * 16_807
            location.x == 0 -> location.y * 48_271
            else -> {
                getRegion(Vector(location.x - 1, location.y)).erosionLevel *
                        getRegion(Vector(location.x, location.y - 1)).erosionLevel
            }
        }

        val erosionLevel = (geologicIndex + depth) % 20_183

        val region = Region(geologicIndex, erosionLevel)

        regions[location] = region

        return region
    }

    private data class Region(
        val geologicIndex: Int,
        val erosionLevel: Int,
    )
}

private enum class Terrain(val value: Int, val validTools: Set<Tool>) {
    ROCKY(0, setOf(Tool.CLIMBING_GEAR, Tool.TORCH)),
    WET(1, setOf(Tool.CLIMBING_GEAR, Tool.NONE)),
    NARROW(2, setOf(Tool.TORCH, Tool.NONE));

    companion object {
        fun fromErosionLevel(erosionLevel: Int): Terrain =
            Terrain.values().first { it.value == erosionLevel % 3 }
    }
}

private enum class Tool { TORCH, CLIMBING_GEAR, NONE }