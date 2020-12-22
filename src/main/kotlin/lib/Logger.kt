package lib

const val logEnabled = false

fun log(text: String = "") {
   if (logEnabled) println(text)
}