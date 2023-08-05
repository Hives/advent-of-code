package days.day19_3

import lib.Reader
import lib.checkAnswer
import lib.time
import java.util.PriorityQueue
import kotlin.math.ceil

fun main() {
    val input = Reader("day19.txt").strings()
    val exampleInput = Reader("day19-example.txt").strings()

    time(iterations = 2, warmUpIterations = 2, message = "Part 1") {
        part1(input)
    }.checkAnswer(1264)

    // WARNING - very slow (6 minutes)
    time(iterations = 1, warmUpIterations = 0, message = "Part 2") {
        part2(input)
    }.checkAnswer(13475)
}

fun part1(input: List<String>) =
    input.map(::parse).sumOf { findMaxGeodes(it, 24) * it.id }

fun part2(input: List<String>): Int =
    input.asSequence()
        .take(3)
        .map(::parse)
        .map { findMaxGeodes(it, 32) }
        .onEach(::println)
        .reduce(Int::times)

fun findMaxGeodes(blueprint: Blueprint, time: Int): Int {
    val initialState = State(time = time, oreRobots = 1)

    val stateQueue = PriorityQueue<State>()
    stateQueue.add(initialState)

    var maxGeodes = 0

    while (stateQueue.isNotEmpty()) {
        val state = stateQueue.poll()
        maxGeodes = maxOf(state.extrapolateGeodes(), maxGeodes)
        val newStates = getNewStates(state, blueprint)
        stateQueue.addAll(newStates)
    }

    return maxGeodes
}

fun State.extrapolateGeodes() = geodes + (time * geodeRobots)

fun getNewStates(state: State, blueprint: Blueprint): List<State> {
    val newStates = mutableListOf<State?>()
    if (state.oreRobots < blueprint.maxOreCost) {
        newStates.add(scheduleBuild(state, blueprint.oreRobotBlueprint))
    }
    if (state.clayRobots < blueprint.maxClayCost) {
        newStates.add(scheduleBuild(state, blueprint.clayRobotBlueprint))
    }
    if (state.obsidianRobots < blueprint.maxObsidianCost) {
        newStates.add(scheduleBuild(state, blueprint.obsidianRobotBlueprint))
    }
    newStates.add(scheduleBuild(state, blueprint.geodeRobotBlueprint))

    return newStates.filterNotNull().filter { it.time >= 0 }
}

fun scheduleBuild(state: State, blueprint: Blueprint.RobotBlueprint): State? {
    val timeRequired = timeToBuild(state, blueprint) ?: return null

    return State(
        time = state.time - timeRequired,
        ore = state.ore + (timeRequired * state.oreRobots) - blueprint.oreCost,
        clay = state.clay + (timeRequired * state.clayRobots) - blueprint.clayCost,
        obsidian = state.obsidian + (timeRequired * state.obsidianRobots) - blueprint.obsidianCost,
        geodes = state.geodes + (timeRequired * state.geodeRobots),
        oreRobots = state.oreRobots + blueprint.oreRobotsBuilt,
        clayRobots = state.clayRobots + blueprint.clayRobotsBuilt,
        obsidianRobots = state.obsidianRobots + blueprint.obsidianRobotsBuilt,
        geodeRobots = state.geodeRobots + blueprint.geodeRobotsBuilt,
    )
}

fun timeToBuild(state: State, blueprint: Blueprint.RobotBlueprint): Int? {
    val oreTime = when {
        blueprint.oreCost == 0 -> 0
        state.oreRobots == 0 -> return null
        state.ore >= blueprint.oreCost -> 0
        else -> ((blueprint.oreCost - state.ore) / state.oreRobots.toFloat()).rndUp()
    }
    val clayTime = when {
        blueprint.clayCost == 0 -> 0
        state.clayRobots == 0 -> return null
        state.clay >= blueprint.clayCost -> 0
        else -> ((blueprint.clayCost - state.clay) / state.clayRobots.toFloat()).rndUp()
    }
    val obsidianTime = when {
        blueprint.obsidianCost == 0 -> 0
        state.obsidianRobots == 0 -> return null
        state.obsidian >= blueprint.obsidianCost -> 0
        else -> ((blueprint.obsidianCost - state.obsidian) / state.obsidianRobots.toFloat()).rndUp()
    }

    return maxOf(oreTime, clayTime, obsidianTime) + 1
}

fun Float.rndUp() = ceil(this).toInt()

data class State(
    val time: Int,
    val ore: Int = 0,
    val clay: Int = 0,
    val obsidian: Int = 0,
    val geodes: Int = 0,
    val oreRobots: Int = 0,
    val clayRobots: Int = 0,
    val obsidianRobots: Int = 0,
    val geodeRobots: Int = 0
) : Comparable<State> {
    override fun compareTo(other: State) = other.geodes.compareTo(geodes)
}

data class Blueprint(
    val id: Int,
    val oreRobotBlueprint: RobotBlueprint,
    val clayRobotBlueprint: RobotBlueprint,
    val obsidianRobotBlueprint: RobotBlueprint,
    val geodeRobotBlueprint: RobotBlueprint,
) {
    data class RobotBlueprint(
        val oreRobotsBuilt: Int = 0,
        val clayRobotsBuilt: Int = 0,
        val obsidianRobotsBuilt: Int = 0,
        val geodeRobotsBuilt: Int = 0,
        val oreCost: Int = 0,
        val clayCost: Int = 0,
        val obsidianCost: Int = 0,
    )

    val maxOreCost = maxOf(
        oreRobotBlueprint.oreCost,
        clayRobotBlueprint.oreCost,
        obsidianRobotBlueprint.oreCost,
        geodeRobotBlueprint.oreCost
    )
    val maxClayCost = maxOf(
        oreRobotBlueprint.clayCost,
        clayRobotBlueprint.clayCost,
        obsidianRobotBlueprint.clayCost,
        geodeRobotBlueprint.clayCost
    )
    val maxObsidianCost = maxOf(
        oreRobotBlueprint.obsidianCost,
        clayRobotBlueprint.obsidianCost,
        obsidianRobotBlueprint.obsidianCost,
        geodeRobotBlueprint.obsidianCost
    )
}

fun parse(input: String): Blueprint {
    val r =
        """Blueprint (\d+): Each ore robot costs (\d+) ore. Each clay robot costs (\d+) ore. Each obsidian robot costs (\d+) ore and (\d+) clay. Each geode robot costs (\d+) ore and (\d+) obsidian.""".toRegex()
    return r.find(input)!!.destructured.let { (id, oreCostOre, clayCostOre, obsidianCostOre, obsidianCostClay, geodeCostOre, geodeCostObsidian) ->
        Blueprint(
            id = id.toInt(),
            oreRobotBlueprint = Blueprint.RobotBlueprint(
                oreRobotsBuilt = 1,
                oreCost = oreCostOre.toInt()
            ),
            clayRobotBlueprint = Blueprint.RobotBlueprint(
                clayRobotsBuilt = 1,
                oreCost = clayCostOre.toInt()
            ),
            obsidianRobotBlueprint = Blueprint.RobotBlueprint(
                obsidianRobotsBuilt = 1,
                oreCost = obsidianCostOre.toInt(),
                clayCost = obsidianCostClay.toInt()
            ),
            geodeRobotBlueprint = Blueprint.RobotBlueprint(
                geodeRobotsBuilt = 1,
                oreCost = geodeCostOre.toInt(),
                obsidianCost = geodeCostObsidian.toInt()
            )
        )
    }
}
