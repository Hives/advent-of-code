package lib

class Reader(private val file: String) {
    fun listOfInts() = read().lines().map { it.toInt() }

    fun listOfPasswords() = read().lines().map { line ->
       line.split("-", ": ", " ").let {
           PasswordDetails(
               password = it[3],
               requiredChar = it[2].single(),
               min = it[0].toInt(),
               max = it[1].toInt()
           )
       }
    }

    private fun read() = javaClass.getResource("/$file").readText()
}