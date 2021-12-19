package days.day19

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import lib.Vector3D

class Day19KtTest : StringSpec({

    "set comparison" {
        (setOf(1, 2) == setOf(2, 1)) shouldBe true
    }

    "equal vectors" {
        val v1 = Vector3D(1, 2, 3)
        val v2 = Vector3D(1, 2, 3)

        (v1 == v2) shouldBe true
    }

    "distances" {
        val scanner0 = parse("""--- scanner 0 ---
                   |-618,-824,-621
                   |-537,-823,-458
                   |-447,-329,318
                   |404,-588,-901
                   |544,-627,-890
                   |528,-643,409
                   |-661,-816,-575
                   |390,-675,-793
                   |423,-701,434
                   |-345,-311,381
                   |459,-707,401
                   |-485,-357,347""".trimMargin())[0]

        val scanner1 = parse("""--- scanner 1 ---
                   |686,422,578
                   |605,423,415
                   |515,917,-361
                   |-336,658,858
                   |-476,619,847
                   |-460,603,-452
                   |729,430,532
                   |-322,571,750
                   |-355,545,-477
                   |413,935,-424
                   |-391,539,-444
                   |553,889,-390""".trimMargin())[0]

        val relativeDistances0 = scanner0.getRelativeDistances(Orientation.A).values

        println(relativeDistances0)

        Orientation.values().forEach { println(scanner1.getRelativeDistances(it).values) }
    }

})
