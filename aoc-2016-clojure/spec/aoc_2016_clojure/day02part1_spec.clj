(ns aoc-2016-clojure.day02part1_spec
  (:require [speclj.core :refer :all]
            [aoc-2016-clojure.day02part1 :refer :all]))

(describe "move-one-space"
          (it "can go up"
              (should= [1 1] (move-one-space [1 2] \U)))
          (it "should stop going up at top row"
              (should= [2 0] (move-one-space [2 0] \U)))
          (it "can go right"
              (should= [2 1] (move-one-space [1 1] \R)))
          (it "should stop going right at right column"
              (should= [2 0] (move-one-space [2 0] \R)))
          (it "can go down"
              (should= [1 2] (move-one-space [1 1] \D)))
          (it "should stop going down at bottom column"
              (should= [1 2] (move-one-space [1 2] \D)))
          (it "can go left"
              (should= [0 1] (move-one-space [1 1] \L)))
          (it "should stop going down at left column"
              (should= [0 1] (move-one-space [0 1] \L))))

(describe "process-instruction"
          (it "can process a string of directions"
              (should= [0 0] (process-instruction [1 1] "ULL")))
          (it "can process another string of directions"
              (should= [1 1] (process-instruction [1 2] "UUUUD"))))

(describe "process-instructions"
          (let [instructions "ULL\nRRDDD\nLURDL\nUUUUD"]
            (it "can process a sequence of instructions"
                (should=
                  [[0 0] [2 2] [1 2] [1 1]]
                  (process-instructions [1 1] instructions)))))

(describe "map-positions"
          (it "maps 1" (should= 1 (map-position [0 0])))
          (it "maps 2" (should= 2 (map-position [1 0])))
          (it "maps 3" (should= 3 (map-position [2 0])))
          (it "maps 4" (should= 4 (map-position [0 1])))
          (it "maps 5" (should= 5 (map-position [1 1])))
          (it "maps 6" (should= 6 (map-position [2 1])))
          (it "maps 7" (should= 7 (map-position [0 2])))
          (it "maps 8" (should= 8 (map-position [1 2])))
          (it "maps 9" (should= 9 (map-position [2 2]))))
