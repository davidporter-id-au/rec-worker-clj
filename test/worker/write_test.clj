(ns worker.write-test
  (:require [worker.write :as sut])
  (:require [clojure.core.async :as async])
  (use [clojure.test]))


(def ch (async/chan 200))

(defn setup [cb]
  (do
    (doall (map (fn [i] (async/>!! ch i)) (range 100)))
    (cb)))

(use-fixtures :each setup)

(deftest get-block-from-channel
  (testing "That, when given a channel filled with n number of single items,
the function is able to convert that to a concrete vector of 25 items"
(is (= 25 (count (sut/get-block-from-channel ch 25))))))
