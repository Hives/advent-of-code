package days.day21

fun countSafeIngredients(input: List<String>): Int {
    val allergenInfo = parseInput(input)

    val allergenPossibilityMap = getAllergenPossibilities(allergenInfo)
    val possiblyAllergenicIngredients = allergenPossibilityMap.values.flatten().toSet()

    val safeIngredients = extractIngredients(allergenInfo) - possiblyAllergenicIngredients

    val ingredientsListsCombined = allergenInfo.flatMap { (ingredients, _) -> ingredients }

    return safeIngredients.map { ingredient -> ingredientsListsCombined.count { it == ingredient } }.sum()
}

fun part2(input: List<String>): String =
    findAllergenicIngredients(input).toSortedMap().values.joinToString(",")

fun findAllergenicIngredients(input: List<String>): Map<String, String> {
    val allergenInfo = parseInput(input)
    val allergenPossibilityMap = getAllergenPossibilities(allergenInfo)
    return findAllergenicIngredientsUniquely(allergenPossibilityMap)
}

fun findAllergenicIngredientsUniquely(possibilityMap: Map<String, List<String>>): Map<String, String> {
    tailrec fun go(possibilityMap2: Map<String, List<String>>): Map<String, List<String>> =
        if (possibilityMap2.values.maxOf { it.size } == 1) possibilityMap2
        else {
            val uniques = possibilityMap2.values.filter { it.size == 1 }.flatten()
            go(
                possibilityMap2.map { (ingredient, allergens) ->
                    if (allergens.size == 1) ingredient to allergens
                    else ingredient to allergens - uniques
                }.toMap()
            )
        }

    return go(possibilityMap)
        .map { (ingredient, allergens) -> ingredient to allergens.single() }
        .toMap()
}

fun parseInput(input: List<String>): List<Pair<List<String>, List<String>>> =
    input.map {
        it.split(" (contains ")
            .let { (first, second) ->
                Pair(
                    first.split(" "),
                    second.dropLast(1).split(", ")
                )
            }
    }

fun getAllergenPossibilities(allergenInfo: List<Pair<List<String>, List<String>>>): Map<String, List<String>> =
    extractAllergens(allergenInfo).map { allergen ->
        allergenInfo
            .filter { (_, allergens) -> allergens.contains(allergen) }
            .map { (ingredients, _) -> ingredients }
            .reduce { acc, ingredients -> acc.intersect(ingredients).toList() }
            .let { possibilities -> allergen to possibilities }
    }.toMap()

fun extractAllergens(allergenInfo: List<Pair<List<String>, List<String>>>): List<String> =
    allergenInfo.flatMap { (_, allergens) -> allergens }.toSet().toList()

fun extractIngredients(allergenInfo: List<Pair<List<String>, List<String>>>): List<String> =
    allergenInfo.flatMap { (ingredients, _) -> ingredients }.toSet().toList()

