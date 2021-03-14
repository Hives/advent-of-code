(ns aoc-2016-clojure.day02_spec
  (:require [speclj.core :refer :all]
            [aoc-2016-clojure.day02 :refer :all]))

(describe "Foobar"
          (it "should add numbers"
              (should= (foobar 1 2) 3)))