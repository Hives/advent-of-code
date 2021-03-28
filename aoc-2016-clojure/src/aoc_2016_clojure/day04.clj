(ns aoc-2016-clojure.day04
  (:require [clojure.string :as str]
            [clojure.java.io :as io]))

(def puzzleInput (slurp (io/resource "day04.txt")))

(defn parse [input]
  (let [[_ encryptedName id checksum] (re-find #"([a-z\-]+)-(\d+)\[([a-z]+)\]" input)]
    [encryptedName (Integer/parseInt id) checksum]))

(defn generate-checksum [encryptedName]
  (let [letters (remove #(= "-" %) (str/split encryptedName #""))
        freqs   (into [] (frequencies letters))]
    (str/join
      (take 5
        (map first
          (sort-by #(- (second %))
            (sort-by first freqs)))))))

(defn validate [[encryptedName _ checksum]]
  (= (generate-checksum encryptedName) checksum))

(defn get-valid-rooms [input]
  (filter validate
    (map parse (str/split input #"\n"))))

(defn part1 [input]
  (reduce + (map second (get-valid-rooms input))))

(part1 puzzleInput)

(defn letter-to-number [letter]
  (- (int letter) 97))

(defn number-to-letter [letter]
  (char (+ letter 97)))

(defn shift-letter [character distance]
  (number-to-letter
    (mod
      (+ distance (letter-to-number character))
      26)))

(defn decrypt [name distance]
  (str/join (map
              (fn [character]
                (if (= character \-)
                  \space
                  (shift-letter character distance)))
              name)))

(defn part2 [input]
  (let [valid-rooms (get-valid-rooms input)]
    (filter
      (fn [[name _]] (str/includes? name "pole"))
      (map
        (fn [[name sectorID]] [(decrypt name sectorID) sectorID])
        valid-rooms))))

(part2 puzzleInput)