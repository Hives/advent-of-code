package days.day01

// 5C3:
//    5!
// ---------
// 3!*(5-3)!
//
// 5*4*3*2*1
// ---------
// 3*2*1*2*1
//
// 10

//  1: 1 2 3
//  2: 1 2 4
//  3: 1 2 5
//  4: 1 3 4
//  5: 1 3 5
//  6: 1 4 5
//  7: 2 3 4
//  8: 2 3 5
//  9: 2 4 5
// 10: 3 4 5

fun main() {
    val input = listOf('a', 'b', 'c', 'd', 'e')
    input.choose(2).forEach {
        println(it)
    }
    // outputs
    // ['a', 'b']
    // ['a', 'c']
    // ['a', 'd']
    // ['a', 'e']
    // ['b', 'c']
    // etc.
}

fun <T> List<T>.choose(n: Int) = Combination(this, n).asSequence()

class Combination<T>(private val list: List<T>, private val n: Int) : Iterator<List<T>> {
    init {
        require(n <= list.size) { "n can't be more than list.size" }
    }

    private val maximums = List(n) { it + list.size - n }
    private var pointer = n - 1
    private val indices = MutableList(n) { it }

    override fun hasNext() = pointer >= 0

    override fun next() = indices.toList().also {
        if (indices[pointer] < maximums[pointer]) {
            indices[pointer] ++
        } else {
            while (pointer >= 0 && indices[pointer] == maximums[pointer]) {
                pointer--
            }
            if (hasNext()) {
                var foo = indices[pointer] + 1
                indices[pointer] = foo
                while(pointer < n - 1) {
                    foo++
                    pointer++
                    indices[pointer] = foo
                }
            }
        }
    }.map { list[it] }
}