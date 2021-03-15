(ns aoc-2016-clojure.day01-1)

(defn parseInput
  "Parses an input"
  [input]
  (clojure.string/split input #", "))

(defn rotate
  "Rotates a compass point"
  [current direction]
  (let
    [inc (if (= \L direction) -1 1)]
    (mod (+ current inc) 4)))

(defn move
  "Moves"
  [[x y] direction distance]
  (case direction
    0 [x (+ y distance)]
    1 [(+ x distance) y]
    2 [x (- y distance)]
    3 [(- x distance) y])
  )

(defn process-instruction
  "Processes an instruction"
  [state instruction]
  (let [[x y facing] state
        new-facing (rotate facing (first instruction))
        distance (Integer/parseInt (subs instruction 1))
        [new-x new-y] (move [x y] new-facing distance)
        ]
    [new-x new-y new-facing])
  )

(defn process-instructions
  "Processes multiple instructions"
  [state instructions]
  (if (= instructions `())
    state
    (process-instructions
      (process-instruction state (first instructions))
      (rest instructions))))

(defn find-distance
  "Finds the distance"
  [[x y]]
  (+ x y))

(let [input "L2, L3, L3, L4, R1, R2, L3, R3, R3, L1, L3, R2, R3, L3, R4, R3, R3, L1, L4, R4, L2, R5, R1, L5, R1, R3, L5, R2, L2, R2, R1, L1, L3, L3, R4, R5, R4, L1, L189, L2, R2, L5, R5, R45, L3, R4, R77, L1, R1, R194, R2, L5, L3, L2, L1, R5, L3, L3, L5, L5, L5, R2, L1, L2, L3, R2, R5, R4, L2, R3, R5, L2, L2, R3, L3, L2, L1, L3, R5, R4, R3, R2, L1, R2, L5, R4, L5, L4, R4, L2, R5, L3, L2, R4, L1, L2, R2, R3, L2, L5, R1, R1, R3, R4, R1, R2, R4, R5, L3, L5, L3, L3, R5, R4, R1, L3, R1, L3, R3, R3, R3, L1, R3, R4, L5, L3, L1, L5, L4, R4, R1, L4, R3, R3, R5, R4, R3, R3, L1, L2, R1, L4, L4, L3, L4, L3, L5, R2, R4, L2"
      finalState (process-instructions [0 0 0] (parseInput input))
      ]
  (find-distance finalState))
