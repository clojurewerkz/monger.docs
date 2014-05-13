---
title: "Monger, a Clojure MongoDB client: Using MongoDB Map/Reduce with Clojure"
layout: article
---

## About this guide

This guide covers:

 * Using Map/Reduce with Monger
 * Storing and loading JavaScript functions from classpath


This work is licensed under a <a rel="license" href="http://creativecommons.org/licenses/by/3.0/">Creative Commons Attribution 3.0 Unported License</a> (including images & stylesheets). The source is available [on Github](https://github.com/clojurewerkz/monger.docs).


## What version of Monger does this guide cover?

This guide covers Monger 2.0 (including preview releases).


## What version of MongoDB does this guide cover?

This guide covers MongoDB 2.0. Some features may be specific to MongoDB 2.2, but this guide tries to avoid them. If you are looking for
edge Map/Reduce support documentation, please refer to the [MongoDB guide on Map/Reduce](http://www.mongodb.org/display/DOCS/MapReduce).


## Overview

Map/Reduce is a programming model for processing large data sets
[popularized by
Google](http://research.google.com/archive/mapreduce.html) (see also
[Map/Reduce
revisited](http://userpages.uni-koblenz.de/~laemmel/MapReduce/paper.pdf)).

Map/reduce in MongoDB is useful for batch processing of data and
aggregation operations. It is similar in spirit to using something
like Hadoop with all input coming from a collection and output going
to a collection. Often, in a situation where you would have used GROUP
BY in SQL, map/reduce is often the right tool in MongoDB.

In MongoDB, a Map/Reduce query consists of an input collection, a
*mapper function*, a *reducer function*, a name of the output
collection (where results will be inserted) and an *output type* that
controls how Map/Reduce calculation results should be combined with
the existing documents in the output collection. The mapper and
reducer functions are in JavaScript and can be written inline (passed
as strings) or read from classpath using a helper function.


### Map/Reduce vs the Aggregation Framework

MongoDB 2.2 also supports a more focused, less generic and easier to
use data processing feature called the [Aggregation
Framework](/articles/aggregation.html) which makes raw map/reduce a
relatively low-level facility.


## Performing MongoDB Map/Reduce queries with Clojure

`monger.collection/map-reduce` is the function used to run Map/Reduce
queries with Monger. It takes a collection name, two JavaScript
functions as strings (typically loaded from JVM classpath), a
destination collection and one of the output type values (a
`com.mongodb.MapReduceCommand$OutputType` instance):

``` clojure
(ns monger.docs.examples
  (:require [clojurewerkz.support.js :as js]
            [monger.core :refer [command]]
            [monger.collection :refer [map-reduce]]
            [monger.result :refer [ok?]]
            [monger.conversion :refer [from-db-object]])
  (:import [com.mongodb MapReduceCommand$OutputType MapReduceOutput]))

;; performs a map/reduce query using functions stored in mapper.js and reducer.js
;; on the classpath. The result will be returned "inline" (as a collection of documents back to the client).
(let [output  (mc/map-reduce "events" (js/load-resource "mr/mapper.js") (js/load-resource "mr/reducer.js") "map_reduce_results" MapReduceCommand$OutputType/MERGE {})
      result  (from-db-object ^DBObject (.results ^MapReduceOutput output) true))]
  (println (ok? output))
  (println result))
```

It is also possible to return results to the client (as "inline output"):

``` clojure
(ns monger.docs.examples
  (:require [clojurewerkz.support.js :as js]
            [monger.core :refer [command]]
            [monger.collection :refer [map-reduce]]
            [monger.result :refer [ok?]]
            [monger.conversion :refer [from-db-object]])
  (:import [com.mongodb MapReduceCommand$OutputType MapReduceOutput]))

;; performs a map/reduce query using functions stored in mapper.js and reducer.js
;; on the classpath. The result will be returned "inline" (as a collection of documents back to the client).
(let [output  (mc/map-reduce "events" (js/load-resource "mr/mapper.js") (js/load-resource "mr/reducer.js") nil MapReduceCommand$OutputType/INLINE {})
      result  (from-db-object ^DBObject (.results ^MapReduceOutput output) true))]
  (println (ok? output))
  (println result))
```

Learn more about [different MongoDB map/reduce output types](http://docs.mongodb.org/manual/core/map-reduce/).

## What To Read Next

The documentation is organized as [a number of guides](/articles/guides.html), covering all kinds of topics.

We recommend that you read the following guides first, if possible, in this order:

 * [GridFS support](/articles/gridfs.html)
 * [Using MongoDB Aggregation Framework](/articles/aggregation.html)
 * [Using MongoDB commands](/articles/commands.html)


## Tell Us What You Think!

Please take a moment to tell us what you think about this guide on Twitter or the [Monger mailing list](https://groups.google.com/forum/#!forum/clojure-mongodb)

Let us know what was unclear or what has not been covered. Maybe you do not like the guide style or grammar or discover spelling mistakes. Reader feedback is key to making the documentation better.
