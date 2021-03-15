(ns aoc-2016-clojure.day03_spec
  (:require [speclj.core :refer :all]
            [aoc-2016-clojure.day03 :refer :all]))

(describe "validate-triangle"
          (it "answers true for a valid triangle"
              (should= true (validate-triangle [3 4 5])))
          (it "answers false for an invalid triangle"
              (should= false (validate-triangle [3 1 5])))
          (it "if the short sides equal the long one, that's invalid"
              (should= false (validate-triangle [3 2 5]))))

(describe "parse-triple"
          (it "parses a string with 3 numbers"
              (should= [1 2 3] (parse-triple "  1  2  3  "))))

(describe "flip"
          (it "flips a 2D array"
              (should=
                [[1 4 7] [2 5 8] [3 6 9]]
                (flip [[1 2 3] [4 5 6] [7 8 9]]))))