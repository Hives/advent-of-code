package days.day20

import days.day16.flip

fun getAllOrientations(sea: List<String>): List<List<String>> =
    sea.map { row -> row.toCharArray().toList() }.let {
        listOf(
            it,
            it.rotate(1),
            it.rotate(2),
            it.rotate(3),
            it.flip(),
            it.flip().rotate(1),
            it.flip().rotate(2),
            it.flip().rotate(3),
        )
    }.map { orientedSea ->
        orientedSea.map { row -> row.joinToString("") }
    }

fun List<String>.countSeaMonsters(): Int =
    this.indices.flatMap { y ->
        this[y].indices.map { x ->
            V(x, y)
        }
    }.count { it.isSeaMonsterLocation(this) }

fun V.isSeaMonsterLocation(sea: List<String>): Boolean =
    seaMonsterCoordinates
        .map { it + this }
        .map { sea.getV(it) }
        .all { it == '#' }

fun List<String>.getV(v: V): Char =
    this.getOrNull(v.y)?.getOrNull(v.x) ?: '!'

/*
                      1 1 1 1 1 1 1 1 1 1
  0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9
0                                     #
1 #         # #         # #         # # #
2   #     #     #     #     #     #
 */

val seaMonsterCoordinates = listOf(
    V(18, 0),
    V(0, 1),
    V(5, 1),
    V(6, 1),
    V(11, 1),
    V(12, 1),
    V(17, 1),
    V(18, 1),
    V(19, 1),
    V(1, 2),
    V(4, 2),
    V(7, 2),
    V(10, 2),
    V(13, 2),
    V(16, 2),
)

data class V(val x: Int, val y: Int) {
    operator fun plus(vector: V) = V(
        x = this.x + vector.x,
        y = this.y + vector.y
    )
}
