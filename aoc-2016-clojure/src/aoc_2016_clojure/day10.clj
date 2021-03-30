(ns aoc-2016-clojure.day10
  (:require [clojure.java.io :as io]
            [clojure.string :as str]))

(def test-input (str/split "value 5 goes to bot 2\nbot 2 gives low to bot 1 and high to bot 0\nvalue 3 goes to bot 1\nbot 1 gives low to output 1 and high to bot 0\nbot 0 gives low to output 2 and high to output 0\nvalue 2 goes to bot 2\n" #"\n"))
(def puzzle-input (str/split (slurp (io/resource "day10.txt")) #"\n"))

(defn kebab-case [string]
  (str/replace string #"\s" "-"))

(defn update-bots [bots line]
  (let [[_ value bot] (re-find #"value (\d+) goes to bot (\d+)" line)
        bot-key  (keyword (str "bot-" bot))
        new-bots (assoc bots bot-key (conj (bot-key bots) (Integer/parseInt value)))]
    new-bots))

(defn update-rules [rules line]
  (let [[_ bot low high] (re-find #"bot (\d+) gives low to (.*) and high to (.*)" line)
        bot-key   (keyword (str "bot-" bot))
        new-rules (assoc rules bot-key {:low (keyword (kebab-case low)) :high (keyword (kebab-case high))})]
    new-rules))

(defn parse-input [input]
  (reduce
    (fn [[bots rules] line]
      (if (str/includes? line "value")
        [(update-bots bots line) rules]
        [bots (update-rules rules line)]))
    [{} {}]
    input))

(defn find-bot-with-2-values [state]
  (filter
    (fn [[key values]]
      (and
        (= 2 (count values))
        (str/includes? (name key) "bot")))
    (seq state)))

(defn go-one-step [state rules]
  (let [[[two-value-bot two-values]] (find-bot-with-2-values state)
        [low-value high-value] (sort two-values)
        low-destination-bot  (:low (two-value-bot rules))
        high-destination-bot (:high (two-value-bot rules))]
    (assoc (dissoc state two-value-bot)
      low-destination-bot (conj (low-destination-bot state) low-value)
      high-destination-bot (conj (high-destination-bot state) high-value))))

(let [[state rules] (parse-input puzzle-input)]
  state)

(defn part-1 [input]
  (let [[initial-state rules] (parse-input input)]
    (loop [state initial-state]
      (let [[[two-value-bot two-values]] (find-bot-with-2-values state)]
        (if (and (.contains two-values 61) (.contains two-values 17))
          two-value-bot
          (let [new-state (go-one-step state rules)]
            (recur new-state)))))))

(defn run-the-whole-thing [input]
  (let [[initial-state rules] (parse-input input)]
    (loop [state initial-state]
      (if (empty? (find-bot-with-2-values state))
        state
        (let [new-state (go-one-step state rules)]
          (recur new-state))))))

(defn part-2 [input]
  (let [end-state (run-the-whole-thing input)]
    (*
      (first (:output-0 end-state))
      (first (:output-1 end-state))
      (first (:output-2 end-state)))))

(part-1 puzzle-input)
(part-2 puzzle-input)