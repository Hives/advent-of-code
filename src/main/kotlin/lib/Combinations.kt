package lib

fun <T> List<T>.combinations(n: Int): List<List<T>> {
    fun addFromRemainderToSelection(
        partialCombinations: List<PartialCombination<T>>
    ): List<PartialCombination<T>> =
        partialCombinations.flatMap { (selections, remainder) ->
            remainder.mapIndexed { index, i ->
                PartialCombination(
                    selections = selections + i,
                    remainder = remainder.subList(index + 1, remainder.size)
                )
            }
        }

    tailrec fun go(
        partialCombinations: List<PartialCombination<T>>,
        iteration: Int
    ): List<PartialCombination<T>> =
        if (iteration == 0) partialCombinations
        else go(addFromRemainderToSelection(partialCombinations), iteration - 1)

    return go(partialsFrom(this), n).map { it.selections }
}

private data class PartialCombination<T> (val selections: List<T>, val remainder: List<T>)

private fun <T> partialsFrom(list: List<T>) = listOf(PartialCombination(emptyList(), list))
