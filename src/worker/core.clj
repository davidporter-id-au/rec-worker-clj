(ns worker.core
  (:require [worker.write :as db])
  (:require [clojure.core.async :as async])
  (:require [worker.get :as reader]))

;Create a channel with a buffer for 1000 items
(def recommendations-channel (async/chan 1000))

(defn put-recommendations-onto-channel
  "Takes a lazy sequence and places it onto the channel"
  [channel sequence]
  ;Go through the lazy sequence and place the lines onto a channel
  (doseq [s sequence] (async/>!! channel s)))

(defn -main [& args]

  (if (not (= 3 (count args)))
    (do
      (println "Usage: lein run <s3 bucket> <s3 key> <dynamo table>")
      (System/exit 1)))

  (def bucket (nth args 0))
  (def object-key (nth args 1))
  (def dynamo-table (nth args 2))

  ;Kick off reading actions and putting them into the queue
  (future
    (put-recommendations-onto-channel
     recommendations-channel
     (reader/create-sequence bucket object-key)))

  ;Create a batch of reader threads and pull off to write to dynamodb
  (dorun (for [t (range 12)] (db/write-thread! t recommendations-channel dynamo-table 25))))
