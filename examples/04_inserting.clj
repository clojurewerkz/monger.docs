(ns my.service.server
  (:use [monger.core :only [connect! connect set-db! get-db]]
        [monger.collection :only [insert insert-batch]])
  (:import [org.bson.types ObjectId]
           [com.mongodb DB WriteConcern]))

;; localhost, default port
(connect!)
(set-db! (monger.core/get-db "monger-test"))

;; without document id
(insert "document" { :first_name "John" :last_name "Lennon" })

;; multiple documents at once
(insert-batch "document" [{ :first_name "John" :last_name "Lennon" }
                          { :first_name "Paul" :last_name "McCartney" }])

;; with explicit document id
(insert "documents" { :_id (ObjectId.) :first_name "John" :last_name "Lennon" })

;; with a different write concern
(insert "documents" { :_id (ObjectId.) :first_name "John" :last_name "Lennon" } WriteConcern/JOURNAL_SAFE)

;; with a different database
(let [archive-db (get-db "monger-test.archive")]
  (insert archive-db "documents" { :first_name "John" :last_name "Lennon" } WriteConcern/NORMAL))
