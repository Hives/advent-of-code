(ns aoc-2016-clojure.day17
  (:import (java.security MessageDigest)))

(def example-input "hijkl")
(def puzzle-input "edjrjqaa")

; from stackoverflow https://stackoverflow.com/questions/54060656/clojure-hashing-algorithm
(defn md5 [^String s]
  (let [algorithm (MessageDigest/getInstance "MD5")
        raw       (.digest algorithm (.getBytes s))]
    (format "%032x" (BigInteger. 1 raw))))

(defn is-open-door-char? [character]
  (some #(= character %) #{\b \c \d \e \f}))

(defn get-unlocked-directions [hash-string]
  (let [door-chars {\U (get hash-string 0)
                    \D (get hash-string 1)
                    \L (get hash-string 2)
                    \R (get hash-string 3)}]
    (filter
     #(is-open-door-char? (door-chars %))
     (keys door-chars))))

(defn remove-value-if [bool value coll]
  (if bool (remove #{value} coll) coll))

(defn remove-walls [{:keys [x y]} directions]
  (->> directions
       (remove-value-if (= 0 x) \L)
       (remove-value-if (= 3 x) \R)
       (remove-value-if (= 0 y) \U)
       (remove-value-if (= 3 y) \D)))

(defn move [location direction]
  (let [unit-vectors            {\U {:x 0 :y -1}
                                 \D {:x 0 :y 1}
                                 \L {:x -1 :y 0}
                                 \R {:x 1 :y 0}}
        {delta-x :x delta-y :y} (unit-vectors direction)
        {old-x :x old-y :y}     location]
    {:x (+ old-x delta-x) :y (+ old-y delta-y)}))

(defn is-complete? [{:keys [location]}]
  (and (= (location :x) 3) (= (location :y) 3)))

(defn generate-next-steps [seed {:keys [path location]}]
  (let [seed-plus-path (apply str (concat seed path))
        hash           (md5 seed-plus-path)
        next-directions (->> hash
                             (get-unlocked-directions)
                             (remove-walls location))]
    (map (fn [direction]
           {:path (conj path direction)
            :location (move location direction)})
         next-directions)))

(defn part-1 [seed]
  (loop [state [{:path [] :location {:x 0 :y 0}}]]
    (let [_ (println state)
          complete-paths (filter is-complete? state)]
      (if (empty? state)
        ":("
        (if-not (empty? complete-paths)
          complete-paths
          (recur (mapcat #(generate-next-steps seed %) state)))))))

;; (part-1 example-input)
;; (part-1 puzzle-input)

(defn part-2-one-step [seed state longest-path-length length]
  (let [next-steps       (mapcat #(generate-next-steps seed %) state)
        complete-paths   (filter is-complete? next-steps)
        new-longest-path (if (empty? complete-paths)
                           longest-path-length
                           length)
        new-state       (remove is-complete? next-steps)]
    [new-state new-longest-path (inc length)]))

(defn part-2 [seed]
  (loop [state               [{:path [] :location {:x 0 :y 0}}]
         longest-path-length 0
         length              0]
    (if (empty? state)
      longest-path-length
      (let [_                (println (str "paths: " (count state) " length: " length " longest-path-length: " longest-path-length))
            [new-state new-longest-path new-length] (part-2-one-step seed state longest-path-length length)]
        (recur new-state new-longest-path new-length)))))

;; (part-2 "ihgpwlah")

;; (part-2-one-step "ihgpwlah" [{:path [\D \D \R \R \R] :location {:x 3 :y 2}}] 0 5)
