(ns aoc-2016-clojure.day18)

(def puzzle-input ".^^^.^.^^^^^..^^^..^..^..^^..^.^.^.^^.^^....^.^...^.^^.^^.^^..^^..^.^..^^^.^^...^...^^....^^.^^^^^^^")
(def example-input ".^^.^.^^^^")
(def sierpinski-input "................................................................................^................................................................................")

(defn is-trap? [trio]
  (not (= (first trio) (last trio))))

(defn get-next-row [row]
  (->> (concat [\.] row [\.])
       (partition 3 1)
       (map #(if (is-trap? %) \^ \.))))

(defn find-safe-spaces [first n]
  (->> first
       (seq)
       (iterate get-next-row)
       (take n)
       (flatten)
       (filter #(= \. %))
       (count)))

(defn printy [first n]
  (->> first
       (seq)
       (iterate get-next-row)
       (take n)
       (map (fn [row] (map (fn [cell] (if (= \. cell) \space cell)) row)))
       (map #(apply str %))
       (clojure.string/join "\n")
       (println)))

(find-safe-spaces puzzle-input 40)
;; (do-it puzzle-input 400000) ;; slow >1m
;;
(printy sierpinski-input 40)
