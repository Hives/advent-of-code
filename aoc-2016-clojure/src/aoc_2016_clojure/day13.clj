(ns aoc-2016-clojure.day13
  (:require [clojure.string :as str]))

(def test-input 10)
(def puzzle-input 1352)
(def favourite-number puzzle-input)

(defn add-points
  [point-1 point-2]
  (let [{x-1 :x y-1 :y} point-1
        {x-2 :x y-2 :y} point-2]
    {:x (+ x-1 x-2) :y (+ y-1 y-2)}))

(defn is-within-bounds?
  [point]
  (let [{x :x y :y} point]
    (and (>= x 0) (>= y 0))))

(defn count-ones
  [input]
  (count (filter #(= "1" %) (str/split input #""))))

(defn is-open-space?
  [point]
  (let [{x :x y :y}          point
        magic-formula        (+ (* x x) (* 3 x) (* 2 x y) y (* y y) favourite-number)
        magic-formula-binary (Integer/toString magic-formula 2)]
    (even? (count-ones magic-formula-binary))))

(defn get-open-neighbours
  [point]
  (let [neighbour-vectors [{:x 0 :y -1}
                           {:x -1 :y 0}
                           {:x 0 :y 1}
                           {:x 1 :y 0}]]
    (filter
     #(and (is-within-bounds? %) (is-open-space? %))
     (map #(add-points point %) neighbour-vectors))))

(defn extend-path
  [path]
  (let [next-points             (get-open-neighbours (last path))
        non-looping-next-points (filter #(not (.contains path %)) next-points)]
    (if (empty? non-looping-next-points)
      [path]
      (map #(conj path %) non-looping-next-points))))

(defn find-shortest-path
  [start end]
  (loop
      [paths [[start]]]
    (let [complete-paths (filter #(= end (last %)) paths)]
      (if (not-empty complete-paths)
        (- (count (first complete-paths)) 1)
        (recur (mapcat extend-path paths))))))

(defn count-locations-reached
  [start max-steps]
  (loop
      [paths [[start]]
       steps 0]
    (if (= steps max-steps)
      (count (distinct (flatten paths)))
      (recur (mapcat extend-path paths) (inc steps)))))

(defn part-1
  []
  (find-shortest-path {:x 1 :y 1} {:x 31 :y 39}))

(defn part-2
  []
  (count-locations-reached {:x 1 :y 1} 50))
