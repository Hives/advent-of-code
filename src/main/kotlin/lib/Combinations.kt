package lib

fun List<Int>.combinations(n: Int): List<List<Int>> {
    fun addFromRemainderToSelection(
        partialCombinations: List<PartialCombination>
    ): List<PartialCombination> =
        partialCombinations.flatMap { (selections, remainder) ->
            remainder.mapIndexed { index, i ->
                PartialCombination(
                    selections = selections + i,
                    remainder = remainder.subList(index + 1, remainder.size)
                )
            }
        }

    tailrec fun go(
        partialCombinations: List<PartialCombination>,
        iteration: Int
    ): List<PartialCombination> =
        if (iteration == 0) partialCombinations
        else go(addFromRemainderToSelection(partialCombinations), iteration - 1)

    return go(partialsFrom(this), n).map { it.selections }
}

private data class PartialCombination(val selections: List<Int>, val remainder: List<Int>)

private fun partialsFrom(ints: List<Int>) = listOf(PartialCombination(emptyList(), ints))
