(ns aoc-2016-clojure.day03
  (:require [clojure.java.io :as io]
            [clojure.string :as string]))

(def input (slurp (io/resource "day03.txt")))

(defn validate-triangle [[a b c]]
  (let [[short middle long] (sort [a b c])]
    (> (+ short middle) long)))

(defn parse-triple [input]
  (map #(Integer/parseInt %) (re-seq #"\d+" input)))

(defn rows-to-triples [input]
  (let [lines (string/split input #"\n")]
    (map parse-triple lines)))

(defn flip [two-d-array]
  (apply map list two-d-array))

(defn part1 []
  (let [triples (rows-to-triples input)]
    (count (filter validate-triangle triples))))

(part1)

(defn part2 []
  (let [triples (rows-to-triples input)
        triples-by-columns (reduce concat (map flip (partition 3 triples)))]
    (count (filter validate-triangle  triples-by-columns))))

(part2)
