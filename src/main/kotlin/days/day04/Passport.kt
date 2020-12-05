package days.day04

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
                birthYear.isInRange(1920, 2002),
                issueYear.isInRange(2010, 2020),
                expirationYear.isInRange(2020, 2030),
                height.isLengthInRange("cm", 150, 193) || height.isLengthInRange("in", 59, 76),
                hairColor.isHexString(),
                eyeColor.isOneOf("amb", "blu", "brn", "gry", "grn", "hzl", "oth"),
                passportId.is9DigitInt()
        ).all { it }

    private fun String?.isInRange(low: Int, high: Int) = this?.toIntOrNull()?.let { it in low..high } ?: false

    private fun String?.isLengthInRange(unit: String, low: Int, high: Int) =
            this != null && this.endsWith(unit) && this.replace(unit, "").isInRange(low, high)

    private fun String?.isHexString() =
            this?.length == 7 && this.startsWith("#") && this.drop(1).all { "1234567890abcdef".contains(it) }

    private fun String?.isOneOf(vararg values: String) = this?.let { values.contains(it) } ?: false

    private fun String?.is9DigitInt() = this?.length == 9 && this.toIntOrNull() != null

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

