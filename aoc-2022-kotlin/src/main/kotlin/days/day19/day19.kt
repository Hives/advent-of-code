package days.day19

import lib.Reader

fun main() {
    val input = Reader("day19.txt").strings()
    val exampleInput = Reader("day19-example.txt").strings()

    part1(exampleInput)
}

fun part1(input: List<String>) {
    val blueprints = input.map(::parse)
    evaluate(blueprints.first(), 24)
}

fun evaluate(blueprint: Blueprint, totalTime: Int) {
    val initialState = State(
        time = 0,
        robots = Resources(ore = 1),
        resources = Resources(),
        robotBeingBuilt = null
    )


    val queue = mutableSetOf(initialState)

    var bestState: State = initialState

    while (queue.isNotEmpty()) {
        println("queue:")
        queue.forEach(::println)
        val current =
            queue.sortedWith(
                compareBy(
                    { it.time },
                    { it.robots },
                    { it.robotBeingBuilt?.sortOrder ?: -1 },
                    { it.resources },
                )
            ).last()
        queue.remove(current)
        print("selected:")
        println(current)
        if (current.time == totalTime + 1) {
            if (current.resources.geode > bestState.resources.geode) bestState = current
            continue
        }

        val newRobots =
            if (current.robotBeingBuilt == null) current.robots
            else {
                when (current.robotBeingBuilt) {
                    Material.ORE -> current.robots + Resources(ore = 1)
                    Material.CLAY -> current.robots + Resources(clay = 1)
                    Material.OBSIDIAN -> current.robots + Resources(obsidian = 1)
                    Material.GEODE -> current.robots + Resources(geode = 1)
                }
            }

        val potentialRobotsToBuild = mutableListOf(Pair(Resources(), null as Material?))
        if (current.resources.canBuild(blueprint.oreRobot)) potentialRobotsToBuild.add(
            Pair(
                blueprint.oreRobot,
                Material.ORE
            )
        )
        if (current.resources.canBuild(blueprint.clayRobot)) potentialRobotsToBuild.add(
            Pair(
                blueprint.clayRobot,
                Material.CLAY
            )
        )
        if (current.resources.canBuild(blueprint.obsidianRobot)) potentialRobotsToBuild.add(
            Pair(
                blueprint.obsidianRobot,
                Material.OBSIDIAN
            )
        )
        if (current.resources.canBuild(blueprint.geodeRobot)) potentialRobotsToBuild.add(
            Pair(
                blueprint.geodeRobot,
                Material.GEODE
            )
        )

        println("potential new robots")
        println(potentialRobotsToBuild.map { it.second })

        val newStates = potentialRobotsToBuild.map { (cost, robotToBuild) ->
            State(
                time = current.time + 1,
                robots = newRobots,
                resources = current.resources + current.robots - cost,
                robotBeingBuilt = robotToBuild
            )
        }.filter { it !in queue }

        println("new states:")
        println(newStates)

        newStates.forEach { newState ->
            queue.add(newState)
        }
    }
}

data class State(val time: Int, val robots: Resources, val resources: Resources, val robotBeingBuilt: Material?) :
    Comparable<State> {
    override fun compareTo(other: State): Int =
        when {
            this.resources.geode < other.resources.geode -> -1
            this.resources.obsidian < other.resources.obsidian -> -1
            this.resources.clay < other.resources.clay -> -1
            else -> this.resources.clay.compareTo(other.resources.clay)
        }
}

data class Resources(val ore: Int = 0, val clay: Int = 0, val obsidian: Int = 0, val geode: Int = 0) :
    Comparable<Resources> {
    operator fun plus(other: Resources): Resources =
        Resources(
            ore = this.ore + other.ore,
            clay = this.clay + other.clay,
            obsidian = this.obsidian + other.obsidian,
            geode = this.geode + other.geode
        )

    operator fun minus(other: Resources): Resources {
        require(other.geode == 0) { "Should never need to deduct any geodes?! Tried to subtract this: $other" }
        return Resources(
            ore = this.ore - other.ore,
            clay = this.clay - other.clay,
            obsidian = this.obsidian - other.obsidian,
            geode = this.geode - other.geode
        )
    }

    fun canBuild(cost: Resources) =
        this.ore >= cost.ore &&
                this.clay >= cost.clay &&
                this.obsidian >= cost.obsidian &&
                this.geode >= cost.geode

    override fun compareTo(other: Resources): Int =
        when {
            this.geode < other.geode -> -1
            this.obsidian < other.obsidian -> -1
            this.clay < other.clay -> -1
            else -> this.clay.compareTo(other.clay)
        }
}

enum class Material(val sortOrder: Int) { ORE(0), CLAY(1), OBSIDIAN(2), GEODE(3); }

data class Blueprint(
    val id: String,
    val oreRobot: Resources,
    val clayRobot: Resources,
    val obsidianRobot: Resources,
    val geodeRobot: Resources
)

fun parse(input: String): Blueprint {
    val r =
        """Blueprint (\d+): Each ore robot costs (\d+) ore. Each clay robot costs (\d+) ore. Each obsidian robot costs (\d+) ore and (\d+) clay. Each geode robot costs (\d+) ore and (\d+) obsidian.""".toRegex()
    return r.find(input)!!.destructured.let { (id, oreCostOre, clayCostOre, obsidianCostOre, obsidianCostClay, geodeCostOre, geodeCostObsidian) ->
        Blueprint(
            id = id,
            oreRobot = Resources(ore = oreCostOre.toInt()),
            clayRobot = Resources(ore = clayCostOre.toInt()),
            obsidianRobot = Resources(
                ore = obsidianCostOre.toInt(),
                clay = obsidianCostClay.toInt(),
            ),
            geodeRobot = Resources(
                ore = geodeCostOre.toInt(),
                obsidian = geodeCostObsidian.toInt()
            )
        )
    }
}
