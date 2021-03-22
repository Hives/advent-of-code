(ns aoc-2016-clojure.day06
  (:require [clojure.string :as str]
            [clojure.java.io :as io]))

(def testInput (str/split "eedadn\ndrvtee\neandsr\nraavrd\natevrs\ntsrnev\nsdttsa\nrasrtv\nnssdts\nntnada\nsvetve\ntesnvt\nvntsnd\nvrdear\ndvrsen\nenarar" #"\n"))
(def puzzleInput (str/split (slurp (io/resource "day06.txt")) #"\n"))

(defn increase-letter-count [[letter counts]]
  (let [letter-key (keyword (str letter))]
    (update counts letter-key #(if (not %) 1 (inc %)))))

(defn increase-letters-counts [counts word]
  (let [zipped (map vector word counts)]
    (map increase-letter-count zipped)))

(defn count-letter-frequencies [words]
  (let [empty-counts (take (count (first words)) (repeat {}))]
    (reduce increase-letters-counts empty-counts words)))

(defn get-key-by-comparison [comparison input]
  (reduce
    (fn [acc next]
      (if (comparison (second acc) (second next))
        acc
        next))
    input))

(defn decoder [input ordering]
  (let [frequencies   (count-letter-frequencies input)
        most-frequent (map (partial get-key-by-comparison ordering) frequencies)]
    (str/join (map (comp name first) most-frequent))))

(defn part1 [input] (decoder input >))
(defn part2 [input] (decoder input <))

(part1 testInput)
(part2 testInput)
(part1 puzzleInput)
(part2 puzzleInput)
