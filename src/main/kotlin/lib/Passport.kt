package lib

data class Passport(
    val birthYear: String? = null,
    val issueYear: String? = null,
    val expirationYear: String? = null,
    val height: String? = null,
    val hairColor: String? = null,
    val eyeColor: String? = null,
    val passportId: String? = null,
    val countryId: String? = null
) {

    val isValid: Boolean
        get() = listOf(birthYear, issueYear, expirationYear, height, hairColor, eyeColor, passportId)
            .all { it != null }

    val isValid2: Boolean
        get() = listOf(
            birthYear?.toIntOrNull()?.let { it in 1920..2002 } ?: false,
            issueYear?.toIntOrNull()?.let { it in 2010..2020 } ?: false,
            expirationYear?.toIntOrNull()?.let { it in 2020..2030 } ?: false,
            when {
                height.isNullOrBlank() -> false
                else -> when {
                    height.endsWith("cm") ->
                        height.replace("cm", "").toIntOrNull().let { it in 150..193 }
                    height.endsWith("in") ->
                        height.replace("in", "").toIntOrNull().let { it in 59..76 }
                    else -> false
                }
            },
            when {
                hairColor.isNullOrBlank() -> false
                hairColor.length != 7 -> false
                !hairColor.startsWith("#") -> false
                else -> hairColor.drop(1).all { "1234567890abcdef".contains(it) }
            },
            eyeColor?.let { listOf("amb", "blu", "brn", "gry", "grn", "hzl", "oth").contains(it) } ?: false,
            passportId?.let { it.length == 9 && it.toIntOrNull() != null } ?: false
        ).all { it }

    companion object {
        fun from(input: String): Passport {
            val inputMap = input
                .split(" ")
                .map { it.split(":").let { attr -> attr[0] to attr[1] } }
                .toMap()

            return Passport(
                birthYear = inputMap["byr"],
                issueYear = inputMap["iyr"],
                expirationYear = inputMap["eyr"],
                height = inputMap["hgt"],
                hairColor = inputMap["hcl"],
                eyeColor = inputMap["ecl"],
                passportId = inputMap["pid"],
                countryId = inputMap["cid"]
            )
        }
    }
}

fun List<String>.mapToPassportData(): List<String> {
    val output = mutableListOf(emptyList<String>())
    forEach { line ->
        if (line.isEmpty()) output.add(emptyList())
        else output[output.size - 1] = (output[output.size - 1] + line)
    }
    return output.toList().map { it.joinToString(" ") }
}

