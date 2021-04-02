(ns aoc-2016-clojure.day12
  (:require [clojure.string :as str]
            [clojure.java.io :as io]))

(def test-input ["cpy 41 a" "inc a" "inc a" "dec a" "jnz a 2" "dec a"])
(def puzzleInput (str/split (slurp (io/resource "day12.txt")) #"\n"))

(defn string-to-number
  [string]
  (let [number (re-matches #"\d+" string)]
    (if number (Integer/parseInt number) nil)))

(defn cpy
  [from to state]
  (let [from-value (string-to-number from)
        to-key (keyword to)]
    (jump 1
          (if from-value
            (assoc state to-key from-value)
            (assoc state to-key ((keyword from) state))))))

(defn incr
  [reg state]
  (let [reg-key (keyword reg)]
    (jump 1
         (assoc state reg-key (inc (reg-key state))))))

(defn decr
  [reg state]
  (let [reg-key (keyword reg)]
    (jump 1
         (assoc state reg-key (dec (reg-key state))))))

(defn jump
  [steps state]
  (assoc state :place (+ steps (:place state))))

(defn jnz-int
  [test-value steps state]
  (if (= 0 test-value)
    (jump 1 state)
    (jump steps state)))

(defn jnz-reg
  [test-reg steps state]
    (jnz-int (test-reg state) steps state))

(defn jnz
  [test steps state]
  (let [test-value (string-to-number test)
        steps-value (Integer/parseInt steps)]
    (if test-value
      (jnz-int test-value steps-value state)
      (jnz-reg (keyword test) steps-value state))))

(defn process-instruction
  [instruction state]
  (let [[opcode param-1 param-2] (str/split instruction #" ")]
    (case opcode
      "cpy" (cpy param-1 param-2 state)
      "inc" (incr param-1 state)
      "dec" (decr param-1 state)
      "jnz" (jnz param-1 param-2 state))))

(defn process-instructions
  [instructions initial-state]
  (loop
      [state initial-state]
    (if (>= (:place state) (count instructions))
      state
      (recur (process-instruction
               (get instructions (:place state))
               state)))))

(defn part-1
  [instructions]
  (let [initial-state {:a 0 :b 0 :c 0 :d 0 :place 0}]
    (process-instructions instructions initial-state)))

(defn part-2
  [instructions]
  (let [initial-state {:a 0 :b 0 :c 1 :d 0 :place 0}]
    (process-instructions instructions initial-state)))

(part-1 puzzleInput)
(part-2 puzzleInput)
