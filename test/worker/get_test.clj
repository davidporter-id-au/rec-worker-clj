(ns worker.get-test
  (:require [worker.get :as sut])
  (:require [clojure.data.json :as json])
  (:require [clojure.core.async :as async])
  (:use [clojure.test]))

(deftest create-a-sequence
  (testing "That the incoming stream creates a lazy sequence of JSON lines"
    (let [jsonlines-seq
          (sut/create-sequence "mis-table-export" "Ripley/recommendations_448_20151101_1000.json.gz")]
      (is (seq? jsonlines-seq))
      (is (= 33030144  ((json/read-str (first jsonlines-seq)) "seekerId"))))))


