---
title: "Getting Started with Clojure & MongoDB using Monger"
layout: article
---

## About this guide

This guide combines an overview of Monger with a quick tutorial that helps you to get started with it.
It should take about 10 minutes to read and study the provided code examples. This guide covers:

 * Feature of Monger, why Monger was created
 * Clojure and MongoDB version requirements
 * How to add Monger dependency to your project
 * Basic operations (created, read, update, delete)
 * Overview of Monger Query DSL
 * Overview of how Monger integrates with libraries like clojure.data.json and JodaTime.

This work is licensed under a <a rel="license" href="http://creativecommons.org/licenses/by/3.0/">Creative Commons Attribution 3.0 Unported License</a> (including images & stylesheets). The source is available [on Github](https://github.com/clojurewerkz/monger.docs).


## What version of Monger does this guide cover?

This guide covers Monger 1.0.0-beta2, the most recent pre-release version. Monger is a young project but most of the public API
is fleshed out and will not change before the 1.0 release.


## Monger Overview

Monger is an idiomatic Clojure wrapper around MongoDB Java driver. It offers powerful expressive query DSL, strives to support
every MongoDB 2.0+ feature, has next to no performance overhead and is well maintained.


### What Monger is not

Monger is not a replacement for the MongoDB Java driver, instead, Monger is symbiotic with it. Monger does not try to offer
object/document mapping functionality. With Monger, you work with native Clojure and Java data structures like maps,
vectors, strings, dates and so on. This approach has pros and cons but (we believe) closely follows Clojure's philosophy of
reducing incidental complexity. It also fits MongoDB data model very well.


## Supported Clojure versions

Monger is built from the ground up for Clojure 1.3 and later. n


## Supported MongoDB versions

Monger currently uses MongoDB Java driver 2.7.x under the hood and thus supports MongoDB 2.0 and later versions. Please note that some
features may be specific to MongoDB 2.2 and later versions.


## Adding Monger Dependency To Your Project

### With Leiningen

    [com.novemberain/monger "1.0.0-beta2"]

### With Maven

    <dependency>
      <groupId>com.novemberain</groupId>
      <artifactId>monger</artifactId>
      <version>1.0.0-beta2</version>
    </dependency>

## Connecting to MongoDB

Monger supports working with multiple connections and/or databases but is optimized for applications that only use one connection
and one database.

To connect, you use `monger.core/connect!` and `monger.core/connect` functions:

{% gist 2af1bcee22e0f14a8741 %}

To set default database Monger will use, use `monger.core/get-db` and `monger.core/set-db!` functions in combination:

{% gist 86340be2cabe43a4ec18 %}



## How to Insert Documents with Monger

To insert documents, use `monger.collection/insert` and `monger.collection/insert-batch` functions.

{% gist 901c3c3c4aee166efc0f %}


### Default WriteConcern

To set default write concern, use `monger.core/set-default-write-concern!` function:

{% gist 3b43b7f91341a393eb81 %}


### Safe By Default

By default Monger will use `WriteConcern/SAFE` as write concern. We believe that MongoDB Java driver (as well as other
official drivers) have **very unsafe defaults** when no exceptions are raised, even for network issues. This does not sound
like a good default for most applications: many applications use MongoDB because of the flexibility, not extreme write throughput
requirements. Monger's default is to be on the safe side.



## How to Find Documents with Monger

Monger provides two ways of finding documents:

* Using finder functions in the `monger.collection` namespace
* Using query DSL in the `monger.query` namespace

The former is designed to cover simple cases better while the latter gives you access to full power of MongoDB querying
capabilities and extra features like pagination.

### Using finder functions

Finder functions in Monger return either Clojure maps (commonly used) or Java driver's objects like `DBObject` and `DBCursor`.

For example, `monger.collection/find` returns a `DBCursor`:

{% gist d9c31fe7108941b3b94a %}


`monger.collection/find-map-by-id` finds one document by `ObjectId` and automatically converts it to a Clojure map
before returning:

{% gist 976c79a1ba8eaac491b2 %}

### Using query DSL

For cases when it is necessary to combine sorting, limiting or offseting results, pagination and even more advanced features
like cursor snapshotting or manual index hinting, Monger provides a very powerful query DSL. Here is what it looks like:

{% gist b52c7ae2312c8c6b64ee %}




## How to Update Documents with Monger

Monger's update API follows the following simple rule: the "syntax" for condition and update document structure is
the same or as close as possible to MongoDB shell and official drivers. In addition, Monger provides several
convenience functions for common cases, for example, finding documents by id.


### Regular Updates

`monger.collection/update` is the most commonly used way of updating documents. `monger.collection/update-by-id` is useful
when document id is known:

{% gist 8313a67bac %}

### Upserts

MongoDB supports upserts, "update or insert" operations. To do an upsert with Monger, use `monger.collection/update` function with `:upsert` option set to true:

{% gist 745200324f719b68bb27 %}

Note that upsert only inserts one document. Learn more about upserts in [this MongoDB documentation section](www.mongodb.org/display/DOCS/Updating#Updating-update()).


### Atomic Modifiers

Modifier operations are highly-efficient and useful when updating existing values; for instance, they're great for incrementing counters, setting individual fields, updating fields that are arrays and so on.

MongoDB supports modifiers via update operation and Monger API works the same way: you pass a document with modifiers
to `monger.collection/update`. For example, to increment number of views for a particular page:

{% gist 06ac96f1306e274ec82b %}



## How to Remove Documents with Monger

Documents are removed using `monger.collection/remove` function.  `monger.collection/update-by-id` is useful
when document id is known.

{% gist 44e3cdae129deeea7512 %}


## Integration With Other Libraries

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



## Wrapping Up

Congratulations, you now know how to do most common operations with Monger. Monger and MongoDB both have much
more to them to explore. Other guides explain these and other features in depth, as well as rationale and
use cases for them.

To stay up to date with Monger development, [follow @ClojureWerkz on Twitter](http://twitter.com/ClojureWerkz) and
join our [mailing list about Monger, Clojure and MongoDB](https://groups.google.com/forum/#!forum/clojure-monger).


## What to Read Next

Documentation is organized as a number of guides, covering all kinds of topics.

We recommend that you read the following guides first, if possible, in this order:

 * [Connecting to MongoDB](/guides/connecting.html)
 * [Inserting documents](/guides/inserting.html)
 * [Querying & finders](/guides/querying.html)
 * [Updating documents](/guides/updating.html)
 * [Deleting documents](/guides/deleting.html)
 * [Integration with 3rd party libraries](/guides/integration.html)
 * [Map/Reduce](/guides/mapreduce.html)
 * [GridFS support](/guides/gridfs.html)


## Tell Us What You Think!

Please take a moment to tell us what you think about this guide on Twitter or the [Monger mailing list](https://groups.google.com/forum/#!forum/clojure-monger)

Let us know what was unclear or what has not been covered. Maybe you do not like the guide style or grammar or discover spelling mistakes. Reader feedback is key to making the documentation better.
