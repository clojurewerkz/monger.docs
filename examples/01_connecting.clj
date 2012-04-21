(ns my.service.server
  (:require [monger.core :as mg])
  (:import [com.mongodb MongoOptions ServerAddress]))

;; localhost, default port
(monger.core/connect!)

;; given host, given port
(monger.core/connect! { :host "db.megacorp.internal" :port 7878 })


;; given host, given port
(monger.core/connect! { :host "db.megacorp.internal" :port 7878 })

;; using MongoOptions allows fine-tuning connection parameters,
;; like automatic reconnection (highly recommended for production environment)
(let [^MongoOptions opts (mg/mongo-options :threads-allowed-to-block-for-connection-multiplier 300)
      ^ServerAddress sa  (mg/server-address "127.0.0.1" 27017)]
  (mg/connect! sa opts))
