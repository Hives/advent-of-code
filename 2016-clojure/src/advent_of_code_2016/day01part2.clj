(ns advent-of-code-2016.day01part2)

(defn parseInstruction
  "Parses one unit of the input"
  [instruction]
  (let [direction (first instruction)
        distance (Integer/parseInt (subs instruction 1))]
    (cons direction (repeat distance \F))))

(defn parseInput
  "Parses a series of instruction"
  [input]
  (let [instructions (clojure.string/split input #", ")]
    (mapcat parseInstruction instructions)))

(defn move
  "Moves one step"
  [location facing history]
  (let [[x y] location
        new-location (case facing
                       0 [x (+ y 1)]
                       1 [(+ x 1) y]
                       2 [x (- y 1)]
                       3 [(- x 1) y])]
    [new-location (conj history location)])
  )

(defn rotate
  "Rotates one quarter turn"
  [facing direction]
  (let [new-facing
        (case direction
          \L (- facing 1)
          \R (+ facing 1))]
    (mod new-facing 4)))

(defn go
  "Do the thing"
  [location facing history instructions]
  (let [instruction (first instructions)]
    (if (= instruction \F)
      (let [[new-location new-history] (move location facing history)]
        (if (some #{new-location} new-history)
          new-location
          (go new-location facing new-history (rest instructions))))
      (go location (rotate facing instruction) history (rest instructions)))))

(defn find-distance
  "Finds the distance"
  [[x y]]
  (+ (Math/abs x) (Math/abs y)))

(let [input "L2, L3, L3, L4, R1, R2, L3, R3, R3, L1, L3, R2, R3, L3, R4, R3, R3, L1, L4, R4, L2, R5, R1, L5, R1, R3, L5, R2, L2, R2, R1, L1, L3, L3, R4, R5, R4, L1, L189, L2, R2, L5, R5, R45, L3, R4, R77, L1, R1, R194, R2, L5, L3, L2, L1, R5, L3, L3, L5, L5, L5, R2, L1, L2, L3, R2, R5, R4, L2, R3, R5, L2, L2, R3, L3, L2, L1, L3, R5, R4, R3, R2, L1, R2, L5, R4, L5, L4, R4, L2, R5, L3, L2, R4, L1, L2, R2, R3, L2, L5, R1, R1, R3, R4, R1, R2, R4, R5, L3, L5, L3, L3, R5, R4, R1, L3, R1, L3, R3, R3, R3, L1, R3, R4, L5, L3, L1, L5, L4, R4, R1, L4, R3, R3, R5, R4, R3, R3, L1, L2, R1, L4, L4, L3, L4, L3, L5, R2, R4, L2"
      testInput "R8, R4, R4, R8"
      ]
  (find-distance (go [0 0] 0 [] (parseInput input))))
