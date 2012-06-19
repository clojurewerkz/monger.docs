---
title: "Monger, a Clojure MongoDB client: integration with other libraries"
layout: article
---

## About this guide

This guide covers:

 * Monger's philosophy of "having batteries included"
 * Integration with `clojure.data.json`
 * Integration with `clj-time` and Joda Time
 * Integration with `clojure.core.cache`: MongoDB-based Clojure cache protocol implementation


This work is licensed under a <a rel="license" href="http://creativecommons.org/licenses/by/3.0/">Creative Commons Attribution 3.0 Unported License</a> (including images & stylesheets). The source is available [on Github](https://github.com/clojurewerkz/monger.docs).


## What version of Monger does this guide cover?

This guide covers Monger 1.0.0-rc2, the most recent pre-release version. Monger is a young project but most of the public API
is fleshed out and will not change before the 1.0 release.


## Overview

Monger heavily relies on relatively recent Clojure features like protocols to integrate with libraries like
[clojure.data.json](http://github.com/clojure/data.json) or [clj-time](https://github.com/seancorfield/clj-time) ([Joda Time](http://joda-time.sourceforge.net/)). As the result you can focus on your
application instead of figuring out how to glue two libraries together.


### clojure.data.json

Many applications that use MongoDB and Monger have to serialize documents stored in the database to JSON and pass
them to other applications using HTTP or messaging protocols such as [AMQP 0.9.1](http://bit.ly/amqp-model-explained) or [ZeroMQ](http://zeromq.org).

This means that MongoDB data types (object ids, documents) need to be serialized. While BSON, data format used by
MongoDB, is semantically very similar to JSON, MongoDB drivers do not typically provide serialization to JSON
and JSON serialization libraries typically do not support MongoDB data types.

Monger provides a convenient feature for `clojure.data.json`, a pretty popular modern JSON serialization library
for Clojure. The way it works is Monger will extend [clojure.data.json](https://github.com/clojure/data.json) serialization protocol to MongoDB Java
driver data types: `org.bson.types.ObjectId` and `com.mongodb.DBObject` if you opt-in for it.
To use it, you need to add `clojure.data.json` dependency to your project, for example (with Leiningen)

{% gist 2f465e2e9c71ac9aaaef %}


and then require `monger.json` namespace like so:

{% gist 5f03c5acaa0b39cf7a09 %}

when loaded, code in that namespace will extend necessary protocols and that's it. Then you can pass documents
that contain object ids in them to JSON serialization functions of `clojure.data.json` and everything will
just work.

This feature is optional: Monger does not depend on `clojure.data.json` and won't add unused dependencies
to your project.



### clj-time, Joda Time

Because of various shortcomings of Java date/time classes provided by the JDK, many projects choose to use [Joda Time](http://joda-time.sourceforge.net/) to work with dates.

To be able to insert documents with Joda Time date values in them, you need to require `monger.joda-time` namespace:

{% gist e4447d7a88eacd754d9a %}

Just like with `clojure.data.json` integration, there is nothing else you have to do. This feature is optional:
Monger does not depend on `clj-time` or `Joda Time` and won't add unused dependencies to your project.



### clojure.core.cache

Monger provides a MongoDB-backed cache implementation that conforms to the `clojure.core.cache` protocol.
It uses capped collections for caches. You can use any many cache data structure instances as your application
may need.

TBD



## What to read next

The documentation is organized as [a number of guides](/articles/guides.html), covering all kinds of topics.

We recommend that you read the following guides first, if possible, in this order:

 * [Map/Reduce](/articles/mapreduce.html)
 * [GridFS support](/articles/gridfs.html)
 * [Using MongoDB Aggregation Framework](/articles/aggregation.html)
 * [Using MongoDB commands](/articles/commands.html)


## Tell Us What You Think!

Please take a moment to tell us what you think about this guide on Twitter or the [Monger mailing list](https://groups.google.com/forum/#!forum/clojure-mongodb)

Let us know what was unclear or what has not been covered. Maybe you do not like the guide style or grammar or discover spelling mistakes. Reader feedback is key to making the documentation better.
