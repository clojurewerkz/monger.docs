(ns my.service.server
  (:use [monger.core :only [connect! connect set-db! get-db]]
        [monger.collection :only [insert update update-by-id remove-by-id] :as mc])
  (:import [org.bson.types ObjectId]
           [com.mongodb DB WriteConcern]))

;; localhost, default port
(connect!)
(set-db! (monger.core/get-db "monger-test"))

;; insert a few documents
(insert "documents" { :language "English" :pages 38 })
(insert "documents" { :language "Spanish" :pages 78 })
(insert "documents" { :language "Unknown" :pages 87 })

;; remove multiple documents
(mc/remove "documents" { :language "English" })

;; remove ALL documents in the collection
(mc/remove "documents")

;; with a different database
(let [archive-db (get-db "monger-test.archive")]
  (mc/remove archive-db "documents" { :readers 0 :pages 0 }))


;; remove document by id
(let [oid (ObjectId.)]
  (insert "documents" { :language "English" :pages 38 :_id oid })
  (remove-by-id "documents" oid))
