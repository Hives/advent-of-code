(ns aoc-2016-clojure.day09
  (:require [clojure.string :as str]
            [clojure.java.io :as io]))

(def puzzle-input (slurp (io/resource "day09.txt")))

(defn parse-marker [marker]
  (let [[_ characters repeats] (re-find #"(\d+)x(\d+)" marker)]
    [(Integer/parseInt characters) (Integer/parseInt repeats)]))

(defn decompress [input]
  (loop [index  0
         string input]
    (let [next-open-bracket (str/index-of string "(" index)]
      (if (= nil next-open-bracket)
        string
        (let [next-close-bracket (str/index-of string ")" index)
              next-marker        (subs string (+ 1 next-open-bracket) next-close-bracket)
              [characters repeats] (parse-marker next-marker)
              new-section-source (subs string (+ 1 next-close-bracket) (+ 1 characters next-close-bracket))
              new-section        (apply str (repeat repeats new-section-source))
              prefix             (subs string 0 next-open-bracket)
              suffix             (subs string (+ 1 characters next-close-bracket))
              new-string         (str prefix new-section suffix)
              new-index          (+ next-open-bracket (* characters repeats))
              ]
          (recur new-index new-string))))))

(defn part-1 [input]
  (count (decompress input)))

(part-1 puzzle-input)

; ----------

(defn get-expanded-length [string]
  (let [next-open-bracket (str/index-of string "(")]
    (if (= nil next-open-bracket)
      (count string)
      (let [next-close-bracket (str/index-of string ")")
            next-marker        (subs string (+ 1 next-open-bracket) next-close-bracket)
            [characters repeats] (parse-marker next-marker)
            to-be-expanded     (subs string (+ 1 next-close-bracket) (+ 1 characters next-close-bracket))
            prefix             (subs string 0 next-open-bracket)
            suffix             (subs string (+ 1 characters next-close-bracket))
            ]
        (+
          (count prefix)
          (* repeats (get-expanded-length to-be-expanded))
          (get-expanded-length suffix))))))

(defn part-2 [input]
  (get-expanded-length input))

(part-2 puzzle-input)