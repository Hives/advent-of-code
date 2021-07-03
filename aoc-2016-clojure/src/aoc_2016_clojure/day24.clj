(ns aoc-2016-clojure.day24
  (:require [clojure.string :as str]
            [clojure.java.io :as io]
            [clojure.set :as set]
            [clojure.math.combinatorics :as combo]))

(def puzzle-input (slurp (io/resource "day24.txt")))

(def test-input
  "###########
#0.1.....2#
#.#######.#
#4.......3#
###########")

(defn parse-input [input]
  (vec (map vec (str/split input #"\n"))))

(defn in?
  [coll elm]
  (some #(= elm %) coll))

(def test-floor-plan (parse-input test-input))
(def floor-plan (parse-input puzzle-input))

(defn find-start [floor-plan]
  (let [start-row (first (filter #(in? % \0) floor-plan))
        y         (.indexOf floor-plan start-row)
        x         (.indexOf start-row \0)]
    {:x x :y y}))

(defn is-target [square]
  (and (not= square \#) (not= square \.)))

(defn find-coordinates [floor-plan target]
  (let [y (first (filter #(.contains (floor-plan %) target) (range)))
        x (first (filter #(= target ((floor-plan y) %)) (range)))]
    {:x x :y y}))

(defn get-targets [floor-plan]
  (->> floor-plan
       (flatten)
       (filter is-target)
       (reduce (fn [acc next] (assoc acc next (find-coordinates floor-plan next))) {})))

(defn add [point-1 point-2]
  {:x (+ (:x point-1) (:x point-2))
   :y (+ (:y point-1) (:y point-2))})

(defn neighbours [point]
  (let [directions [{:x 0 :y -1} {:x 1 :y 0} {:x 0 :y 1} {:x -1 :y 0}]]
    (map #(add point %) directions)))

(defn get-square [floor-plan {:keys [x y]}]
  (get (get floor-plan y) x))

(defn is-not-wall? [floor-plan point]
  (let [square (get-square floor-plan point)]
    (not= \# square)))

(defn viable-neighbours [floor-plan point]
  (->> point
       (neighbours)
       (filter #(is-not-wall? floor-plan %))))

(defn shortest-distance-between [floor-plan start end]
  (println "--")
  (println (str "finding shortest distance between " start " and " end))
  (loop [steps 0
         visited #{}
         current [start]]
    (if (in? current end)
      (let [_ (println (str "shortest distance: " steps))]
        steps)
      (let [new-visited (set/union visited (set current))
            new-current (->> current
                             (mapcat #(viable-neighbours floor-plan %))
                             (set)
                             (remove #(in? new-visited %)))]
        (recur
         (inc steps)
         new-visited
         new-current)))))

(defn shortest-distances-between-targets [floor-plan]
  (let [targets (get-targets floor-plan)
        target-key-pairs (map set (combo/combinations (keys targets) 2))]
    (reduce
     (fn [acc target-keys]
       (let [key1 ((vec target-keys) 0)
             key2 ((vec target-keys) 1)]
         (println target-keys)
         (assoc acc target-keys
                (shortest-distance-between floor-plan (targets key1) (targets key2)))))
     {}
     target-key-pairs)))

(defn get-path-distance [path distances]
  (->> path
       (partition 2 1)
       (reduce
        (fn [running-total next-leg]
          (+ running-total (distances (set next-leg))))
        0)))

(defn part-1 [floor-plan]
  (let [target-keys    (keys (get-targets floor-plan))
        distances      (shortest-distances-between-targets floor-plan)
        viable-paths   (map #(conj % \0) (combo/permutations (remove #(= \0 %) target-keys)))
        path-distances (map #(get-path-distance % distances) viable-paths)]
    (apply min path-distances)))

(defn part-2 [floor-plan]
  (let [target-keys    (keys (get-targets floor-plan))
        distances      (shortest-distances-between-targets floor-plan)
        viable-paths   (map #(concat (conj % \0) [\0]) (combo/permutations (remove #(= \0 %) target-keys)))
        path-distances (map #(get-path-distance % distances) viable-paths)]
    (apply min path-distances)))

;; (part-1 floor-plan) ;; slow ~1m
;; (part-2 floor-plan) ;; slow ~1m
