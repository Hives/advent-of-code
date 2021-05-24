(ns aoc-2016-clojure.day21
  (:require [clojure.string :as str]
            [clojure.java.io :as io]))

(def puzzleInput (str/split (slurp (io/resource "day21.txt")) #"\n"))

(def ex (vec "abcdefgh"))

(defn toInt [string] (Integer/parseInt string))

(defn swap-pos
  [pos-1 pos-2 input]
  (let [char-1 (get input pos-1)
        char-2 (get input pos-2)]
    (-> input
        (assoc pos-1 char-2)
        (assoc pos-2 char-1))))

(defn swap-char
  [char-1 char-2 input]
  (vec (map #(if (= % char-1)
           char-2
           (if (= % char-2)
             char-1
             %))
        input)))

(defn rotate
  [distance input]
  (let [new-start (mod distance (count input))]
    (vec (concat
     (subvec input new-start)
     (subvec input 0 new-start)))))

(defn rotate-left [distance input] (rotate distance input))
(defn rotate-right [distance input] (rotate (- distance) input))

(defn rotate-based-on-letter
  [letter input]
  (let [index      (.indexOf input letter)
        bonus-step (if (>= index 4) 1 0)]
    (rotate-right (+ 1 index bonus-step) input)))

(defn reverse-x-through-y
  [index-x index-y input]
  (vec (concat
        (subvec input 0 index-x)
        (reverse (subvec input index-x (inc index-y)))
        (subvec input (inc index-y)))))

(defn delete
  [index input]
  (vec (concat
        (subvec input 0 index)
        (subvec input (inc index)))))

(defn insert
  [char index input]
  (vec (concat
        (subvec input 0 index)
        [char]
        (subvec input index))))

(defn move-x-to-y
  [index-x index-y input]
  (let [char (get input index-x)]
    (->> input
         (delete index-x)
         (insert char index-y))))

(defn find-move [instruction]
  (let [[_ from to] (re-matches #"move position (\d+) to position (\d+)" instruction)]
    (if (and from to)
      (partial move-x-to-y from to)
      false)))

(def matchers
  [{:matcher   #"move position (\d+) to position (\d+)"
    :create-fn (fn [group-1 group-2]
                 (partial move-x-to-y (toInt group-1) (toInt group-2)))}
   {:matcher   #"swap position (\d+) with position (\d+)"
    :create-fn (fn [group-1 group-2]
                 (partial swap-pos (toInt group-1) (toInt group-2)))}
   {:matcher   #"swap letter ([a-z]) with letter ([a-z])"
    :create-fn (fn [group-1 group-2]
                 (partial swap-char (first group-1) (first group-2)))}
   {:matcher   #"rotate left (\d+) steps?"
    :create-fn (fn [group-1 _]
                 (partial rotate-left (toInt group-1)))}
   {:matcher   #"rotate right (\d+) steps?"
    :create-fn (fn [group-1 _]
                 (partial rotate-right (toInt group-1)))}
   {:matcher   #"rotate based on position of letter ([a-z])"
    :create-fn (fn [group-1 _]
                 (partial rotate-based-on-letter (first group-1)))}
   {:matcher   #"reverse positions (\d+) through (\d+)"
    :create-fn (fn [group-1 group-2]
                 (partial reverse-x-through-y (toInt group-1) (toInt group-2)))}
   {:matcher   #"move position (\d+) to position (\d+)"
    :create-fn (fn [group-1 group-2]
                 (partial move-x-to-y (toInt group-1) (toInt group-2)))}
   ])

(defn match-and-create-operation [{matcher :matcher create-fn :create-fn} instruction]
  (let [[match group-1 group-2] (re-matches matcher instruction)]
    (if match
      (create-fn group-1 group-2)
      false)))

(defn process-instruction
  [input instruction]
  (let [operation (some #(match-and-create-operation % instruction) matchers)]
    (operation input)))

(defn part-1
  [instructions]
  (str/join (reduce process-instruction (vec "abcdefgh") instructions)))

(part-1 puzzleInput)
