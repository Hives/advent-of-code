package days.day23

import lib.Reader
import lib.Vector
import java.util.PriorityQueue

/*
    This code doesn't work. I ended up solving it manually by making a React app to let you try
    out different sequences of moves interactively, and fiddling around till I found the best
    solution.

    See: https://github.com/Hives/advent-of-code-2021-day-23/
*/

fun main() {
    val input = Reader("day23.txt").strings()
    val exampleInput = Reader("day23-example.txt").strings()

    part1(exampleInput).also { println(it) }
}

fun part1(input: List<String>): Int {
    val initialState = State(parse(input))

    var count = 0;

    val queue = PriorityQueue<State> { a, b -> a.cost - b.cost }
    queue.add(initialState)

    val seen = mutableSetOf<State>()

    var bestCompletion = Int.MAX_VALUE
//    var bestCompletion = 20_000

    while (queue.isNotEmpty()) {

        if (count % 100_000 == 0) {
            println("----")
            println("in queue: ${queue.size}")
            println("count: $count")
            println("best score: ${bestCompletion}")
            println("seen: ${seen.size}")
        }
        count++

        lateinit var state: State
        do {
            state = queue.remove()
        } while (state in seen)
        seen.add(state)

        val unseenNextMoves = state.findNextMoves().filterNot { it in seen }

        val (complete, incomplete) = unseenNextMoves.partition { it.isComplete() }

        complete.forEach { state ->
            if (state.cost < bestCompletion) bestCompletion = state.cost
        }

        incomplete.forEach { state ->
            if (state.cost < bestCompletion) queue.add(state)
        }
    }


    return bestCompletion
}

data class State(val amphipods: Set<Amphipod>) {
    fun isComplete() = amphipods.all { amphipod ->
        amphipod.position is Position.Room && amphipod.position.type == amphipod.type
    }

    fun findNextMoves() =
        amphipods.flatMap { amphipod ->
            val others = amphipods - amphipod
            val newPositions = amphipod.findMoves(others)
            newPositions.map { position -> others + position }
        }.map(::State)

    val cost: Int
        get() {
            if (costMemo == null) {
                costMemo = amphipods.fold(1) { acc, amphipod -> acc + amphipod.energy }
            }
            return costMemo!!
        }

    private var costMemo: Int? = null
}

fun Amphipod.findMoves(others: Set<Amphipod>): Set<Amphipod> {
    val occupiedPoints = others.map { it.position.point }

    // can't move if it's already moved and is back in a room
    if (this.moved && this.position is Position.Room) return emptySet()

    when (position) {
        is Position.Hallway -> {
            // must go home
            val homeRoomPositions = Position.Room.homePositionsFor(type)

            // can't go home if it contains amphipods of wrong type
            val amphipodsInHomeRoomPositions = others.filter { it.position in homeRoomPositions }
            if (amphipodsInHomeRoomPositions.count { it.type != type } > 0) return emptySet()

            // can't go home if pathway is blocked
            val porchXLocation = homeRoomPositions[0].point.x
            val xRange =
                if (porchXLocation < position.point.x) porchXLocation until position.point.x
                else (position.point.x + 1)..porchXLocation
            val pathwayPoints = xRange.map { Vector(it, position.point.y) }
            if (pathwayPoints.any { it in occupiedPoints }) return emptySet()

            // it goes home
            val destination =
                if (homeRoomPositions[0].point in occupiedPoints) homeRoomPositions[1] else homeRoomPositions[0]
            val moveSteps = pathwayPoints.size + destination.point.y

            return setOf(this.copy(position = destination, steps = steps + moveSteps))
        }
        is Position.Room -> {
            // can't move if stuck behind someone else
            if (position.point.y == 2 && (position.point.copy(y = 1) in occupiedPoints)) return emptySet()

            // must move to hallway
            val vacantHallwayPositions = Position.Hallway.positions.filterNot { it.point in occupiedPoints }

            return vacantHallwayPositions.mapNotNull { vacantHallwayPosition ->
                val xLocation = vacantHallwayPosition.point.x
                val xRange =
                    if (xLocation < position.point.x) xLocation..position.point.x
                    else position.point.x..xLocation
                val pathwayPoints = xRange.map { pathwayPointX -> Vector(pathwayPointX, 0) }

                // can't go to a point if pathway is blocked
                if (pathwayPoints.any { pathwayPoint -> pathwayPoint in occupiedPoints }) null
                else {
                    // it goes to the place
                    val moveSteps = pathwayPoints.size + this.position.point.y - 1
                    this.copy(position = vacantHallwayPosition, steps = steps + moveSteps, moved = true)
                }
            }.toSet()
        }
    }
}


