(ns worker.db-integration-test
  (:require [worker.write :as sut])
  (:require [clojure.data.json :as json])
  (:use [amazonica.aws.dynamodbv2])
  (:require [clojure.core.async :as async])
  (use [clojure.test]))

(def ch (async/chan 200))
(def timestamp (quot (System/currentTimeMillis) 1000))
(def test-record (json/write-str {:seekerId 1234 :ts timestamp}))
(def cred {:endpoint "ap-southeast-2"})

(defn setup [cb]
  (do
    (async/>!! ch test-record)
    (sut/process-block-and-write ch "recommendations-resource-api" 1)
    (Thread/sleep 1000); Wait for Godot and eventual consistency
    (cb)))

(use-fixtures :each setup)

; Writes to the "magic" database id of 1234, get's it and compares the timestamp values
; Since the write is an upsert, it shouldn't matter if there's an existing record
(deftest db-integration
  (testing "that the end-to-end functionality with the database writing will work"
    (is (= timestamp (-> (get-item cred
                                :table-name "recommendations-resource-api"
                                :key {:seekerId {:n "1234"}}) :item :ts)))))
