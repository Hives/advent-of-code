import lib.Reader
import lib.Toboggon

fun main() {
    val terrain = Reader("day03.txt").strings()
    val path = Toboggon(terrain).run()
    println(path.filter { it == '#' }.count())
}
