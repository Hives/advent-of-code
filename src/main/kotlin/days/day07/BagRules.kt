package days.day07

typealias BagMap = Map<String, List<Pair<String, Int>>>

fun String.parseBagRules(): BagMap =
    this.lines().map { it.parseBagRule() }.toMap()

private fun String.parseBagRule(): Pair<String, List<Pair<String, Int>>> {
    val (bag, contentsDescription) = this.split(" bags contain ")

    return if (contentsDescription.contains("no other bags")) {
        bag to emptyList()
    } else {
        val contents = contentsDescription
            .split(",")
            .map { """(\d+) (.+) bags?""".toRegex().find(it)!!.destructured }
            .map { (howMany, innerBag) -> Pair(innerBag, howMany.toInt()) }

        bag to contents
    }
}

fun String.countContents(bagMap: BagMap): Int {
    val immediateChildren = bagMap[this]

    val immediateChildrenCount =
        immediateChildren?.map { (_, howMany) -> howMany }?.sum()
            ?: throw UnknownError("Tried to find a bag that wasn't in the rules!")

    val grandchildrenEtcCount =
        immediateChildren.map { (bag, howMany) -> howMany * bag.countContents(bagMap) }.sum()

    return immediateChildrenCount + grandchildrenEtcCount
}

fun String.getAllContainers(map: BagMap): Collection<String> {
    val immediateContainers = this.getImmediateContainers(map)
    return (immediateContainers + immediateContainers.flatMap { it.getAllContainers(map) }).toSet()
}

fun String.getImmediateContainers(map: BagMap): List<String> = map.filter { (container, contents) ->
    contents.map { (bag, number) -> bag }.contains(this)
}.map { (container, contents) -> container }

