(ns aoc-2016-clojure.day24
  (:require [clojure.string :as str]
            [clojure.java.io :as io]))

(def puzzle-input (slurp (io/resource "day24.txt")))

(def test-input
  "###########
#0.1.....2#
#.#######.#
#4.......3#
###########")

(defn parse-input [input]
  (vec (map vec (str/split input #"\n"))))

(def test-floor-plan (parse-input test-input))
(def floor-plan (parse-input puzzle-input))

(defn find-start [floor-plan]
  (let [start-row (first (filter #(some #{\0} %) floor-plan))
        y         (.indexOf floor-plan start-row)
        x         (.indexOf start-row \0)]
    {:x x :y y}))

(find-start test-floor-plan)

(defn is-target [square]
  (and (not= square \#) (not= square \.) (not= square \0)))

(defn get-targets [floor-plan]
  (->> floor-plan
       (flatten)
       (filter is-target)
       (set)))

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

(defn collect [floor-plan collected point]
  (let [square (get-square floor-plan point)]
    (if (is-target square)
      (conj collected square)
      collected)))

(defn next-points [floor-plan point]
  (->> point
       (neighbours)
       (filter #(is-not-wall? floor-plan %))))

(defn next-steps [floor-plan {:keys [point collected]}]
  (->> point
       (next-points floor-plan)
       (map (fn [point]
              {:point point
               :collected (collect floor-plan collected point)}))))

(defn extend-history [floor-plan history]
  (let [current (last history)
        next    (next-steps floor-plan current)]
    (map #(conj history %) next)))

(defn is-loop? [history]
  (let [current  (last history)
        previous (drop-last history)]
    (some #{current} previous)))

(defn is-complete? [history targets]
  (let [{collected :collected} (last history)]
    (= targets collected)))

(defn part-1 [floor-plan]
  (let [start   (find-start floor-plan)
        _       (println start)
        targets (get-targets floor-plan)
        _       (println targets)]
    (loop [histories [[{:point start :collected #{}}]]]
      (if (some #(is-complete? % targets) histories)
        (dec (count (first histories)))
        (let [_ (if (= 0 (mod (count (first histories)) 10))
                  (println (count (first histories))))
              new-histories (->> histories
                                 (mapcat #(extend-history floor-plan %))
                                 (remove is-loop?))]
          (recur new-histories))))))

(part-1 test-floor-plan)
