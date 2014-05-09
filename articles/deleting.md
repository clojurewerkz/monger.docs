---
title: "Monger, a Clojure MongoDB client: deleting documents | MongoDB library for Clojure"
layout: article
---

## About this guide

This guide covers:

 * Deleting documents with Monger
 * Deleting a single document vs multiple documents
 * Working with multiple databases

This work is licensed under a <a rel="license" href="http://creativecommons.org/licenses/by/3.0/">Creative Commons Attribution 3.0 Unported License</a> (including images & stylesheets). The source is available [on Github](https://github.com/clojurewerkz/monger.docs).


## What version of Monger does this guide cover?

This guide covers Monger 1.8 (including beta releases).


## How to Remove Documents with Monger

Documents are removed using `monger.collection/remove` function that takes a collection name and conditions:

``` clojure
(ns my.service.server
  (:require [monger.collection :as mc]
            [monger.core :refer [connect! connect set-db! get-db]])
  (:import [org.bson.types ObjectId]))

;; localhost, default port
(connect!)
(set-db! (monger.core/get-db "monger-test"))

;; insert a few documents
(mc/insert "documents" { :language "English" :pages 38 })
(mc/insert "documents" { :language "Spanish" :pages 78 })
(mc/insert "documents" { :language "Unknown" :pages 87 })

;; remove multiple documents
(mc/remove "documents" { :language "English" })

;; remove ALL documents in the collection
(mc/remove "documents")
```


## Removing a Single Document By Id

`monger.collection/remove-by-id` is useful when document id is known:

``` clojure
(ns my.service.server
  (:require [monger.core :refer [connect! connect set-db! get-db]]
            [monger.collection :refer [insert remove-by-id] :as mc])
  (:import [org.bson.types ObjectId]))

;; localhost, default port
(connect!)
(set-db! (monger.core/get-db "monger-test"))

;; remove document by id
(let [oid (ObjectId.)]
  (insert "documents" { :language "English" :pages 38 :_id oid })
  (remove-by-id "documents" oid))
```



## Working With Multiple Databases

Monger is optimized for applications that use only one database but it
is possible to work with multiple ones.

For that, use functions in the `monger.multi.collection` namespace: they mirror
`monger.collection` but take a database as the first argument.


It is also possible to use [clojure.core/binding](http://clojuredocs.org/clojure_core/clojure.core/binding)
to rebind `monger.core/*mongodb-database*`,
`monger.core/*mongodb-connection*` and `monger.core/*mongodb-gridfs*`
vars to different values or use convenience functions that do that:
`monger.core/with-connection`, `monger.core/with-db`,
`monger.core/with-gridfs`. This is a common practice for Clojure
libraries. Remember that var bindings are thread-local.



## What to read next

The documentation is organized as [a number of guides](/articles/guides.html), covering all kinds of topics.

We recommend that you read the following guides first, if possible, in this order:

 * [Indexing and other collection operations](/articles/collections.html)
 * [Integration with 3rd party libraries](/articles/integration.html)
 * [Map/Reduce](/articles/mapreduce.html)
 * [GridFS support](/articles/gridfs.html)
 * [Using MongoDB Aggregation Framework](/articles/aggregation.html)
 * [Using MongoDB commands](/articles/commands.html)

## Tell Us What You Think!

Please take a moment to tell us what you think about this guide on
Twitter or the [Monger mailing
list](https://groups.google.com/forum/#!forum/clojure-mongodb)

Let us know what was unclear or what has not been covered. Maybe you
do not like the guide style or grammar or discover spelling
mistakes. Reader feedback is key to making the documentation better.
