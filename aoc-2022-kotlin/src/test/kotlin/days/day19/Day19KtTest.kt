package days.day19

import days.day19.Material.CLAY
import days.day19.Material.GEODE
import days.day19.Material.OBSIDIAN
import days.day19_2.Resource
import days.day19_2.Solver
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import lib.Reader

internal class Day19KtTest : StringSpec({
    val initialState = State(
        robots = Resources(ore = 1),
        resources = Resources(),
    )

    val exampleInput = Reader("day19-example.txt").strings()
    val exampleBlueprints = exampleInput.map(::parse)

    "evaluating nothing" {
        val choices = List(24) { null }
        val final = evaluate(choices, initialState, exampleBlueprints[0])
        final.resources.ore shouldBe 24
    }

    "evaluating example 1" {
        val choices = listOf(
            null,
            null,
            CLAY,
            null,
            CLAY,
            null,
            CLAY,
            null,
            null,
            null,
            OBSIDIAN,
            CLAY,
            null,
            null,
            OBSIDIAN,
            null,
            null,
            GEODE,
            null,
            null,
            GEODE,
            null,
            null,
            null,
        )
        val final = evaluate(choices, initialState, exampleBlueprints[0])
        final.resources.geode shouldBe 9
    }

    "evaluating example 1 again" {
        val choices = listOf(
            null,
            null,
            Resource.CLAY,
            null,
            Resource.CLAY,
            null,
            Resource.CLAY,
            null,
            null,
            null,
            Resource.OBSIDIAN,
            Resource.CLAY,
            null,
            null,
            Resource.OBSIDIAN,
            null,
            null,
            Resource.GEODE,
            null,
            null,
            Resource.GEODE,
            null,
            null,
            null,
        )
        val blueprint = days.day19_2.parse(exampleInput[0])
        val solver = Solver(blueprint, 24)
        solver.evaluate(choices).geodes shouldBe 9
    }

//    "nullifying" {
//        listOf(0, 1, 2, 3, 4).nullifyFrom(2) shouldBe listOf(0, 1, null, null, null)
//    }

//    "wtf" {
//        val moves = listOf(null, null, CLAY, null, CLAY, null, CLAY, null, null, null, OBSIDIAN, null, null, null, null, OBSIDIAN, null, GEODE, null, OBSIDIAN, null, GEODE, null, GEODE, OBSIDIAN)
//        printy(moves, exampleBlueprints[0])
//    }

//    "given example" {
//        val moves = listOf(
//            null,
//            null,
//            CLAY,
//            null,
//            CLAY,
//            null,
//            CLAY,
//            null,
//            null,
//            null,
//            OBSIDIAN,
//            CLAY,
//            null,
//            null,
//            OBSIDIAN,
//            null,
//            null,
//            GEODE,
//            null,
//            null,
//            GEODE,
//            null,
//            null,
//            null
//        )
//        printy(moves, exampleBlueprints[0])
//    }
})
