(ns aoc-2016-clojure.day18)

(def puzzle-input ".^^^.^.^^^^^..^^^..^..^..^^..^.^.^.^^.^^....^.^...^.^^.^^.^^..^^..^.^..^^^.^^...^...^^....^^.^^^^^^^")
(def example-input ".^^.^.^^^^")

(defn is-trap? [trio]
  (or
   (= '(\^ \^ \.) trio)
   (= '(\^ \. \.) trio)
   (= '(\. \^ \^) trio)
   (= '(\. \. \^) trio)))

(defn get-next-row [row]
  (->> (concat [\.] row [\.])
       (partition 3 1)
       (map #(if (is-trap? %) \^ \.))))

(defn get-rows [first n]
  (loop [rows [first]]
    (if (= (count rows) n)
      rows
      (recur (conj rows (get-next-row (last rows)))))))

(defn part-1 [input n]
  (let [first (seq input)
        final (map #(apply str %) (get-rows first n))
        _     (println (clojure.string/join "\n" final))]
    (count (remove #(= \^ %) (seq (clojure.string/join "" final))))))

(part-1 puzzle-input 40)
;; (part-1 puzzle-input 400000)
