package days.day19_2

import days.day19_2.Resource.CLAY
import days.day19_2.Resource.GEODE
import days.day19_2.Resource.OBSIDIAN
import days.day19_2.Resource.ORE
import lib.Reader
import java.util.PriorityQueue

fun main() {
    val input = Reader("day19.txt").strings()
    val exampleInput = Reader("day19-example.txt").strings()

    part1(exampleInput)
}

fun part1(input: List<String>) {
    val blueprints = input.map(::parse)
    val solver = Solver(blueprints[1], 24)
    solver.dfs()
}

class Solver(
    private val blueprint: Blueprint,
    private val totalTime: Int
) {
    var completeBranchesChecked = 0

    fun dfs() {
        val queue = PriorityQueue<List<Resource?>>(
            compareBy(
//                { path -> -path.size },
//                { path -> maxPossible(path) },
                { path -> evaluate(path).geodes },
                { path -> evaluate(path).robots.getOrDefault(GEODE, 0) },
                { path -> evaluate(path).robots.getOrDefault(OBSIDIAN, 0) },
            )
        )
        queue.add(emptyList())

        while (queue.isNotEmpty()) {
            println()
            println("best so far: ${evaluate(bestCompletePath).geodes}")
            println(bestCompletePath)
            println("queue size: ${queue.size}")
            val current = queue.poll()
            println("current: $current")
            println("length: ${current.size}")
            println("evaluated: ${evaluate(current)}")
            println("geodes: ${evaluate(current).geodes}")

            if (maxPossible(current) > evaluate(bestCompletePath).geodes) {
                nextPaths(current)
                    .forEach { newPath ->
                        if (evaluate(newPath).geodes > evaluate(bestCompletePath).geodes) {
                            bestCompletePath = newPath
                            queue.filter {
                                maxPossible(it) > evaluate(bestCompletePath).geodes
                            }
                        }
                        if (!isComplete(newPath)) {
                            queue.add(newPath)
                        }
                    }
            }
        }

        println("finished")
    }

    fun evaluate(path: Path): State =
        when {
            path.isEmpty() -> initialState
            path in cachedEvaluations -> cachedEvaluations[path]!!
            else -> {
                val lastMove = path.getOrNull(path.size - 1)
                val lastButOneMove = path.getOrNull(path.size - 2)
                val prevState =
                    if (path.isEmpty()) initialState
                    else evaluate(path.subList(0, path.size - 1))

                val newRobots =
                    if (lastButOneMove != null) prevState.robots.plus(lastButOneMove)
                    else prevState.robots

                val newResources =
                    prevState.resources.plus(newRobots).let {
                        if (lastMove != null) {
                            val cost = blueprint[lastMove]!!
                            it.deduct(cost)
                        } else it
                    }

                val newState = State(
                    resources = newResources,
                    robots = newRobots
                )

                cachedEvaluations[path] = newState

                newState
            }
        }

    private fun maxPossible(path: Path): Int {
        val state = evaluate(path)
        val remainingTime = totalTime - path.size
        val geodesIfWeBuiltANewGeodeRobotEveryTurn = (remainingTime * (remainingTime - 1)) / 2
        val geodesMinedByExistingRobots = remainingTime * state.robots.getOrDefault(GEODE, 0)
        return state.geodes + geodesMinedByExistingRobots + geodesIfWeBuiltANewGeodeRobotEveryTurn
    }

    private fun nextPaths(path: Path): List<Path> {
        val state = evaluate(path)
        val remainingTime = totalTime - path.size

        val possibleRobots = Resource.values()
            .filter { robot ->
                val cost = blueprint[robot]!!
                cost.all { (resource, count) ->
                    state.resources.getOrDefault(resource, 0) >= count
                }
            }
//        println("possible robots: $possibleRobots")

        val sensibleRobots = remainingTimeRequiredToBeWorthBuilding
            .filter { (_, time) -> remainingTime >= time }
            .keys
//        println("sensible robots: $sensibleRobots")

        return (possibleRobots.intersect(sensibleRobots) + null).map { path + it }
    }

    private fun isComplete(path: Path): Boolean = path.size == totalTime

    private val cachedEvaluations = mutableMapOf<Path, State>()
    private var bestCompletePath: Path = emptyList()
    private val initialState = State(
        resources = emptyMap(),
        robots = mapOf(ORE to 1)
    )
    private val remainingTimeRequiredToBeWorthBuilding = mapOf(
        ORE to 4,
        CLAY to 3,
        OBSIDIAN to 2,
        GEODE to 1
    )

}

enum class Resource { ORE, CLAY, OBSIDIAN, GEODE }

typealias Path = List<Resource?>
typealias Blueprint = Map<Resource, ResourceMap>
typealias ResourceMap = Map<Resource, Int>

fun ResourceMap.plus(other: ResourceMap) =
    (this.keys + other.keys).toSet().associateWith { resource ->
        (this.getOrDefault(resource, 0) + other.getOrDefault(resource, 0))
    }

fun ResourceMap.plus(resource: Resource) =
    this.plus(mapOf(resource to 1))

fun ResourceMap.deduct(other: ResourceMap): ResourceMap {
    val newMap = (this.keys + other.keys).toSet().associateWith { resource ->
        (this.getOrDefault(resource, 0) - other.getOrDefault(resource, 0))
    }
    require(newMap.all { it.value >= 0 }) { "Should never be able to create a resource map with less than 0 of a resource" }
    return newMap
}

data class State(
    val robots: ResourceMap,
    val resources: ResourceMap,
) {
    init {
        require(resources.values.all { it >= 0 })
        require(robots.values.all { it >= 0 })
    }

    val geodes: Int
        get() = resources.getOrDefault(GEODE, 0)
}

fun parse(input: String): Blueprint {
    val r =
        """Blueprint (\d+): Each ore robot costs (\d+) ore. Each clay robot costs (\d+) ore. Each obsidian robot costs (\d+) ore and (\d+) clay. Each geode robot costs (\d+) ore and (\d+) obsidian.""".toRegex()
    return r.find(input)!!.destructured.let { (id, oreCostOre, clayCostOre, obsidianCostOre, obsidianCostClay, geodeCostOre, geodeCostObsidian) ->
        mapOf(
            ORE to mapOf(
                ORE to oreCostOre.toInt()
            ),
            CLAY to mapOf(
                ORE to clayCostOre.toInt()
            ),
            OBSIDIAN to mapOf(
                ORE to obsidianCostOre.toInt(),
                CLAY to obsidianCostClay.toInt()
            ),
            GEODE to mapOf(
                ORE to geodeCostOre.toInt(),
                OBSIDIAN to geodeCostObsidian.toInt()
            )
        )
    }
}
