(ns aoc-2016-clojure.day04-spec
  (:require [speclj.core :refer :all]
            [aoc-2016-clojure.day04 :refer :all]))

(describe "parse"
  (it "can parse an input string"
    (should=
      ["aaaaa-bbb-z-y-x" 123 "abxyz"]
      (parse "aaaaa-bbb-z-y-x-123[abxyz]"))))

(describe "generate-checksum"
  (it "can generate the checksum for an encrypted name"
    (should=
      "abxyz"
      (generate-checksum "aaaaa-bbb-z-y-x")))
  (it "can generate the checksum for another encrypted name"
    (should=
      "abcde"
      (generate-checksum "a-b-c-d-e-f-g-h")))
  (it "and another one"
    (should=
      "oarel"
      (generate-checksum "not-a-real-room"))))

(describe "validate"
  (it "can validate a real room"
    (let [input (parse "aaaaa-bbb-z-y-x-123[abxyz]")]
      (should= true (validate input))))
  (it "can validate a real room with lots of tied letters"
    (let [input (parse "a-b-c-d-e-f-g-h-987[abcde]")]
      (should= true (validate input))))
  (it "can validate another real room"
    (let [input (parse "not-a-real-room-404[oarel]")]
      (should= true (validate input))))
  (it "can validate a fake room"
    (let [input (parse "totally-real-room-200[decoy]")]
      (should= false (validate input)))))

(describe "letter-to-number"
  (it "converts a to 0"
    (should= 0 (letter-to-number \a)))
  (it "converts z to 25"
    (should= 25 (letter-to-number \z))))

(describe "number-to-letter"
  (it "converts 0 to a"
    (should= \a (number-to-letter 0)))
  (it "converts 25 to z"
    (should= \z (number-to-letter 25))))

(describe "shift-letter"
  (it "can shift a by 10"
    (should= \k (shift-letter \a 10)))
  (it "can shift to z"
    (should= \z (shift-letter \y 1)))
  (it "can shift round the clock"
    (should= \a (shift-letter \z 1))))

(describe "decrypt"
  (it "can decrypt an encrypted name"
    (should=
      "very encrypted name"
      (decrypt "qzmt-zixmtkozy-ivhz" 343))))