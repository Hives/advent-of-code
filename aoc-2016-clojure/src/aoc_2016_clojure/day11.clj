(ns aoc-2016-clojure.day11)

(def max-floor 3)
(def min-floor 0)

(defn get-microchips [floor]
  (->> floor
    (filter #(= :microchip (first %)))
    (map second)))

(defn get-generators [floor]
  (->> floor
    (filter #(= :generator (first %)))
    (map second)))

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

(defn find-floor-of [floors item]
  (let [floors-indexed (map-indexed (fn [idx itm] [idx itm]) floors)]
    (->> floors-indexed
      (filter (fn [[_ floor]] (some #{item} floor)))
      (first)
      (first))))

(defn get-equipment-floors [floors element]
  [(find-floor-of floors [:microchip element])
   (find-floor-of floors [:generator element])
   ])

(defn add-fingerprint [state]
  (let [{elevator :elevator
         floors   :floors} state
        fingerprint (->> floors
                      (reduce concat)
                      (map second)
                      (set)
                      (map #(get-equipment-floors floors %))
                      (cons [elevator])
                      (sort))]
    {:elevator    elevator
     :floors      floors
     :fingerprint fingerprint}))

(defn distinct-by-fingerprint [states]
  (->> states
    (group-by :fingerprint)
    (vals)
    (map first)))

(defn go-one-step [current-states previous-fingerprints]
  (let [non-looping-next-states (->> current-states
                                  (mapcat get-valid-moves)
                                  (set)
                                  (map add-fingerprint)
                                  (distinct-by-fingerprint)
                                  (filter #(not-any? #{(:fingerprint %)} previous-fingerprints)))]
    [non-looping-next-states (clojure.set/union previous-fingerprints (set (map :fingerprint current-states)))]))

(defn foo [initial]
  (loop [current-states        #{(add-fingerprint initial)}
         previous-fingerprints #{}
         counter               0]
    (if (some state-complete? current-states)
      counter
      (let [[new-states new-previous-fingerprints] (go-one-step current-states previous-fingerprints)
            _ (println (str "count: " counter))
            _ (println (str "states: " (count new-states)))
            _ (println (str "previous states: " (count new-previous-fingerprints)))]
        (recur
          new-states
          new-previous-fingerprints
          (inc counter))))))

(def test-initial-state
  {:elevator 0
   :floors   [#{[:microchip :hydrogen] [:microchip :lithium]}
              #{[:generator :hydrogen]}
              #{[:generator :lithium]}
              #{}]})

(def test-initial-state-2
  {:elevator 0
   :floors   [#{[:microchip :hydrogen] [:microchip :lithium]}
              #{[:generator :lithium]}
              #{[:generator :hydrogen]}
              #{}]})

(def puzzle-initial-state
  {:elevator 0
   :floors   [#{[:generator :thulium] [:microchip :thulium] [:generator :plutonium] [:generator :strontium]}
              #{[:microchip :plutonium] [:microchip :strontium]}
              #{[:generator :promethium] [:microchip :promethium] [:generator :ruthenium] [:microchip :ruthenium]}
              #{}]})

(def puzzle-initial-state-2
  {:elevator 0
   :floors   [#{[:generator :thulium] [:microchip :thulium] [:generator :plutonium] [:generator :strontium] [:generator :elerium] [:microchip :elerium] [:generator :dilithium] [:microchip :dilithium]}
              #{[:microchip :plutonium] [:microchip :strontium]}
              #{[:generator :promethium] [:microchip :promethium] [:generator :ruthenium] [:microchip :ruthenium]}
              #{}]})

(time
  (foo puzzle-initial-state-2)
  )