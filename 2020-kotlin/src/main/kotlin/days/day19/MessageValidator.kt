package days.day19

fun parseInput(input: String): Pair<Map<String, String>, List<String>> {
    val (rules, messages) = input.trim().split("\n\n")

    val ruleMap = rules.split("\n")
        .map { it.split(": ") }
        .map { (id, rule) -> id to rule }
        .toMap()

    return Pair(ruleMap, messages.split("\n"))
}

// recursive rule substitutions:
// rule 8 becomes: 42 | 42 8
// - matches one more more applications of rule 42
// rule 11 becomes: 42 31 | 42 11 41
// - matches rule 42 n times, then rule 32 n times, for some n > 0
//
// and rule 0: 8 11
// hence:
fun testMessageInPart2(message: String, rule42: String, rule31: String, maxN: Int) =
    (1..maxN).any { i ->
        val rule0 = """($rule42)+($rule42){$i}($rule31){$i}""".toRegex()
        rule0.matches(message)
    }

fun expandRule(ruleMap: Map<String, String>, ruleId: String): Regex {
    var rule = ruleMap[ruleId] ?: ""
    do {
        rule = rule.expandRuleOneLevel(ruleMap)
    } while (rule.containsNumber())

    return rule.replace(" ", "").replace("\"", "").toRegex()
}

fun String.expandRuleOneLevel(ruleMap: Map<String, String>): String =
    this
        .split(" ").joinToString(" ") {
            if (it.isNumber()) {
                val replacementRule = ruleMap[it] ?: ""
                if (replacementRule.contains("|")) "( $replacementRule )"
                else replacementRule
            } else it
        }

private val numberRegex = """\d+""".toRegex()
private fun String.containsNumber() = numberRegex.containsMatchIn(this)
private fun String.isNumber() = numberRegex.matches(this)
