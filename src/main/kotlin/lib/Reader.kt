package lib

class Reader(private val file: String) {
    fun listOfInts() = read().lines().map { it.toInt() }

    fun listOfPasswords() = read().lines().map { line ->
       line.split("-", ": ", " ").let {
           PasswordDetails(
               password = it[3],
               requiredChar = it[2].single(),
               pos1 = it[0].toInt(),
               pos2 = it[1].toInt()
           )
       }
    }

    private fun read() = javaClass.getResource("/$file").readText()
}