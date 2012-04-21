(ns my.service.server
  (:require [monger core]]))

(monger.core/connect!)
(monger.core/set-db! (monger.core/get-db "monger-test"))

(monger.core/set-default-write-concern! WriteConcern/FSYNC_SAFE)