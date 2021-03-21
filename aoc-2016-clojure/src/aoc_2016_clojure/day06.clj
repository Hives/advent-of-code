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

(defn count-letter-frequencies [input]
  (let [initial (take (count (first input)) (repeat {}))]
    (reduce increase-letters-counts initial input)))

(defn get-highest-value-key [input]
  (reduce
    (fn [acc next]
      (if (> (second acc) (second next))
        acc
        next))
    input))

(defn get-lowest-value-key [input]
  (reduce
    (fn [acc next]
      (if (< (second acc) (second next))
        acc
        next))
    input))

(defn part1 [input]
  (let [frequencies   (count-letter-frequencies input)
        most-frequent (map get-highest-value-key frequencies)]
    (str/join (map (comp name first) most-frequent))))

(defn part2 [input]
  (let [frequencies   (count-letter-frequencies input)
        least-frequent (map get-lowest-value-key frequencies)]
    (str/join (map (comp name first) least-frequent))))

(part1 testInput)
(part2 testInput)
(part1 puzzleInput)
(part2 puzzleInput)
