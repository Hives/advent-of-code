(ns aoc-2016-clojure.day22
  (:require [clojure.java.io :as io]
            [clojure.string :as str]
            [clojure.math.combinatorics :as combo]))

(def puzzleInput (drop 1 (str/split (slurp (io/resource "day22.txt")) #"\n")))

(defn toInt [string] (Integer/parseInt string))

(defn parse
  [input]
  (let [[_ x y size used avail]
        (re-find #"node-x(\d+)-y(\d+) +(\d+)T +(\d+)T +(\d+)T" input)]
    {:x (toInt x)
     :y (toInt y)
     :size (toInt size)
     :used (toInt used)
     :avail (toInt avail)}))

(def nodes (map parse puzzleInput))

(defn is-viable-pair?
  [[a b]]
  (and (not= 0 (:used a))
       (not= a b)
       (<= (:used a) (:avail b))))

(defn part-1 []
  (->> (combo/selections nodes 2)
       (filter is-viable-pair?)
       (count)))

(defn pad [n string]
  (str/join [(apply str (repeat (- n (count string)) " ")) string]))

(defn print-node [node]
  (str/join ["("
             (pad 3 (str (:used node)))
             "/"
             (pad 3 (str (:size node)))
             ")"]))

(defn print-row [nodes]
  (->> nodes
       (map print-node)
       (str/join " ")))

(->> nodes
     (partition 31)
     (map print-row)
     (str/join "\n")
     (spit "day-22-nodes.txt"))

;; solution was to print a representation of the nodes to a file, then it's
;; possible to count the number of moves required to move the zero node to
;; the top right, and then to move the required data to the top left.
;; n.b. the threading macro above prints out the nodes with rows and columns
;; reversed, so you actually need to move the bottom left to the top left
