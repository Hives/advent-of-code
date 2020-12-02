import lib.Reader
import lib.validatePassword

fun main() {
    val input = Reader("day02.txt").listOfPasswords()
    println(input.count { validatePassword(it) })
}
