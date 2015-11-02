(ns worker.core-test
  (:require [clojure.core.async :as async])
  (:use [clojure.test])
  (:require [worker.core :as sut]))

(def c (async/chan 3))

(defn setup [f]
  (do
    (sut/put-recommendations-onto-channel c [1 2 3])
    (f)))

(use-fixtures :each setup)

(deftest put-onto-channel
  (testing "That the incoming stream puts lines onto a channel"
    (is (= 1 (async/<!! c)))
    (is (= 2 (async/<!! c)))
    (is (= 3 (async/<!! c)))))
