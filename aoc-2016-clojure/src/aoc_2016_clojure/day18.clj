(ns aoc-2016-clojure.day18)

(def puzzle-input ".^^^.^.^^^^^..^^^..^..^..^^..^.^.^.^^.^^....^.^...^.^^.^^.^^..^^..^.^..^^^.^^...^...^^....^^.^^^^^^^")
(def example-input ".^^.^.^^^^")

(defn is-trap? [trio]
  (let [trio-string (apply str trio)]
    (or
     (= "^^." trio-string)
     (= ".^^" trio-string)
     (= "^.." trio-string)
     (= "..^" trio-string))))

(defn get-next-row [row]
  (->> (concat [\.] row [\.])
       (partition 3 1)
       (map #(if (is-trap? %) \^ \.))))

(defn get-rows-1 [first n]
  (loop [rows [first]]
    (if (= (count rows) n)
      rows
      (recur (conj rows (get-next-row (last rows)))))))

(defn part-1 [input n]
  (let [first (seq input)
        final (map #(apply str %) (get-rows first n))
        _     (println (clojure.string/join "\n" final))]
    (count (remove #(= \^ %) (seq (clojure.string/join "" final))))))

(defn get-rows-2 [first n]
  (loop [row              first
         safe-tiles-count 0
         row-count        1]
    (let [new-safe-tiles-count (+
                                safe-tiles-count
                                (count (filter #(= \. %) row)))]
      (if (= n row-count)
        new-safe-tiles-count
        (recur (get-next-row row) new-safe-tiles-count (inc row-count))))))

;; (part-1 puzzle-input 40)
;; (get-rows-2 puzzle-input 400000) ;; slow (~1m)
