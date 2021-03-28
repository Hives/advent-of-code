(ns aoc-2016-clojure.day08
  (:require [clojure.string :as str]
            [clojure.java.io :as io]))

(def test-initial '("......." "......." "......."))
(def test-input (str/split "rect 3x2\nrotate column x=1 by 1\nrotate row y=0 by 4\nrotate column x=1 by 1" #"\n"))
(def fifty-dots "..................................................")
(def puzzle-initial [fifty-dots fifty-dots fifty-dots fifty-dots fifty-dots fifty-dots])
(def puzzle-input (str/split (slurp (io/resource "day08.txt")) #"\n"))

; how does this work? https://stackoverflow.com/questions/8314789/rotate-a-list-of-list-matrix-in-clojure
(defn transpose [matrix]
  (apply map list matrix))

(defn rotate [n xs]
  (if (= n 0)
    xs
    (rotate (- n 1) (concat (list (last xs)) (butlast xs))))) ; probably v. inefficient?

(defn rotate-row [row-index distance matrix]
  (map-indexed
    (fn [index row]
      (if (= index row-index)
        (rotate distance row)
        row))
    matrix))

(defn rotate-col [col-index distance matrix]
  (let [transposed (transpose matrix)
        rotated    (rotate-row col-index distance transposed)]
    (transpose rotated)))

(defn create-rect [width height matrix]
  (map-indexed
    (fn [row-index row]
      (if (< row-index height)
        (map-indexed
          (fn [col-index pixel]
            (if (< col-index width) \# pixel))
          row)
        row))
    matrix))

(defn process-rect [instruction matrix]
  (let [[_ width height] (re-matches #"rect (\d+)x(\d+)" instruction)]
    (create-rect (Integer/parseInt width) (Integer/parseInt height) matrix)))

(defn process-rotate-col [instruction matrix]
  (let [[_ col-index distance] (re-matches #"rotate column x=(\d+) by (\d+)" instruction)]
    (rotate-col (Integer/parseInt col-index) (Integer/parseInt distance) matrix)))

(defn process-rotate-row [instruction matrix]
  (let [[_ row-index distance] (re-matches #"rotate row y=(\d+) by (\d+)" instruction)]
    (rotate-row (Integer/parseInt row-index) (Integer/parseInt distance) matrix)))

(defn process-instruction [instruction matrix]
  (if (re-find #"rect" instruction)
    (process-rect instruction matrix)
    (if (re-find #"rotate column" instruction)
      (process-rotate-col instruction matrix)
      (if (re-find #"rotate row" instruction)
        (process-rotate-row instruction matrix)
        (throw (Exception. (str "Unrecognised instruction: " instruction)))))))

(defn count-lit [matrix]
  (let [pixels (flatten matrix)]
    (count
      (filter #(= \# %) pixels))))

(defn run [instructions initial]
  (reduce
    (fn [matrix instruction]
      (process-instruction instruction matrix))
    initial
    instructions))

(defn part-1 [instructions matrix]
  (count-lit (run instructions matrix)))

(defn part-2 [instructions matrix]
  (map #(str/join "" %) (run instructions matrix)))

(part-1 puzzle-input puzzle-initial)
(part-2 puzzle-input puzzle-initial)
