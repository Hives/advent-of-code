(ns aoc-2016-clojure.day14
  (:require [clojure.string :as str])
  (:import (java.security MessageDigest)))

(def example-input "abc")
(def puzzle-input "ngcjuoqr")

(defn md5 [^String s]
  (let [algorithm (MessageDigest/getInstance "MD5")
        raw       (.digest algorithm (.getBytes s))]
    (format "%032x" (BigInteger. 1 raw))))

(defn find-tripled-character [string]
  (let [search-result (re-find #"(.)\1\1" string)]
    (if search-result
      (second search-result)
      nil)))

(defn contains-quintupled-character [char string]
  (let [quintuple (str char char char char char)]
    (str/includes? string quintuple)))

(defn get-thousand-hashes [salt initial-index]
  (->> (range)
    (map #(+ initial-index %))
    (map #(str salt %))
    (map md5)
    (take 1000)))

(defn stretched-hash [input]
  (loop [n        2017
         previous input]
    (if (= 0 n)
      previous
      (recur (dec n) (md5 previous)))))

(def stretched-hash-memoized
  (memoize stretched-hash))

(defn get-thousand-stretched-hashes [salt initial-index]
  (->> (range)
    (map #(+ initial-index %))
    (map #(str salt %))
    (map stretched-hash-memoized)
    (take 1000)))

(defn test-index [salt index]
  (let [hash              (md5 (str salt index))
        tripled-character (find-tripled-character hash)]
    (if tripled-character
      (let [next-thousand-hashes (get-thousand-hashes salt (inc index))]
        (some #(contains-quintupled-character tripled-character %) next-thousand-hashes))
      false)))

(defn test-index-stretched-hash [salt index]
  (let [hash              (stretched-hash-memoized (str salt index))
        tripled-character (find-tripled-character hash)]
    (if tripled-character
      (let [next-thousand-hashes (get-thousand-stretched-hashes salt (inc index))]
        (some #(contains-quintupled-character tripled-character %) next-thousand-hashes))
      false)))

(defn part-1 [salt]
  (loop
    [index         0
     running-total 0]
    (if (= running-total 64)
      (dec index)
      (if (test-index salt index)
        (recur (inc index) (inc running-total))
        (recur (inc index) running-total)))))

(defn part-2 [salt]
  (loop
    [index         0
     running-total 0]
    (let [_ (if (= 0 (mod (inc index) 10))
              (println (str "index: " index ", running-total: " running-total))
              nil)]
      (if (= running-total 64)
        (dec index)
        (if (test-index-stretched-hash salt index)
          (recur (inc index) (inc running-total))
          (recur (inc index) running-total))))))

;(part-1 puzzle-input)
(part-2 puzzle-input)
