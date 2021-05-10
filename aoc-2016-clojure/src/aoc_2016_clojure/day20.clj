(ns aoc-2016-clojure.day20
  (:require [clojure.string :as str]
            [clojure.java.io :as io]))

(def puzzleInput (str/split (slurp (io/resource "day20.txt")) #"\n"))
(def max-ip 4294967295)

(defn parse-input [input]
  (->> input
       (map #(subvec (re-matches #"(\d*)-(\d*)" %) 1))
       (map (fn [[start end]] [(Long/parseLong start) (Long/parseLong end)]))))

(def ranges (parse-input puzzleInput))

;; requires ranges to be sorted by start first
(defn merge-range [ranges new-range]
  (let [[next-start next-end] new-range
        [last-start last-end] (last ranges)]
    (if (> next-start (inc last-end))
      (conj ranges new-range)
      (if (<= next-end last-end)
        ranges
        (conj (subvec ranges 0 (dec (count ranges))) [last-start next-end])))))

(defn merge-ranges [ranges]
  (->> ranges
       (sort)
       (reduce merge-range [[0 0]])))

(def blacklist (merge-ranges ranges))

(->> (conj blacklist [(inc max-ip) (inc max-ip)])
     (partition 2 1)
     (reduce
      (fn [acc [range-1 range-2]]
        (+ acc (- (dec (first range-2)) (last range-1))))
      0))
