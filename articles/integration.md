---
title: "Monger, a Clojure MongoDB client: integration with other libraries | MongoDB library for Clojure"
layout: article
---

## About this guide

This guide covers:

 * Monger's philosophy of "having batteries included"
 * Integration with `clojure.data.json`
 * Integration with `clj-time` and Joda Time
 * Integration with `clojure.core.cache`: MongoDB-based Clojure cache protocol implementation
 * Using MongoDB-backed Ring session stores
 * Basic [Noir](http://webnoir.org) integration example


This work is licensed under a <a rel="license" href="http://creativecommons.org/licenses/by/3.0/">Creative Commons Attribution 3.0 Unported License</a> (including images & stylesheets). The source is available [on Github](https://github.com/clojurewerkz/monger.docs).


## What version of Monger does this guide cover?

This guide covers Monger 1.2.


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

This means that you need to add dependencies on `clj-time` and `clojure.data.json` to your project before requiring `monger.joda-time`.

#### Setting Default Time Zone Used By Joda Time

When Joda Time integration is loaded, Monger extends its own Clojure-to-DBObject conversion protocol to support Joda Time date/time/instant types
and convert loaded dates to Joda Time dates. When a new Joda Time `org.joda.time.DateTime` instance is created, it will use an environment-specific
time zone by default (configured via the `user.timezone` JVM property). Because altering `user.timezone` may also affect other libraries,
it is recommended to set default time zone using Joda Time API like so:

{% gist e925a257436a631ed8d5 %}

This will only have effect on Joda Time (and, in turn, Monger date/time/instant deserialization).


### Use require, Not use

`monger.joda-time` and `monger.json` only extend existing protocols and do not define public functions. Due to [a subtle bug in Clojure 1.5](http://dev.clojure.org/jira/browse/CLJ-1062),
using the ns macro's `:use` option on such namespace will fail. But worry not: there is no need to use `:use`. Just use `:require`, it will cause
protocol extensions to be compiled and that's all you need to use Monger's integration points.



### clojure.core.cache

Monger provides a MongoDB-backed cache implementation that conforms to the `clojure.core.cache` protocol.
It uses capped collections for caches. You can use any many cache data structure instances as your application
may need.

To use Monger's cache implementation, use functions in both `clojure.core.cache` and `monger.cache` namespaces, then create
a cache store using `monger.cache/basic-monger-cache-factory` that can be passed a collection name you want the cache to use:

{% gist ca954b7edc7b10e91c73 %}

Then use the store like you would any other `core.cache` store, database backed or not.

To learn more about the [clojure.core.cache](https://github.com/clojure/core.cache) protocol and functions it provides,
see [clojure.core.cache documentation](https://github.com/clojure/core.cache/wiki).



### Using MongoDB-backed Ring session store with Monger 1.0

Monger 1.0 provides a MongoDB-backed session store for [Ring, Clojure's ubiquitous HTTP middleware library](https://github.com/ring-clojure/ring). It can be found in the
`monger.ring.session-store` namespace. To create a new store, use the `monger.ring.session-store/monger-store` that takes name of the collections
sessions will be stored in:

{% gist cfcecfa0b7ec191693ac %}

Below is an example take from [RefHeap](https://github.com/Raynes/refheap), an open source paste app build with Clojure, Noir, MongoDB and Monger and hosted on Heroku.
**Please note that this example uses Clojure 1.4 features**:

{% gist 329e32c41c5c55e9ba5e %}


### Using MongoDB-backed Ring session store with Monger 1.1

Monger `1.1.0-beta1` and later versions provide the same MongoDB-backed session store for Ring as 1.0 but also has an alternative store that uses Clojure reader serialization.
This means this store stores data in a way that non-Clojure applications won't be able to read easily but also supports edge cases in Clojure
data type serialization that Monger itself does not, for example, namespaced keywords (like `::identity`).

This is the Ring session store you should use if you want to use Monger with [Friend, a popular authentication and authorization library for Clojure](https://github.com/cemerick/friend/).


It works exactly the same way but the name of the function that creates a store is different:

{% gist 0039b3cdcdb21d54e911 %}




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
