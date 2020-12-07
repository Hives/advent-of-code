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
            hairColor.matches(hexStringRegex),
            eyeColor.matches(eyeColourRegex),
            passportId.matches(nineDigitIntegerRegex)
        ).all { it }

    private fun String?.isInRange(low: Int, high: Int) = this?.toIntOrNull()?.let { it in low..high } ?: false

    private fun String?.isLengthInRange(unit: String, low: Int, high: Int) =
        this != null && this.endsWith(unit) && this.replace(unit, "").isInRange(low, high)

    private fun String?.matches(regex: Regex) =
        if (this == null) false
        else regex.matches(this)

    private val hexStringRegex = """^#[a-f|0-9]{6}$""".toRegex()
    private val eyeColourRegex = """^(amb|blu|brn|gry|grn|hzl|oth)$""".toRegex()
    private val nineDigitIntegerRegex = """^\d{9}$""".toRegex()

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

