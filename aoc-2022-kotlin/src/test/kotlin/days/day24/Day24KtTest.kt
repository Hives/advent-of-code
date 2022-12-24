package days.day24

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import java.util.PriorityQueue

enum class Num(val n: Int) { ONE(1), TWO(2), THREE(3), FOUR(4), FIVE(5) }

internal class Day24KtTest : StringSpec({
    "priority queue" {
        val p = PriorityQueue<Num>(compareBy({ -it.n }))
        p.add(Num.FIVE)
        p.add(Num.TWO)
        p.size shouldBe 2
        p.poll() shouldBe Num.FIVE
        p.size shouldBe 1
    }
})
