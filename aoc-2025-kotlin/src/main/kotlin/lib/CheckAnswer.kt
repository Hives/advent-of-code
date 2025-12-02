package lib

private const val ANSI_RED = "\u001B[31m"
private const val ANSI_GREEN = "\u001B[32m"
private const val ANSI_YELLOW = "\u001B[33m"
private const val ANSI_RESET = "\u001B[0m"

fun <T> T.checkAnswer(expected: T) {
    when (expected) {
        null -> printUnknown("Got this answer:\n$this\nIs it right?!?")
        this -> printSuccess("Got the right answer ($expected)")
        else -> printError("THE ANSWER WAS WRONG!!! Got $this but expected $expected")
    }
}

private fun printError(message: String) {
    println("$ANSI_RED$message$ANSI_RESET")
}

private fun printSuccess(message: String) {
    println("$ANSI_GREEN$message$ANSI_RESET")
}

private fun printUnknown(message: String) {
    println("$ANSI_YELLOW$message$ANSI_RESET")
}
