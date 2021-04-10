(ns aoc-2016-clojure.day11)

(def max-floor 3)
(def min-floor 0)
(def a-floor '([:h :microchip] [:h :generator] [:l :microchip]))

(defn get-microchips [floor]
  (->> floor
    (filter #(= :microchip (second %)))
    (map first)))

(defn get-generators [floor]
  (->> floor
    (filter #(= :generator (second %)))
    (map first)))

(defn is-valid-floor? [floor]
  (let [microchips           (get-microchips floor)
        generators           (get-generators floor)
        unpowered-microchips (remove #(.contains generators %) microchips)]
    (or
      (empty? unpowered-microchips)
      (empty? generators))))

(defn is-valid-state? [state]
  (let [{floors :floors} state]
    (every? is-valid-floor? floors)))

(defn all-pairs [coll]
  (loop [[x & xs] (vec coll)
         acc []]
    (if (nil? xs)
      acc
      (recur xs (apply conj acc (map #(list x %) xs))))))

(defn select-one-or-two [coll]
  (concat (map list coll) (all-pairs coll)))

(defn remove-items-from-lists [items lists]
  (map
    (fn [list]
      (remove #(some #{%} items) list))
    lists))

(defn move-equipment-to-floor [state equipment new-floor-num]
  (let [{floors :floors} state
        new-floors (->> floors
                     (remove-items-from-lists equipment)
                     (map-indexed
                       (fn [index floor]
                         (if (= index new-floor-num)
                           (concat floor equipment)
                           floor)))
                     (map set)
                     (vec))]
    {:elevator new-floor-num
     :floors   new-floors}))

(defn get-potential-next-floors [current-floor]
  (filter
    #(and (>= % min-floor) (<= % max-floor))
    (map #(+ current-floor %) '(-1 1))))

(defn get-next-states [state]
  (let [{current-floor-num :elevator
         floors            :floors} state
        current-floor                  (get floors current-floor-num)
        potential-equipment            (select-one-or-two current-floor)
        potential-floors               (get-potential-next-floors current-floor-num)
        potential-equipment-and-floors (mapcat (fn [floor]
                                                 (map (fn [equipment]
                                                        [equipment floor])
                                                   potential-equipment))
                                         potential-floors)]
    (map
      (fn [[equipment floor]]
        (move-equipment-to-floor state equipment floor))
      potential-equipment-and-floors)))

(defn get-valid-moves [state]
  (->> state
    (get-next-states)
    (filter is-valid-state?)))

(defn state-complete? [state]
  (let [{floors :floors} state
        [first-floor second-floor third-floor] floors]
    (and
      (empty? first-floor)
      (empty? second-floor)
      (empty? third-floor))))

(defn history-complete? [history]
  (state-complete? (first history)))

(def test-initial-state
  {:elevator 0
   :floors   [#{[:hydrogen :microchip] [:lithium :microchip]}
              #{[:hydrogen :generator]}
              #{[:lithium :generator]}
              #{}]})

(def puzzle-initial-state
  {:elevator 0
   :floors   [#{[:thulium :generator] [:thulium :microchip] [:plutonium :generator] [:strontium :generator]}
              #{[:plutonium :microchip] [:strontium :microchip]}
              #{[:promethium :generator] [:promethium :microchip] [:ruthenium :generator] [:ruthenium :microchip]}
              #{}]})

(defn go-one-step [current-states previous-states]
  (let [next-states             (set (mapcat get-valid-moves current-states))
        non-looping-next-states (filter #(not-any? #{%} previous-states) next-states)]
    [non-looping-next-states (clojure.set/union previous-states (set current-states))]))

(defn foo [initial]
  (loop [current-states  #{initial}
         previous-states #{}
         counter         0]
    (if (> counter 40)
      current-states
      (if (some state-complete? current-states)
       counter
       (let [[new-states new-previous-states] (go-one-step current-states previous-states)
             _ (println (str "count: " counter))
             _ (println (str "states: " (count new-states)))
             _ (println (str "previous states: " (count new-previous-states)))]
         (recur
           new-states
           new-previous-states
           (inc counter)))))))

(time
  (foo test-initial-state)
  )