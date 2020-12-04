package lib

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isFalse
import assertk.assertions.isTrue
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

internal class PassportTest {
    @Test
    fun `can get a passport with all fields`() {
        val input = "ecl:gry pid:860033327 eyr:2020 hcl:#fffffd byr:1937 iyr:2017 cid:147 hgt:183cm"

        val passport = Passport.from(input)

        assertThat(passport).isEqualTo(
            Passport(
                birthYear = "1937",
                issueYear = "2017",
                expirationYear = "2020",
                height = "183cm",
                hairColor = "#fffffd",
                eyeColor = "gry",
                passportId = "860033327",
                countryId = "147"
            )
        )
    }

    @Test
    fun `values are null if not provided`() {
        val passport = Passport.from("foo:bar")
        assertThat(passport).isEqualTo(
            Passport(
                birthYear = null,
                issueYear = null,
                expirationYear = null,
                height = null,
                hairColor = null,
                eyeColor = null,
                passportId = null,
                countryId = null
            )
        )
    }

    @Nested
    inner class ValidationPart1 {
        @Test
        fun `passport is valid if contains all properties`() {
            assertThat(allPropsPassport.isValid).isTrue()
        }

        @Test
        fun `passport is valid if country if is null`() {
            val passportWithNoCountry = allPropsPassport.copy(countryId = null)
            assertThat(passportWithNoCountry.isValid).isTrue()
        }

        @Test
        fun `passport is invalid if other properties are null`() {
            val invalidPassports = listOf(
                allPropsPassport.copy(birthYear = null),
                allPropsPassport.copy(issueYear = null),
                allPropsPassport.copy(expirationYear = null),
                allPropsPassport.copy(height = null),
                allPropsPassport.copy(hairColor = null),
                allPropsPassport.copy(eyeColor = null),
                allPropsPassport.copy(passportId = null)
            )
            invalidPassports.forEach { passport ->
                assertThat(passport.isValid).isFalse()
            }
        }
    }

