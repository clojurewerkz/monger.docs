(ns my.service.server
  (:require [monger core]]))

;; localhost, default port
(monger.core/connect!)
(monger.core/set-db! (monger.core/get-db "monger-test"))
