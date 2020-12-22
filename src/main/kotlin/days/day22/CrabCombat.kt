package days.day22

tailrec fun play(handA: List<Int>, handB: List<Int>): List<Int> {
    if (handA.isEmpty()) return handB
    if (handB.isEmpty()) return handA

    val (newHandA, newHandB) = playOneRound(handA, handB)

    return play(newHandA, newHandB)
}

fun playOneRound(handA: List<Int>, handB: List<Int>): Pair<List<Int>, List<Int>> {
    val (a, handA2) = handA.takeQ()
    val (b, handB2) = handB.takeQ()

    return if (a > b) {
        Pair(
            handA2.addQ(a).addQ(b),
            handB2
        )
    } else {
        Pair(
            handA2,
            handB2.addQ(b).addQ(a),
        )
    }
}

fun <T> List<T>.takeQ(): Pair<T, List<T>> {
    require(this.isNotEmpty()) { "Can't take from empty list." }
    return Pair(this.first(), this.drop(1))
}

fun <T> List<T>.addQ(new: T): List<T> = this + new

fun Iterable<Int>.score() =
    this.toList().reversed().mapIndexed { index, i -> (index + 1) * i }.reduce { a, b -> a + b }

