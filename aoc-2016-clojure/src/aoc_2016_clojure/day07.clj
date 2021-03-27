(ns aoc-2016-clojure.day07
  (:require [clojure.string :as str]
            [clojure.set :as set]
            [clojure.java.io :as io]))

(def test-input (str/split "abba[mnop]qrst\nabcd[bddb]xyyx\naaaa[qwer]tyui\nioxxoj[asdfgh]zxcvbn" #"\n"))
(def puzzle-input (str/split (slurp (io/resource "day07.txt")) #"\n"))

(defn has-abba [input]
  (some? (re-seq #"(\w)(?!\1)(\w)\2\1" input)))

(defn get-letter-sequences [input]
  (re-seq #"\w+" input))

(defn get-bracketed-sequences [input]
  (map second (re-seq #"\[(\w+)\]" input)))

(defn parse-ip7 [ip7]
  (let [bracketed-sequences (set (get-bracketed-sequences ip7))
        all-sequences       (set (get-letter-sequences ip7))]
    {:bracketed     bracketed-sequences
     :non-bracketed (set/difference all-sequences bracketed-sequences)}))

(defn supports-tls [ip7]
  (let [{bracketed     :bracketed
         non-bracketed :non-bracketed} (parse-ip7 ip7)]
    (and
      (not-any? has-abba bracketed)
      (some has-abba non-bracketed))))

(defn part-1 [input]
  (count (filter supports-tls input)))

(part-1 puzzle-input)

(defn tails [input]
  (reductions (fn [string _] (subs string 1)) input input))

(defn find-first-aba [input]
  (first (re-find #"(\w)(?!\1)(\w)\1" input)))

(defn find-all-aba [input]
  (distinct (remove nil? (map find-first-aba (tails input)))))

(defn aba-to-bab [aba]
  (str (subs aba 1) (subs aba 1 2)))

(defn at-least-one-needle-exists-in-haystack [haystack needles]
  (some #(str/includes? haystack %) needles))

(defn at-least-one-needle-exists-in-at-least-one-haystack [haystacks needles]
  (some #(at-least-one-needle-exists-in-haystack % needles) haystacks))

(defn supports-ssl [ip7]
  (let [{bracketed     :bracketed
         non-bracketed :non-bracketed} (parse-ip7 ip7)
        non-bracketed-aba (flatten (map find-all-aba non-bracketed))
        babs              (map aba-to-bab non-bracketed-aba)]
    (and
      (not-empty non-bracketed-aba)
      (at-least-one-needle-exists-in-at-least-one-haystack bracketed babs))))

(defn part-2 [input]
  (count (filter supports-ssl input)))

(part-2 puzzle-input)
