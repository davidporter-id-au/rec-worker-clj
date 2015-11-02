(ns worker.write
  (:require [clojure.data.json :as json])
  (:require [clojure.core.async :as async])
  (:use [amazonica.aws.dynamodbv2]))

(def cred {:endpoint "ap-southeast-2"})

(defn get-block-from-channel
  "Pulls elements off the internal channel"
  [channel block-size]
  ;25 Items is the maximum supported by dynamodb for batch operations
  (async/<!! (async/into [] (async/take block-size channel))))

(defn database-client [table-name batch]
    ; Write to the table the batch block
    (batch-write-item cred :request-items { table-name batch }))

(defn process-block-and-write
  "Takes a single batch of lines off the internal channel
  and pipelines them to the database"
  [channel dynamo-table-name block-size]
  (let
      [write-to-dynamo! (partial database-client dynamo-table-name)
       format-records (partial map (fn [record] { :put-request { :item record }}))]
    (->
      ; Pull from the internal queue
     (get-block-from-channel channel block-size)
      ; Parse the collection
      ((partial map json/read-str))
      ;Format the records
      format-records
      ; ..and write
      write-to-dynamo!)))

(defn write-thread!
  "Main operation - infinite loop on a thread.
   Blocks, waiting, when nothing remains to process"
  [thread-number channel dynamo-table-name block-size]
    (println (str "starting thread " thread-number))
    (future
      (while true
        (process-block-and-write channel dynamo-table-name block-size)
        (println (str "completed write of batch - thread " thread-number)))))
