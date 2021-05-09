(ns aoc-2016-clojure.day18)

(def puzzle-input ".^^^.^.^^^^^..^^^..^..^..^^..^.^.^.^^.^^....^.^...^.^^.^^.^^..^^..^.^..^^^.^^...^...^^....^^.^^^^^^^")
(def example-input ".^^.^.^^^^")

(defn is-trap? [trio]
  (not (= (first trio) (last trio))))

(defn get-next-row [row]
  (->> (concat [\.] row [\.])
       (partition 3 1)
       (map #(if (is-trap? %) \^ \.))))

(defn do-it [first n]
  (->> first
       (seq)
       (iterate get-next-row)
       (take n)
       (flatten)
       (filter #(= \. %))
       (count)))

(do-it puzzle-input 40)
;; (do-it puzzle-input 400000) ;; slow >1m
