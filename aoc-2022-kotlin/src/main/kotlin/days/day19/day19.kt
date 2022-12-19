package days.day19

import lib.Reader
import kotlin.system.exitProcess

fun main() {
    val input = Reader("day19.txt").strings()
    val exampleInput = Reader("day19-example.txt").strings()

    part1(exampleInput)
}

fun part1(input: List<String>) {
    val blueprints = input.map(::parse)

    val totalMinutes = 24

    val initial = List(totalMinutes) { null as Material? }
//    println(initial)
//
//    val improved = improve(initial, blueprints[0])
//    println(improved)
//
//    val improved2 = improve(improved!!, blueprints[0])
//    println(improved2)
//
//    val improved3 = improve(improved2!!, blueprints[0])
//    println(improved3)
//
    var count = 0
    tailrec fun go(moves: List<Material?>): List<Material?> {
        count++
        if (count == 100) exitProcess(0)
        println()
        println(moves)
        println(evaluate(moves, blueprints[0]))
        val improved = improve(moves, blueprints[0])
        return if (improved == null) moves
        else go(improved)
    }

    val final = go(initial)

    println("hello")

    println(evaluate(final, blueprints[0]))
}

val initialState = State(
    robots = Resources(ore = 1),
    resources = Resources(),
)

fun improve(moves: List<Material?>, blueprint: Blueprint): List<Material?>? {
    val states = moves.scan(initialState) { acc, material ->
        acc.calculateNext(material, blueprint)
    }.dropLast(1).mapIndexed { index, state -> Pair(index, state) }

    if (!states.all { it.second.resources.allValuesNonNegative() }) throw Exception("Found some -ve resources?! ${states.first { !it.second.resources.allValuesNonNegative() }}")

    Material.values().sortedByDescending { it.sortOrder }.forEach { material ->
        val index =
            states.firstOrNull { (index, state) ->
                state.resources.canBuild(blueprint.getByMaterial(material)) && moves[index] == null
            }
        if (index != null) {
            return moves.replace(index.first, material).nullifyFrom(index.first + 1)
        }
    }

    return null
}

fun <T> List<T>.replace(index: Int, value: T): List<T> {
    val mutable = this.toMutableList()
    mutable[index] = value
    return mutable.toList()
}

fun <T> List<T?>.nullifyFrom(index: Int): List<T?> {
    return subList(0, index) + List<T?>(size - index) { null }
}

fun evaluate(moves: List<Material?>, blueprint: Blueprint): State {
    return moves.fold(initialState) { acc, move ->
        acc.calculateNext(move, blueprint)
    }
}

fun State.calculateNext(move: Material?, blueprint: Blueprint): State {
    val newRobots = this.robots + when (move) {
        null -> Resources()
        Material.ORE -> Resources(ore = 1)
        Material.CLAY -> Resources(clay = 1)
        Material.OBSIDIAN -> Resources(obsidian = 1)
        Material.GEODE -> Resources(geode = 1)
    }
    val newResources = this.resources + this.robots - when (move) {
        null -> Resources()
        Material.ORE -> blueprint.oreRobot
        Material.CLAY -> blueprint.clayRobot
        Material.OBSIDIAN -> blueprint.obsidianRobot
        Material.GEODE -> blueprint.geodeRobot
    }
    return State(robots = newRobots, resources = newResources)
}

fun printy(moves: List<Material?>, blueprint: Blueprint) {
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

data class State(val robots: Resources, val resources: Resources)

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
    val id: String,
    val oreRobot: Resources,
    val clayRobot: Resources,
    val obsidianRobot: Resources,
    val geodeRobot: Resources
) {
    fun getByMaterial(material: Material) =
        when (material) {
            Material.ORE -> oreRobot
            Material.CLAY -> clayRobot
            Material.OBSIDIAN -> obsidianRobot
            Material.GEODE -> geodeRobot
        }
}

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
