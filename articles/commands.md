---
title: "Monger, a Clojure MongoDB client: Using MongoDB commands | MongoDB library for Clojure"
layout: article
---

## About this guide

This guide covers:

 * Using MongoDB commands
 * Using auxilliary functions that help with database administration


This work is licensed under a <a rel="license" href="http://creativecommons.org/licenses/by/3.0/">Creative Commons Attribution 3.0 Unported License</a> (including images & stylesheets). The source is available [on Github](https://github.com/clojurewerkz/monger.docs).


## What version of Monger does this guide cover?

This guide covers Monger 2.0 (including preview releases).


## Overview

The MongoDB command interface provides access to all non CRUD database
operations. Fetching server stats, initializing a replica set, and
running a map-reduce job are all accomplished by running a command.

You specify a command first by constructing a standard BSON document
whose first key is the name of the command. For example, specify the
isMaster command using the following BSON document (demonstrated here
as an ordered Clojure map):

``` clojure
(sorted-map "isMaster" 1)
```

Some MongoDB commands are version-specific. Some implement
sophisticated features, for example, the Aggregation Framework
support, and some are administrative in nature, like reindexing a
collection.

With Monger, you use `monger.core/command` function to run
commands. `monger.result/ok?` can be used to determine if a command
succeeded or not, `monger.conversion/from-db-object` is used to
convert a command results to a Clojure map. Here is an example that
demonstrates all three:

``` clojure
(ns monger.docs.examples
  (:require [monger.core :as mg]
            [monger.result :refer [ok?]]
            [monger.conversion :refer [from-db-object]]))

(let [conn       (mg/connect)
      db         (mg/get-db conn "monger-test")
      raw-result (mg/command db (sorted-map :isMaster 1))
      result     (from-db-object raw-result true)]
  ;= true
  (ok? raw-result)
  ;; {:serverUsed 127.0.0.1:27017, :ismaster true, :maxBsonObjectSize 16777216, :ok 1.0}
  (println result))
```

[MongoDB command
reference](http://docs.mongodb.org/manual/reference/commands/?highlight=commands)
lists available DB commands. Command document structure with Monger is
exactly the same as in the MongoDB shell and MongoDB manual guides. If
your command happens to include operators (`$gt`, `$lt`, `$regex`,
etc), you can use macros in the `monger.operators` namespace, just
like with queries.

Monger command API was designed to for flexibility: it lets developers
to use new commands as soon as MongoDB server supports them. However,
many commands are used more often than others and their API does not
change any more. Monger provides convenient functions for some of them
in the `monger.command` namespace.


## MongoDB Commands and Map Ordering

MongoDB command documents may depend on map ordering. So, when using
`monger.core/command` to execute commands Monger does not provide
helper functions for, make sure you use
[clojure.core/sorted-map](http://clojure.github.com/clojure/clojure.core-api.html#clojure.core/sorted-map)
instead of a map literal. Otherwise you may [start seeing "unknown
command"
errors](https://groups.google.com/forum/?fromgroups=#!topic/clojure-mongodb/IMEnskx6yXo).


## Common Commands

### Get Collection Stats

`monger.command/collection-stats` takes a collection names and returns
collection stats:

``` clojure
(ns monger.docs.examples
  (:require [monger.command :as cmd])
            [monger.conversion :refer [from-db-object]])

;; #<CommandResult { "count" : 107281 , "size" : 52210704 , "avgObjSize" : 486.67242102515826 , "storageSize" : 65224704 , "numExtents" : 9 , "nindexes" : 6 , "lastExtentSize" : 17399808 , "paddingFactor" : 1.0 , "flags" : 1 , "totalIndexSize" : 26187728, …, "ok" : 1.0}>
(cmd/collection-stats db "documents")

;; {:paddingFactor 1.0, :ok 1.0, …, :totalIndexSize 26187728, :count 107281, :avgObjSize 486.67242102515826, :lastExtentSize 17399808, :size 52210704, :storageSize 65224704, :flags 1, :nindexes 6, :numExtents 9}
(from-db-object (cmd/collection-stats db "documents") true)
```


### Get Database Stats

`monger.command/db-stats` is similar to `monger.command/collection-stats` but returns database stats:

``` clojure
(ns monger.docs.examples
  (:require [monger.command :as cmd]
            [monger.conversion :refer [from-db-object]]))

;= #<CommandResult { "serverUsed" : "127.0.0.1:27017" , "db" : "…" , "collections" : 25 , "objects" : 312807 , "avgObjSize" : 297.94926584123755 , "dataSize" : 93200616 , "storageSize" : 116150272 , "numExtents" : 53 , "indexes" : 37 , "indexSize" : 33088272 , "fileSize" : 469762048 , "nsSizeMB" : 16 , "ok" : 1.0}>
(cmd/db-stats db)

;= {:objects 312807, :collections 25, :nsSizeMB 16, :ok 1.0, :avgObjSize 297.94926584123755, :indexes 37, :storageSize 116150272, :fileSize 469762048, :dataSize 93200616, :serverUsed "127.0.0.1:27017", :numExtents 53, :db "…", :indexSize 33088272}
(from-db-object (cmd/db-stats db) true)
```


### Reindex a Collection

``` clojure
(ns monger.docs.examples
  (:require [monger.command :as cmd]
            [monger.conversion :refer [from-db-object]]))

(cmd/reindex-collection db "pages")
```


### Get Server Status

`monger.command/server-status` returns stats for the MongoDB server
Monger is connected to:

``` clojure
(ns monger.docs.examples
  (:require [monger.core :as mg]
            [monger.command :as cmd]))

(let [conn (mg/connect)
      db   (mg/get-db conn "monger-test")]
  (cmd/server-status db))
```


### Run "top" Command

`monger.command/top` provides programmatic access to the same
information `mongotop` command line tool outputs.

Specifically, it returns raw usage of each database, and provides
amount of time, in microseconds, used and a count of operations for
the following event types:

* total
* readLock
* writeLock
* queries
* getmore
* insert
* update
* remove
* commands

`monger.command/top` accepts a **connection** as its only argument.

## What to Read Next

Congratulations, this is the last guide. For the definitive list of
commands MongoDB supports, see [MongoDB command
reference](http://docs.mongodb.org/manual/reference/commands/?highlight=commands).

Take a look at [other guides](/articles/guides.html), they cover all kinds of topics.


## Tell Us What You Think!

Please take a moment to tell us what you think about this guide on
Twitter or the [Monger mailing
list](https://groups.google.com/forum/#!forum/clojure-mongodb)

Let us know what was unclear or what has not been covered. Maybe you
do not like the guide style or grammar or discover spelling
mistakes. Reader feedback is key to making the documentation better.
