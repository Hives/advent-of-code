package days.day23

class CrabCups(input: String, max: Int? = null) {
    private val cupsLinks: MutableList<Int>
    private var current: Int
    private val maxCup: Int

    init {
        val cupsList = input.split("").filterNot { it == "" }.map { it.toInt() }
            .let { inputList ->
                if (max == null) {
                    inputList
                } else {
                    inputList + ((inputList.size + 1)..max).toList()
                }
            }
        current = cupsList.first()
        maxCup = cupsList.size

        cupsLinks = MutableList(cupsList.size + 1) { -1 }
        cupsList.indices.forEach {
            cupsLinks[cupsList[it]] = cupsList[(it + 1) % cupsList.size]
        }
    }

    fun run(n: Int) {
        repeat(n) {
            crunch()
        }
    }

    fun crunch() {
        val nextThree = getNextThree()

        var destination = restrict(current - 1)
        while (destination in nextThree) {
            destination = restrict(destination - 1)
        }

        cupsLinks[current] = cupsLinks[nextThree.last()]
        cupsLinks[nextThree.last()] = cupsLinks[destination]
        cupsLinks[destination] = nextThree.first()

        current = cupsLinks[current]
    }

    fun getFirst(n: Int): List<Int> {
        val o = mutableListOf<Int>()
        var cup = 1
        repeat(n) {
            o.add(cup)
            cup = cupsLinks[cup]
        }
        return o.toList()
    }

    fun getCups(): List<Int> {
        val o = mutableListOf<Int>()
        var n = 1
        do {
            o.add(n)
            n = cupsLinks[o.last()]
        } while (n != 1)
        return o.toList()
    }

    private fun getNextThree(): List<Int> {
        val taken = mutableListOf<Int>()

        var next = current
        repeat(3) {
            next = cupsLinks[next]
            taken.add(next)
        }

        return taken.toList()
    }

    private fun restrict(n: Int) =
        when (n) {
            maxCup + 1 -> 1
            0 -> maxCup
            else -> n
        }

}
