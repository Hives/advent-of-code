package days.day07

typealias BagMap = Map<String, List<Pair<String, Int>>>

fun String.parseBagRules(): BagMap =
    this.lines().map { it.parseBagRule() }
        .reduce { acc, map -> (acc.toList() + map.toList()).toMap() }

private fun String.parseBagRule(): BagMap =
    this.split("contain").let {
        mapOf(
            it[0].replace("bags", "").trim() to it[1].split(",")
                .let {
                    if (it.singleOrNull()?.contains("no other bags") == true) emptyList()
                    else
                        it.map {
                            it.replace("bags", "").replace("bag", "").replace(".", "").trim()
                                .split(" ")
                                .let {
                                    Pair(
                                        it.subList(1, it.size).joinToString(" "),
                                        it[0].toInt()
                                    )
                                }
                        }
                }
        )
    }

fun String.countContents(bagMap: BagMap): Int {
    val children = bagMap[this]
    val childrenCount = children?.map { (bag, multiple) -> multiple }?.sum() ?: 0
    val grandchildrenCount =
        children?.map { (bag, multiple) -> multiple * bag.countContents(bagMap) }?.sum() ?: 0
    return childrenCount + grandchildrenCount
}

fun String.getAllContainers(map: BagMap): Collection<String> {
    val immediateContainers = this.getImmediateContainers(map)
    return (immediateContainers + immediateContainers.flatMap { it.getAllContainers(map) }).toSet()
}

fun String.getImmediateContainers(map: BagMap): List<String> = map.filter { (container, contents) ->
    contents.map { (bag, number) -> bag }.contains(this)
}.map { (container, contents) -> container }

