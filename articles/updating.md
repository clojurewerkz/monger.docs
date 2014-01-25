---
title: "Monger, a Clojure MongoDB client: updating documents | MongoDB library for Clojure"
layout: article
---

## About this guide

This guide covers:

 * Updating documents with Monger
 * Using atomic operations with Monger
 * Upserting documents
 * Updating a single document vs multiple documents
 * Overriding default write concern for individual operations
 * Working with multiple databases

This work is licensed under a <a rel="license" href="http://creativecommons.org/licenses/by/3.0/">Creative Commons Attribution 3.0 Unported License</a> (including images & stylesheets). The source is available [on Github](https://github.com/clojurewerkz/monger.docs).


## What version of Monger does this guide cover?

This guide covers Monger 1.7 (including beta releases).


## Overview

Monger's update API follows the following simple rule: the "syntax" for condition and update document structure is
the same or as close as possible to MongoDB shell and the official drivers. In addition, Monger provides several
convenience functions for common cases, for example, finding documents by id.


## Updating multiple documents

`monger.collection/update` is the most commonly used way of updating documents. It takes a collection name, a query condition, an updated document
and a number of options, the most
commonly used of which is `:multi`. When `:multi` is set to `true`, it instructs MongoDB to update multiple documents
that match the query.

``` clojure
(ns my.service
  (:require [monger.collection :as mc]))

;; resets the entire score table by updating all documents to {"score": 0}
(mc/update "scores" {} {:score 0} :multi true)

;; resets only those rows the entire score table where round equals 3
(mc/update "scores" {:round 3} {:score 0} :multi true)
```


### Alternative API for Working with Multiple Databases

`monger.multi.collection/update` is a twin sister of `monger.collection/update` which takes
a database as its first argument instead of relying on `monger.core/*mongodb-database*`.



## Updating a single document

`monger.collection/update` can be instructed to only update a single document by passing `:multi` option with the value of `false`:

``` clojure
(ns my.service
  (:require [monger.collection :as mc]))

;; updates only one document
(mc/update "scores" {:state "idle"} {:score 0} :multi false)
```


`monger.collection/update-by-id` is useful when document id is known:

``` clojure
(ns my.service
  (:require [monger.collection :as mc]))

;; updates score for player "sam" by a known id
(mc/update-by-id "scores" oid {:score 1088})
```

### Alternative API for Working with Multiple Databases

`monger.multi.collection/update-by-id` is a twin sister of `monger.collection/update-by-id` which takes
a database as its first argument instead of relying on `monger.core/*mongodb-database*`.



## Overriding Write Concern

It is possible to override default write concern for a single operation using the `:write-concern` key that `monger.collection/update` and
`monger.collection/update-by-id` accept:

``` clojure
(ns my.service
  (:require [monger.collection :as mc])
  (:import com.mongodb.WriteConcern))

;; updates score for player "sam", waiting for MongoDB server to
;; update recovery journal (trading some throughput for safety)
(mc/update "scores" {:username "sam"} {:score 1088} :multi true :write-concern WriteConcern/JOURNAL_SAFE)
```


## Upserts

MongoDB supports upserts, "update or insert" operations. To do an upsert with Monger, use `monger.collection/update` function with `:upsert` option set to true:

``` clojure
(ns my.service
  (:require [monger.collection :as mc]))

;; updates score for player "sam" if it exists; creates a new document otherwise
(mc/update "scores" {:player "sam"} {:score 1088} :upsert true)
```

Note that upsert only inserts one document. Learn more about upserts
in [this MongoDB documentation
section](http://docs.mongodb.org/manual/core/update/#Updating-update).

### Alternative API for Working with Multiple Databases

`monger.multi.collection/upsert` is a twin sister of `monger.collection/upsert` which takes
a database as its first argument instead of relying on `monger.core/*mongodb-database*`.


## Atomic Modifiers

### Overview

Modifier operations are highly-efficient and useful when updating existing values; for instance, they're great for incrementing counters, setting individual fields, updating fields that are arrays and so on.

MongoDB supports modifiers via update operation and Monger API works the same way: you pass a document with modifiers
to `monger.collection/update`. For example, to increment number of views for a particular page:

``` clojure
(ns my.service
  (:require [monger.collection :as mc])
  (:use monger.operators))

(mc/update "visits" {:url "http://megacorp.com"} {$inc {:visits 1}})
```

Whenever you have a MongoDB shell example, query and update documents
will have the same structure with Monger. Monger provides a number of
macros in the `monger.operators` namespace to make your code look a
little bit cleaner: for example, instead of `"$set"` you can use
`$set` and it will be expanded to `"$set"` when your Clojure code is
compiled (at the macro expansion time).

Atomic modifiers are documented in detail in this [MongoDB
documentation
guide](http://docs.mongodb.org/manual/tutorial/isolate-sequence-of-operations/). Below
we will demonstrate eachsome of them with (very short) code examples,
but using them in general is identical to how you use them in the
MongoDB shell.


### Using $inc operator

``` clojure
(ns my.service
  (:require [monger.collection :as mc])
  (:use monger.operators))

(mc/update "visits" {:url "http://megacorp.com"} {$inc {:visits 1}})
```


### Using $set operator

`$set` operator changes (sets) one or more fields for a document (or multiple documents)

``` clojure
(ns my.service
  (:require [monger.collection :as mc])
  (:use monger.operators))

;; sets "weight" field in the document to 20.5
(mgcol/update coll {:_id oid} {$set {:weight 20.5}})

;; sets several fields atomically
(mgcol/update coll {:_id oid} {$set {:weight 20.5 :color "blue" :width 10.75}})
```


### Using $unset operator

`$unset` operator clears (removes) a single field from a document

``` clojure
(ns my.service
  (:require [monger.collection :as mc])
  (:use monger.operators))

(mc/update "visits" {:url "http://megacorp.com"} {$unset "unverified"})
```


### Using $push operator

`$push` operator appends a single value to an array field (and allows for duplicates):

``` clojure
(ns my.service
  (:require [monger.collection :as mc])
  (:use monger.operators))

;; adds "überachievement" to the list of badges (which an array field)
(mgcol/update "people" {:_id oid} {$push {:badges "überachievement"}})
```


### Using $pushAll operator

`$pushAll` operator adds multiple values to an array field (and allows for duplicates):

``` clojure
(ns my.service
  (:require [monger.collection :as mc])
  (:use monger.operators))

;; adds multiple items to an array field
(mgcol/update coll {:_id oid} {$pushAll {:items ["Glass Star" "See No Evil"]}})
```


### Using $addToSet operator

`$addToSet` operator is similar to `$push` but will filter out duplicate entries (sets are collections
that do not have duplicates):

``` clojure
(ns my.service
  (:require [monger.collection :as mc])
  (:use monger.operators))

;; adds a single value to an array field, ensuring there are no duplicates
(mgcol/update coll {:_id oid} {$addToSet {:permissions ["write"]}})
```


### Using $pull operator

``` clojure
(ns my.service
  (:require [monger.collection :as mc])
  (:use monger.operators))

;; removes a single value from an array field
(mgcol/update coll {:_id oid} {$pull {:permissions "write"}})
```


### Using $pullAll operator

``` clojure
(ns my.service
  (:require [monger.collection :as mc])
  (:use monger.operators))

;; removes multiple items from an array field
(mgcol/update coll {:_id oid} {$pullAll {:items ["Glass Star" "See No Evil"]}})
```


## Using Save Operation

`monger.collection/save` function performs insert or update (replace) based on whether or not provided document already has an id (whether the `:_id` field
is set).

With a new document, it works very much like `monger.collection/insert`:

``` clojure
(let [collection "people"
      document       {:name "Joe" :age 30}]
    ;; here monger.collection/save works the same way as monger.collection/insert
    (monger.collection/save "people" document))
```

`monger.collection/save-and-return` with new documents works the same way as `monger.collection/insert-and-return`:

``` clojure
(let [collection "people"
      document       {:name "Joe" :age 30}]
    ;; here monger.collection/save-and-return works the same way as monger.collection/insert-and-return
    (monger.collection/save-and-return "people" document))
```

With documents that are not new (already have the `:_id` field), `monger.collection/save` and `monger.collection/save-and-return`
will first look up the existing document by id and replace it with the one provided:

``` clojure
(let [document   (mc/insert-and-return "people" {:name "Joe" :username "sadjoe" :age 30})
      doc-id     (:_id document)]
    ;; finds and updates a document by _id because it is present
    (mc/save-and-return "people" { :_id doc-id :name "Joe" :age 30 :username "happyjoe" }))
```

### Alternative API for Working with Multiple Databases

`monger.multi.collection/save` is a twin sister of `monger.collection/save` which takes
a database as its first argument instead of relying on `monger.core/*mongodb-database*`.


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



## What To Read Next

The documentation is organized as [a number of guides](/articles/guides.html), covering all kinds of topics.

We recommend that you read the following guides first, if possible, in this order:

 * [Deleting documents](/articles/deleting.html)
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
