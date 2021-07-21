(ns aoc-2016-clojure.day25
  (:require [clojure.string :as str]
            [clojure.java.io :as io]))

(def puzzleInput (str/split (slurp (io/resource "day25.txt")) #"\n"))

(defn string-to-number
  [string]
  (let [number (re-matches #"\d+" string)]
    (if number (Integer/parseInt number) nil)))

(defn jump
  [steps state]
  (assoc state :place (+ steps (:place state))))

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

(defn out
  [reg state]
  (let [old-output   (state :output)
        output-value (state (keyword reg))
        new-output   (conj old-output output-value)]
    (jump 1
          (assoc state
                 :output    new-output
                 :is-valid? (validate-clock-signal new-output)))))

(defn process-instruction
  [instruction state]
  (let [[opcode param-1 param-2] (str/split instruction #" ")]
    (case opcode
      "cpy" (cpy param-1 param-2 state)
      "inc" (incr param-1 state)
      "dec" (decr param-1 state)
      "jnz" (jnz param-1 param-2 state)
      "out" (out param-1 state))))

(defn alternate [n]
  (if (= 0 n) 1 0))

(defn validate-clock-signal
  [signal]
  (loop [signal   signal
         expected 0]
      (cond
        (empty? signal) true
        (= expected (first signal)) (recur (drop 1 signal) (alternate expected))
        :else false)))

(validate-clock-signal [0 0 1 1 0 1 0 1])

(defn process-instructions
  [instructions initial-state]
  (loop
      [state initial-state]
    (cond
      (not (state :is-valid?)) false
      (= 10 (count (state :output))) true
      :else (recur
             (process-instruction
               (get instructions (:place state))
               state)))))

(defn test-value
  [instructions initial-a]
  (let [initial-state {:a initial-a :b 0 :c 0 :d 0 :place 0 :output [] :is-valid? true}]
    (process-instructions instructions initial-state)))

(defn part-1
  [instructions]
  (loop [initial-a 0]
     (let [_ (if (= 0 (mod initial-a 1)) (println (str "testing " initial-a)))]
       (if (test-value instructions initial-a)
        initial-a
        (recur (inc initial-a))))))

(part-1 puzzleInput)
