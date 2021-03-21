(ns aoc-2016-clojure.day05
  (:import (java.security MessageDigest)))

(def testInput "abc")
(def puzzleInput "ugkcyxxp")

; from stackoverflow https://stackoverflow.com/questions/54060656/clojure-hashing-algorithm
(defn md5 [^String s]
  (let [algorithm (MessageDigest/getInstance "MD5")
        raw       (.digest algorithm (.getBytes s))]
    (format "%032x" (BigInteger. 1 raw))))

(defn part1 [doorId]
  (loop [index    0
         password ""]
    (if (= (count password) 8)
      password
      (recur
        (+ 1 index)
        (let [hash (md5 (str doorId index))]
          (if (= (subs hash 0 5) "00000")
            (str password (get hash 5))
            password))))))

;(part1 puzzleInput)

(defn is-valid-position [character]
  (clojure.string/includes? "01234567" (str character)))

(defn replace-at-pos [string, position, insert]
  (str (subs string 0 position) insert (subs string (+ 1 position))))

(defn part2 [doorId]
  (loop [index    0
         password "--------"]
    (if (not-any? #(= \- %) password)
      password
      (recur
        (+ 1 index)
        (let [hash      (md5 (str doorId index))
              position  (get hash 5)
              character (get hash 6)]
          (if (and
                (= (subs hash 0 5) "00000")
                (is-valid-position position)
                (= (get password (Integer/parseInt (str position))) \-))
            (let [new-password (replace-at-pos password (Integer/parseInt (str position)) character)
                  _            (println new-password)]
              new-password)
            password))))))

(part2 puzzleInput)