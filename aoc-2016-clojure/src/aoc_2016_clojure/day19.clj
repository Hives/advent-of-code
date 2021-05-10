(ns aoc-2016-clojure.day19)

(def puzzle-input 3017957)

(defn make-elves [n]
  (vec (range 1 (inc n))))

(defn rotate [v]
  (conj (subvec v 1) (first v)))

(defn n-times [f n input]
  (loop [i   n
         acc input]
    (if (= i 0)
      acc
      (recur (dec i) (f acc)))))

(defn del [index v]
  (let [start (subvec v 0 index)
        end   (subvec v (inc index))]
    (into start end)))

(def elves (make-elves puzzle-input))
;; this is too slow!
(time (del 1500000 elves))

(defn one-turn-1 [v]
  (subvec (rotate v) 1))

(defn one-turn-2 [v]
  (let [target (quot (count v) 2)
        _      (if (= (rem (count v) 2) 0) (println (count v)))]
    (->> v
        (del target)
        (rotate))))

(defn go [n one-turn]
  (loop [elves (make-elves n)]
    (if (= 1 (count elves))
      (first elves)
      (recur (one-turn elves)))))

(go puzzle-input one-turn-1)
