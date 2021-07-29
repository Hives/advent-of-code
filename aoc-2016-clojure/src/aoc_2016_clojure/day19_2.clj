(ns aoc-2016-clojure.day19_2)

(def puzzle-input 3017957)

;; solved by working out the solution for inputs of 1-500
;; and then working out the pattern (see kotlin solution
;; also in repo)
(defn solve-recursively [n]
  (loop [prev-n 1 prev-winner 1]
    (let [next-n (inc prev-n)
          winner (cond
                   (= prev-winner prev-n) 1
                   (< prev-winner (/ prev-n 2)) (+ prev-winner 1)
                   :else (+ prev-winner 2))]
      (if (= next-n n)
        winner
        (recur next-n winner)))))

(solve-recursively puzzle-input)
