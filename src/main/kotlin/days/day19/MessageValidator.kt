package days.day19

fun parseInput(input: String): Pair<Map<Int, String>, List<String>> {
    val (start, end) = input.trim().split("\n\n")
    val ruleRegex = """^(\d+): (.*)$""".toRegex()
    val ruleMap = start.split("\n")
        .map { ruleRegex.find(it)!!.destructured }
        .map { (id, rule) -> id.toInt() to rule }
        .toMap()

    return Pair(ruleMap, end.split("\n"))
}

fun testMessage(message: String, rule42: String, rule31: String): Boolean {
    return (1..20).map { i ->
        val rule = """($rule42)+($rule42){$i}($rule31){$i}""".toRegex()
        val result = rule.matches(message)
//        println("$i: $result")
        result
    }.any { it }
}

fun combineRules(rule42: String, rule31: String): Regex =
    """($rule42)($rule42)+($rule31)+""".toRegex()

fun condenseRule(ruleMap: Map<Int, String>, ruleId: Int): Regex {
    var rule = ruleMap[ruleId]!!
    do {
        rule = rule.substituteRules(ruleMap)
    } while ("""\d""".toRegex().containsMatchIn(rule))

    return rule.replace(" ", "").replace("\"", "").toRegex()
}

fun String.substituteRules(ruleMap: Map<Int, String>): String {
    return this
        .split(" ").joinToString(" ") {
            try {
                val rule = ruleMap[it.toInt()]
                if (rule!!.contains("|")) "( $rule )"
                else rule
            } catch (e: Exception) {
                it
            }
        }
}

