(ns my.service.server
  (:use [monger.core :only [connect! connect set-db! get-db]]
        [monger.collection :only [insert update update-by-id]])
  (:import [org.bson.types ObjectId]
           [com.mongodb DB WriteConcern]))

;; localhost, default port
(connect!)
(set-db! (monger.core/get-db "monger-test"))

;; insert a few documents
(insert "documents" { :language "English" :pages 38 })
(insert "documents" { :language "Spanish" :pages 78 })
(insert "documents" { :language "Unknown" :pages 87 })

;; remove a document
(update "documents" { :language "Unknown" } { "$set" { :language "Spanish" } })
(update "documents" { :language "Unknown" } { "$inc" { :pages 3 :collaborators 2 } })
;; update multiple documents
(update "documents" { :language "English" } { "$set" { :year 1997 } } :multi true)


;; update document by id
(let [oid (ObjectId.)]
  (insert "documents" { :language "English" :pages 38 :_id oid })
  (update-by-id "documents" oid { "$set" { :language "Spanish" } }))
