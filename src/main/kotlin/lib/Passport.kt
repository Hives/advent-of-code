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
            birthYear.inRange(1920, 2002),
            issueYear.inRange(2010, 2020),
            expirationYear.inRange(2020, 2030),
            when {
                height.isNullOrBlank() -> false
                else -> when {
                    height.endsWith("cm") -> height.replace("cm", "").inRange(150, 193)
                    height.endsWith("in") -> height.replace("in", "").inRange(59, 76)
                    else -> false
                }
            },
            when {
                hairColor?.length != 7 -> false
                !hairColor.startsWith("#") -> false
                else -> hairColor.drop(1).all { "1234567890abcdef".contains(it) }
            },
            eyeColor?.let { listOf("amb", "blu", "brn", "gry", "grn", "hzl", "oth").contains(it) } ?: false,
            passportId?.let { it.length == 9 && it.toIntOrNull() != null } ?: false
        ).all { it }

    private fun String?.inRange(low: Int, high: Int): Boolean = this?.toIntOrNull()?.let { it in low..high } ?: false

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

fun String.mapToPassportData(): List<String> =
    this.split("\n\n").map { it.replace("\n", " ") }

