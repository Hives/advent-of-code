package days.day19

import days.day19.Material.CLAY
import lib.Reader

fun main() {
    val input = Reader("day19.txt").strings()
    val exampleInput = Reader("day19-example.txt").strings()

    part1(exampleInput)
}

fun part1(input: List<String>) {
    val blueprints = input.map(::parse)
    val initialState = State(
        robots = Resources(ore = 1),
        resources = Resources(),
    )

    val totalMinutes = 24

    val a = getBestGeodes(initialState, blueprints[1], totalMinutes)
    println(a)
}

fun getBestGeodes(
    initialState: State,
    blueprint: Blueprint,
    totalMinutes: Int
): Int {
    val finalMoveses = go(listOf(listOf(null, null, CLAY)), initialState, blueprint, totalMinutes)
    val bestMoves = finalMoveses.maxBy { evaluate(it, initialState, blueprint).resources.geode }
    println(bestMoves)
    return evaluate(bestMoves, initialState, blueprint).resources.geode
}

tailrec fun go(
    moveses: List<List<Material?>>,
    initialState: State,
    blueprint: Blueprint,
    totalMinutes: Int
): List<List<Material?>> {
    println()
    println(moveses.first().size)
    println(moveses.size)

    return if (moveses.first().size == totalMinutes - 1) moveses.map { it + null }
    else {
        val newMoveses = moveses.flatMap { nextMoves(it, initialState, blueprint) }
        val bestNewMoveses = newMoveses
            .sortedBy { heuristic(it, initialState, blueprint, totalMinutes) }
            .takeLast(50_000)
        go(bestNewMoveses, initialState, blueprint, totalMinutes)
    }
}


fun heuristic(moves: List<Material?>, initialState: State, blueprint: Blueprint, totalMinutes: Int): Int {
    val remainingTime = totalMinutes - moves.size

    val finalState = evaluate(moves, initialState, blueprint)

    return finalState.let {
        val potentialOre = it.resources.ore + (it.robots.ore * remainingTime)
        val potentialClay =
            it.resources.clay + (it.robots.clay * remainingTime) + (potentialOre / blueprint.oreRobot.ore)
        val potentialObsidian =
            it.resources.obsidian + (it.robots.obsidian * remainingTime) + (potentialClay / blueprint.obsidianRobot.clay)
        val potentialGeodes =
            it.resources.geode + (it.robots.geode * remainingTime) + (potentialObsidian / blueprint.geodeRobot.obsidian)

        potentialGeodes
    }
}

fun nextMoves(moves: List<Material?>, initialState: State, blueprint: Blueprint): List<List<Material?>> {
    val finalState = evaluate(moves, initialState, blueprint)
    return finalState.possibleMoves(blueprint).map { moves + it }
}

//fun improve(moves: List<Material?>, blueprint: Blueprint): List<Material?>? {
//    val states = moves.scan(initialState) { acc, material ->
//        acc.calculateNext(material, blueprint)
//    }.dropLast(1).mapIndexed { index, state -> Pair(index, state) }
//
//    if (!states.all { it.second.resources.allValuesNonNegative() }) throw Exception("Found some -ve resources?! ${states.first { !it.second.resources.allValuesNonNegative() }}")
//
//    Material.values().sortedByDescending { it.sortOrder }.forEach { material ->
//        val index =
//            states.firstOrNull { (index, state) ->
//                state.resources.canBuild(blueprint.getByMaterial(material)) && moves[index] == null
//            }
//        if (index != null) {
//            return moves.replace(index.first, material).nullifyFrom(index.first + 1)
//        }
//    }
//
//    return null
//}
//
//fun <T> List<T>.replace(index: Int, value: T): List<T> {
//    val mutable = this.toMutableList()
//    mutable[index] = value
//    return mutable.toList()
//}
//
//fun <T> List<T?>.nullifyFrom(index: Int): List<T?> {
//    return subList(0, index) + List<T?>(size - index) { null }
//}

val evaluateCachedResults: MutableMap<Pair<List<Material?>, Blueprint>, State> = HashMap()
fun evaluate(moves: List<Material?>, initialState: State, blueprint: Blueprint): State {
    if (moves.isEmpty()) return initialState
    if (Pair(moves, blueprint) in evaluateCachedResults) evaluateCachedResults[Pair(moves, blueprint)]

    val previousMoves = moves.subList(0, moves.size - 1)
    val penultimateState = evaluateCachedResults.getOrPut(Pair(previousMoves, blueprint)) {
        evaluate(previousMoves, initialState, blueprint)
    }
    return penultimateState.calculateNext(moves.last(), blueprint)
}

fun printy(moves: List<Material?>, initialState: State, blueprint: Blueprint) {
    moves.foldIndexed(initialState) { index, acc, material ->
        val next = acc.calculateNext(material, blueprint)
        println()
        println("== Minute ${index + 1} ==")
        if (material != null) {
            println("Spend ${blueprint.getByMaterial(material)} ore to start building a ${material.name.lowercase()}-collecting robot.")
        }
        println(material)
        println(next)
        next
    }
}

data class State(val robots: Resources, val resources: Resources) {
    fun possibleMoves(blueprint: Blueprint): List<Material?> {
        val moves = mutableListOf<Material?>(null)
        Material.values().forEach {
            if (resources.canBuild(blueprint.getByMaterial(it))) moves.add(it)
        }
        return moves.toList()
    }

    fun calculateNext(move: Material?, blueprint: Blueprint): State {
        val newRobots = this.robots + when (move) {
            null -> Resources()
            Material.ORE -> Resources(ore = 1)
            CLAY -> Resources(clay = 1)
            Material.OBSIDIAN -> Resources(obsidian = 1)
            Material.GEODE -> Resources(geode = 1)
        }
        val newResources = this.resources + this.robots - when (move) {
            null -> Resources()
            Material.ORE -> blueprint.oreRobot
            CLAY -> blueprint.clayRobot
            Material.OBSIDIAN -> blueprint.obsidianRobot
            Material.GEODE -> blueprint.geodeRobot
        }
        return State(robots = newRobots, resources = newResources)
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

    fun allValuesNonNegative() = this.ore >= 0 && this.clay >= 0 && this.obsidian >= 0 && this.geode >= 0

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
    val id: Int,
    val oreRobot: Resources,
    val clayRobot: Resources,
    val obsidianRobot: Resources,
    val geodeRobot: Resources
) {
    fun getByMaterial(material: Material) =
        when (material) {
            Material.ORE -> oreRobot
            CLAY -> clayRobot
            Material.OBSIDIAN -> obsidianRobot
            Material.GEODE -> geodeRobot
        }
}

fun parse(input: String): Blueprint {
    val r =
        """Blueprint (\d+): Each ore robot costs (\d+) ore. Each clay robot costs (\d+) ore. Each obsidian robot costs (\d+) ore and (\d+) clay. Each geode robot costs (\d+) ore and (\d+) obsidian.""".toRegex()
    return r.find(input)!!.destructured.let { (id, oreCostOre, clayCostOre, obsidianCostOre, obsidianCostClay, geodeCostOre, geodeCostObsidian) ->
        Blueprint(
            id = id.toInt(),
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
