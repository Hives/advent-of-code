package lib

fun String.indexesOf(s: String) = mapIndexedNotNull { index, _ ->
    index.takeIf {
        this.length >= index + s.length && substring(index, index + s.length) == s
    }
}

fun String.toLongs() = split(" ").map(String::toLong)
