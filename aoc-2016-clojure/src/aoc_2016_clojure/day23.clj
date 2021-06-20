(ns aoc-2016-clojure.day23
  (:require [clojure.string :as str]
            [clojure.java.io :as io]))

(def puzzleInputDay12 (str/split (slurp (io/resource "day12.txt")) #"\n"))
(def puzzleInput (str/split (slurp (io/resource "day23.txt")) #"\n"))

(def testInput (str/split "cpy 2 a
tgl a
tgl a
tgl a
cpy 1 a
dec a
dec a" #"\n"))

(defn clear-log []
  (spit "output.txt" ""))
(defn log [string]
  (spit "output.txt" (str string "\n") :append true))

(defn string-to-number
  [string]
  (let [number (re-matches #"-?\d+" string)]
    (if number (Integer/parseInt number) nil)))

(defn jump
  [steps state]
    (assoc state :position (+ steps (:position state))))

(defn cpy
  [from to state]
  (if (string-to-number to)
    (jump 1 state)
    (let [from-value (string-to-number from)
          to-key (keyword to)]
      (jump 1
            (if from-value
              (assoc state to-key from-value)
              (assoc state to-key ((keyword from) state)))))))

(defn incr
  [reg state]
  (if (string-to-number reg)
    (jump 1 state)
    (let [reg-key (keyword reg)]
     (jump 1
           (assoc state reg-key (inc (reg-key state)))))))

(defn decr
  [reg state]
  (if (string-to-number reg)
    (jump 1 state)
    (let [reg-key (keyword reg)]
      (jump 1
            (assoc state reg-key (dec (reg-key state)))))))

(defn jnz
  [test steps state]
  (let [test-value  (or (string-to-number test) ((keyword test) state))
        steps-value (or (string-to-number steps) ((keyword steps) state))]
    (if (= 0 test-value)
      (jump 1 state)
      (jump steps-value state))))

(defn multiply-and-add
  [reg-1 reg-2 target-reg state]
  (let [_                (log "i tried to multiplied")
        value-1          ((keyword reg-1) state)
        value-2          ((keyword reg-2) state)
        target-value     ((keyword target-reg) state)
        new-target-value (+ target-value (* value-1 value-2))]
    (jump 5 (assoc state
                   (keyword reg-1) 0
                   (keyword reg-2) 0
                   (keyword target-reg) new-target-value))))

(defn toggle
  [instructions position]
  (let [target-instruction (get instructions position)
        new-instruction (if target-instruction
                          (let [[opcode param-1 param-2] (str/split target-instruction #" ")]
                            (if param-2
                              (if (= opcode "jnz")
                                (str/join " " ["cpy" param-1 param-2])
                                (str/join " " ["jnz" param-1 param-2]))
                              (if (= opcode "inc")
                                (str/join " " ["dec" param-1])
                                (str/join " " ["inc" param-1]))))
                          nil)]
    (if new-instruction
      (assoc instructions position new-instruction)
      instructions)))

(defn tgl
  [steps state]
  (let [{instructions :instructions
         position     :position}    state
        steps-value                 (or (string-to-number steps) ((keyword steps) state))
        target-position             (+ position steps-value)
        new-instructions            (toggle instructions target-position)]
    (jump 1 (assoc state :instructions new-instructions))))

(defn process-one-instruction
  [state]
  (let [{instructions :instructions
         position     :position}    state
        instruction                 (get instructions position)
        [opcode param-1 param-2]    (str/split instruction #" ")]
    (case opcode
      "cpy" (cpy param-1 param-2 state)
      "inc" (incr param-1 state)
      "dec" (decr param-1 state)
      "jnz" (jnz param-1 param-2 state)
      "tgl" (tgl param-1 state))))

(defn process-line-5
  [state]
  (let [{instructions :instructions} state
        line-5 (get instructions 5)
        line-6 (get instructions 6)
        line-7 (get instructions 7)
        line-8 (get instructions 8)
        line-9 (get instructions 9)
        _ (println line-5)]
    (if (and (= (str/split line-5 #" ") "inc")
             (= (str/split line-6 #" ") "dec")
             (= (str/split line-7 #" ") "jnz")
             (= (str/split line-8 #" ") "dec")
             (= (str/split line-9 #" ") "jnz"))
      (multiply-and-add "c" "d" "a" state)
      (let []
        (log (format "Something changed in lines 5-9\n%s"
                     (:instructions state)))
       (process-one-instruction state)))))

(defn process-special-lines
  [state]
  (if (= (:position state) 5)
    (process-line-5 state)
    (process-one-instruction state)))

(defn process-line
  [state]
  (let [special-lines [5]]
    (if (some #{(:position state)} special-lines)
      (process-special-lines state)
      (process-one-instruction state))))

(defn write-state [state]
  (let [{a :a b :b c :c d :d position :position} state]
    (log (format "a: %s, b: %s, c: %s, d: %s, line: %s"
                   a b c d position))))

(defn run
  [initial-state]
  (loop
      [state initial-state]
    (let [{position     :position
           instructions :instructions
           iterations   :iterations} state
          _ (write-state state)
          ]
      (if (>= position (count instructions))
       state
       (recur
        (process-line
         (assoc state :iterations (inc iterations))))))))

(defn part-1
  [instructions]
  (let [_ (clear-log)
        initial-state {:a 7 :b 0 :c 0 :d 0 :position 0 :instructions instructions :iterations 0}]
    (run initial-state)))

(part-1 puzzleInput)
