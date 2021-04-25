(ns aoc-2016-clojure.day16
  (require [clojure.string :as str]))

(def example-input "10000")
(def puzzle-input "10011111011011001")

(defn invert [x]
  (map
   #(if (= % \1) \0 \1)
   x))

(defn expand [a]
  (let [b (invert (reverse a))]
    (concat a (list \0) b)))

(defn expand-to-fill-disk [input disk-length]
  (loop [current input]
    (if (> (count current) disk-length)
      (take disk-length current)
      (recur (expand current)))))

(defn iterations-to-fill-disk [seed disk-length]
  (loop [length (count seed)
         iteration 0]
    (if (>= length disk-length)
      iteration
      (recur (+ 1 (* 2 length)) (inc iteration)))))

(defn expand-n-times [seed n]
  (loop [iteration n
         current   seed]
    (if (= iteration 0)
      current
      (recur (dec iteration) (expand current)))))

(count (expand-n-times "1" 20))

;; (defn expand-to-fill-disk-2 [input disk-length]
;;   (let [iterations (iterations-to-fill-disk input disk-length)]))

(defn contract [input]
  (->> input
       (partition 2)
       (map #(let [[a b] %]
               (if (= a b) \1 \0)))))

(defn contract-until-odd-length [input]
  (loop [current input]
    (if (odd? (count current))
      current
      (recur (contract current)))))

(defn do-it [input disk-length]
  (-> input
      (expand-to-fill-disk disk-length)
      (contract-until-odd-length)
      (#(apply str %))))

;; (time (do-it puzzle-input 36864))
;; (time (do-it puzzle-input 35651584))
