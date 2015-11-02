(ns worker.get
  (:require [aws.sdk.s3 :as s3])
  (:require [clojure.core.async :as async])
  (:require [clojure.java.io :as io])
  (:use [amazonica.aws.s3])
  (:import [java.util.zip GZIPInputStream]))

(defn create-sequence [bucket s3-key]
  (->
   (get-object bucket s3-key)
   :object-content
   (java.util.zip.GZIPInputStream.)
   io/reader
   line-seq))