enum class AmphipodType(val energyPerStep: Int) {
    A(1), B(10), C(100), D(1000)
}

sealed class Position(val point: Vector) {
    sealed class Hallway(point: Vector) : Position(point) {
        object Hallway1 : Hallway(Vector(0, 0))
        object Hallway2 : Hallway(Vector(1, 0))
        object Hallway4 : Hallway(Vector(3, 0))
        object Hallway6 : Hallway(Vector(5, 0))
        object Hallway8 : Hallway(Vector(7, 0))
        object Hallway10 : Hallway(Vector(9, 0))
        object Hallway11 : Hallway(Vector(10, 0))

        companion object {
            val positions: List<Hallway>
                get() = listOf(Hallway1, Hallway2, Hallway4, Hallway6, Hallway8, Hallway10, Hallway11)
        }
    }

    sealed class Room(point: Vector, val type: AmphipodType) : Position(point) {
        object RoomA1 : Room(Vector(2, 1), AmphipodType.A)
        object RoomA2 : Room(Vector(2, 2), AmphipodType.A)
        object RoomB1 : Room(Vector(4, 1), AmphipodType.B)
        object RoomB2 : Room(Vector(4, 2), AmphipodType.B)
        object RoomC1 : Room(Vector(6, 1), AmphipodType.C)
        object RoomC2 : Room(Vector(6, 2), AmphipodType.C)
        object RoomD1 : Room(Vector(8, 1), AmphipodType.D)
        object RoomD2 : Room(Vector(8, 2), AmphipodType.D)

        companion object {
            fun homePositionsFor(type: AmphipodType): List<Room> =
                when (type) {
                    // in priority order
                    AmphipodType.A -> listOf(RoomA2, RoomA1)
                    AmphipodType.B -> listOf(RoomB2, RoomB1)
                    AmphipodType.C -> listOf(RoomC2, RoomC1)
                    AmphipodType.D -> listOf(RoomD2, RoomD1)
                }
        }
    }
}

data class Amphipod(
    val id: Int,
    val position: Position,
    val type: AmphipodType,
    val steps: Int = 0,
    val moved: Boolean = false,
) {
    val energy: Int
        get() = steps * type.energyPerStep
}

fun parse(input: List<String>): Set<Amphipod> {
    var count = 0;
    return (input[2].replace("#", "").let {
        listOf(
            Amphipod(count++, Position.Room.RoomA1, AmphipodType.valueOf(it[0].toString())),
            Amphipod(count++, Position.Room.RoomB1, AmphipodType.valueOf(it[1].toString())),
            Amphipod(count++, Position.Room.RoomC1, AmphipodType.valueOf(it[2].toString())),
            Amphipod(count++, Position.Room.RoomD1, AmphipodType.valueOf(it[3].toString()))
        )
    } +
            input[3].replace("#", "").replace(" ", "").let {
                listOf(
                    Amphipod(count++, Position.Room.RoomA2, AmphipodType.valueOf(it[0].toString())),
                    Amphipod(count++, Position.Room.RoomB2, AmphipodType.valueOf(it[1].toString())),
                    Amphipod(count++, Position.Room.RoomC2, AmphipodType.valueOf(it[2].toString())),
                    Amphipod(count++, Position.Room.RoomD2, AmphipodType.valueOf(it[3].toString()))
                )
            }).toSet()
}