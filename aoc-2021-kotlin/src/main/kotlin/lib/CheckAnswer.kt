package lib

private const val ANSI_RED = "\u001B[31m"
private const val ANSI_RESET = "\u001B[0m"

fun <T> T.checkAnswer(expected: T) {
    if (this != expected) {
        printError("THE ANSWER WAS WRONG!!! Got $this but expected $expected")
    }
}

private fun printError(message: String) {
    println("$ANSI_RED$message$ANSI_RESET")
}