    @Nested
    inner class ValidationPart2 {
        @Test
        fun `birth year between 1920 and 2002`() {
            val tooLow = allPropsPassport.copy(birthYear = "1919")
            val lowerBound = allPropsPassport.copy(birthYear = "1920")
            val upperBound = allPropsPassport.copy(birthYear = "2002")
            val tooHigh = allPropsPassport.copy(birthYear = "2003")
            val notANumber = allPropsPassport.copy(birthYear = "wtf")
            val notSet = allPropsPassport.copy(birthYear = null)

            assertThat(tooLow.isValid2).isFalse()
            assertThat(tooHigh.isValid2).isFalse()
            assertThat(notANumber.isValid2).isFalse()
            assertThat(notSet.isValid2).isFalse()

            assertThat(lowerBound.isValid2).isTrue()
            assertThat(upperBound.isValid2).isTrue()
        }

        @Test
        fun `issue year between 2010 and 2020`() {
            val tooLow = allPropsPassport.copy(issueYear = "2009")
            val lowerBound = allPropsPassport.copy(issueYear = "2010")
            val upperBound = allPropsPassport.copy(issueYear = "2020")
            val tooHigh = allPropsPassport.copy(issueYear = "2021")
            val notANumber = allPropsPassport.copy(issueYear = "wtf")
            val notSet = allPropsPassport.copy(issueYear = null)

            assertThat(tooLow.isValid2).isFalse()
            assertThat(tooHigh.isValid2).isFalse()
            assertThat(notANumber.isValid2).isFalse()
            assertThat(notSet.isValid2).isFalse()

            assertThat(lowerBound.isValid2).isTrue()
            assertThat(upperBound.isValid2).isTrue()
        }

        @Test
        fun `expiration year between 2020 and 2030`() {
            val tooLow = allPropsPassport.copy(expirationYear = "2019")
            val lowerBound = allPropsPassport.copy(expirationYear = "2020")
            val upperBound = allPropsPassport.copy(expirationYear = "2030")
            val tooHigh = allPropsPassport.copy(expirationYear = "2031")
            val notANumber = allPropsPassport.copy(expirationYear = "wtf")
            val notSet = allPropsPassport.copy(expirationYear = null)

            assertThat(tooLow.isValid2).isFalse()
            assertThat(tooHigh.isValid2).isFalse()
            assertThat(notANumber.isValid2).isFalse()
            assertThat(notSet.isValid2).isFalse()

            assertThat(lowerBound.isValid2).isTrue()
            assertThat(upperBound.isValid2).isTrue()
        }

        @Nested
        inner class Height {
            @Test
            fun `in cm, height between 150 & 193`() {
                val tooLow = allPropsPassport.copy(height = "149cm")
                val lowerBound = allPropsPassport.copy(height = "150cm")
                val upperBound = allPropsPassport.copy(height = "193cm")
                val tooHigh = allPropsPassport.copy(height = "194cm")
                val malformed = allPropsPassport.copy(height = "WTFcm")

                assertThat(tooLow.isValid2).isFalse()
                assertThat(tooHigh.isValid2).isFalse()
                assertThat(malformed.isValid2).isFalse()

                assertThat(lowerBound.isValid2).isTrue()
                assertThat(upperBound.isValid2).isTrue()
            }

            @Test
            fun `in inches, height between 59 & 76`() {
                val tooLow = allPropsPassport.copy(height = "58in")
                val lowerBound = allPropsPassport.copy(height = "59in")
                val upperBound = allPropsPassport.copy(height = "76in")
                val tooHigh = allPropsPassport.copy(height = "77in")
                val malformed = allPropsPassport.copy(height = "WTFin")

                assertThat(tooLow.isValid2).isFalse()
                assertThat(tooHigh.isValid2).isFalse()
                assertThat(malformed.isValid2).isFalse()

                assertThat(lowerBound.isValid2).isTrue()
                assertThat(upperBound.isValid2).isTrue()
            }

            @Test
            fun `fails if no units`() {
                val noUnits = allPropsPassport.copy(height = "190")
                assertThat(noUnits.isValid2).isFalse()
            }

            @Test
            fun `fails if not set`() {
                val notSet = allPropsPassport.copy(height = null)
                assertThat(notSet.isValid2).isFalse()
            }
        }

        @Test
        fun `hair colour`() {
            val noHashAtStart = allPropsPassport.copy(hairColor = "123abc")
            val invalidCharacter = allPropsPassport.copy(hairColor = "#123abz")
            val hashInMiddle = allPropsPassport.copy(hairColor = "#fff#ff")
            val tooLong = allPropsPassport.copy(hairColor = "#fffffff")
            val tooShort = allPropsPassport.copy(hairColor = "#fffff")
            val notSet = allPropsPassport.copy(hairColor = null)
            val valid = allPropsPassport.copy(hairColor = "#123abc")

            assertThat(noHashAtStart.isValid2).isFalse()
            assertThat(invalidCharacter.isValid2).isFalse()
            assertThat(hashInMiddle.isValid2).isFalse()
            assertThat(tooLong.isValid2).isFalse()
            assertThat(tooShort.isValid2).isFalse()
            assertThat(notSet.isValid2).isFalse()

            assertThat(valid.isValid2).isTrue()
        }

        @Test
        fun `eye color`() {
            val notSet = allPropsPassport.copy(eyeColor = null)
            val invalidEyeColor = allPropsPassport.copy(eyeColor = "wtf")

            assertThat(notSet.isValid2).isFalse()
            assertThat(invalidEyeColor.isValid2).isFalse()

            val validEyeColors = listOf("amb", "blu", "brn", "gry", "grn", "hzl", "oth")
            validEyeColors.forEach { validEyeColor ->
                val p = allPropsPassport.copy(eyeColor = validEyeColor)
                assertThat(p.isValid2).isTrue()
            }
        }

        @Test
        fun `passport id`() {
            val tooShort = allPropsPassport.copy(passportId = "12345678")
            val tooLong = allPropsPassport.copy(passportId = "1234567890")
            val notANumber = allPropsPassport.copy(passportId = "12345678x")
            val valid = allPropsPassport.copy(passportId = "123456789")
            val leadingZeros = allPropsPassport.copy(passportId = "000000001")

            assertThat(tooShort.isValid2).isFalse()
            assertThat(tooLong.isValid2).isFalse()
            assertThat(notANumber.isValid2).isFalse()

            assertThat(valid.isValid2).isTrue()
            assertThat(leadingZeros.isValid2).isTrue()
        }
    }

    @Nested
    inner class InputMapper {
        @Test
        fun `can map passport data spread over multiple lines`() {
           val input = "iyr:2013 ecl:amb cid:350 eyr:2023 pid:028048884\n" +
                   "hcl:#cfa07d byr:1929\n" +
                   "\n" +
                   "hcl:#ae17e1 iyr:2013\n" +
                   "eyr:2024\n" +
                   "ecl:brn pid:760753108 byr:1931\n" +
                   "hgt:179cm"

            val expectedOutput = listOf(
                "iyr:2013 ecl:amb cid:350 eyr:2023 pid:028048884 hcl:#cfa07d byr:1929",
                "hcl:#ae17e1 iyr:2013 eyr:2024 ecl:brn pid:760753108 byr:1931 hgt:179cm"
            )

            assertThat(input.mapToPassportData()).isEqualTo(expectedOutput)
        }
    }

    private val allPropsPassport = Passport(
        birthYear = "1937",
        issueYear = "2017",
        expirationYear = "2025",
        height = "183cm",
        hairColor = "#fffffd",
        eyeColor = "gry",
        passportId = "860033327",
        countryId = "147"
    )
}