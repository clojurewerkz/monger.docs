---
title: "Monger, a Clojure MongoDB client: deleting documents | MongoDB library for Clojure"
layout: article
---

## About this guide

This guide covers:

 * Deleting documents with Monger
 * Deleting a single document vs multiple documents

This work is licensed under a <a rel="license" href="http://creativecommons.org/licenses/by/3.0/">Creative Commons Attribution 3.0 Unported License</a> (including images & stylesheets). The source is available [on Github](https://github.com/clojurewerkz/monger.docs).


## What version of Monger does this guide cover?

This guide covers Monger 3.0 (including preview releases).


## How to Remove Documents with Monger

Documents are removed using `monger.collection/remove` function that
takes a collection name and conditions:

``` clojure
(ns my.service.server
  (:require [monger.core :as mg]
            [monger.collection :as mc])
  (:import [org.bson.types.ObjectId]))


(let [conn (mg/connect)
      db   (mg/get-db conn "monger-test")
      coll "documents"]
  ;; insert a few documents
  (mc/insert db coll { :language "English" :pages 38 })
  (mc/insert db coll { :language "Spanish" :pages 78 })
  (mc/insert db coll { :language "Unknown" :pages 87 })
  
  ;; remove multiple documents
  (mc/remove db coll { :language "English" })
  
  ;; remove ALL documents in the collection
  (mc/remove db coll))
```


## Removing a Single Document By Id

`monger.collection/remove-by-id` is useful when document id is known:

``` clojure
(ns my.service.server
  (:require [monger.core :as mg]
            [monger.collection :as mc])
  (:import org.bson.types.ObjectId))

(let [conn (mg/connect)
      db   (mg/get-db conn "monger-test")
      coll "documents"]
  ;; remove document by id
  (let [oid (ObjectId.)]
    (mc/insert db coll { :language "English" :pages 38 :_id oid })
    (mc/remove-by-id db coll oid)))
```


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
