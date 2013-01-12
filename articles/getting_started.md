---
title: "Getting Started with Clojure & MongoDB using Monger | MongoDB Clojure client | MongoDB library for Clojure"
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

This guide covers Monger 1.4.


## Monger Overview

Monger is an idiomatic Clojure wrapper around MongoDB Java driver. It offers powerful expressive query DSL, strives to support
every MongoDB 2.0+ feature, has next to no performance overhead and is well maintained.


### What Monger is not

Monger is not a replacement for the MongoDB Java driver, instead, Monger is symbiotic with it. Monger does not try to offer
object/document mapping functionality. With Monger, you work with native Clojure and Java data structures like maps,
vectors, strings, dates and so on. This approach has pros and cons but (we believe) closely follows Clojure's philosophy of
reducing incidental complexity. It also fits MongoDB data model very well.


## Supported Clojure versions

Monger is built from the ground up for Clojure 1.3 and later. The most recent stable release
is highly recommended.


## Supported MongoDB versions

Monger currently uses MongoDB Java driver 2.10.x under the hood and thus supports MongoDB 2.0 and later versions. Please note that some
features may be specific to MongoDB 2.2 and later versions.


## Adding Monger Dependency To Your Project

Monger artifacts are [released to Clojars](https://clojars.org/com.novemberain/monger).

### With Leiningen

    [com.novemberain/monger "1.4.2"]

### With Maven

Add Clojars repository definition to your `pom.xml`:

{% gist 65642c4b53d26539e5f6 %}

And then the dependency:

    <dependency>
      <groupId>com.novemberain</groupId>
      <artifactId>monger</artifactId>
      <version>1.4.2</version>
    </dependency>

## Connecting to MongoDB

Monger supports working with multiple connections and/or databases but is optimized for applications that only use one connection
and one database.

To connect, you use `monger.core/connect!` and `monger.core/connect` functions:

{% gist 2af1bcee22e0f14a8741 %}

To set default database Monger will use, use `monger.core/get-db` and `monger.core/set-db!` functions in combination:

{% gist 86340be2cabe43a4ec18 %}


## Disconnecting

To disconnect, use `monger.core/disconnect!`.


### Using URI (Heroku, CloudFoundry, etc)

In certain environments, for example, Heroku or other PaaS providers, the only way to connect to MongoDB is via connection URI.

Monger provides `monger.core/connect-via-uri!` function that combines `monger.core/connect!`, `monger.core/set-db!` and `monger.core/authenticate`
and works with string URIs like `mongodb://userb71148a:0da0a696f23a4ce1ecf6d11382633eb2049d728e@cluster1.mongohost.com:27034/app81766662`.

It can be used to connect with or without authentication, for example:

{% gist 00e0c1e91d4193d0772c %}

It is also possible to pass connection options as query parameters:

{% gist 78b4b5b71472e5517e3b %}




## How to Insert Documents with Monger

To insert documents, use `monger.collection/insert` and `monger.collection/insert-batch` functions.

{% gist 901c3c3c4aee166efc0f %}

`monger.collection/insert` returns write result that `monger.result/ok?` and similar functions can operate on.

`monger.collection/insert-and-return` is an alternative insertion function that returns the exact documented inserted, including the generated document id:

{% gist b0096af8387ad31fb30c %}

`monger.collection/insert-batch` is a recommended way of inserting batches of documents (from tens to hundreds of thousands) because it
is very efficient compared to sequentially or even concurrently inserting documents one by one.


### Document ids (ObjectId)

If you insert a document without the `:_id` key, MongoDB Java driver that Monger uses under the hood will generate one for you. Unfortunately,
it does so by mutating the document you pass it. With Clojure's immutable data structures, that won't work the way MongoDB Java driver authors
expected.

So it is highly recommended to always store documents with the `:_id` key set. If you need a generated object id. You do so by instantiating
`org.bson.types.ObjectId` without arguments:

{% gist dd4dc600f50b8c6ba093 %}

To convert a string in the object id form (for example, coming from a Web form) to an `ObjectId`, instantiate `ObjectId` with an argument:

{% gist 4ba11d5ae126c6b9e2b4 %}

Document ids in MongoDB do not have to be of the object id type, they also can be strings, integers and any value you can store that MongoDB
knows how to compare order (sort). However, using `ObjectId`s is usually a good idea.


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

`monger.collection/find-maps` is similar to `monger.collection/find` but converts `DBObject` instances to Clojure maps:

{% gist 0b9c3d3a60d13ab43d60 %}


`monger.collection/find-one` finds one document and returns it as a `DBObject` instance:

{% gist 21d93baa49e69155419a %}


`monger.collection/find-one-as-map` is similar to `monger.collection/find-one` but converts `DBObject` instances to Clojure maps:

{% gist 21d93baa49e69155419a %}


A more convenient way of finding a document by id as Clojure map is `monger.collection/find-map-by-id`:

{% gist 976c79a1ba8eaac491b2 %}


### Keyword and String Field Names

Clojure maps commonly use keywords, however, BSON and many other programming languages do not have a data type like that. Intead, strings are used as
keys. Several Monger finder functions are "low level", such as [monger.collection/find](http://reference.clojuremongodb.info/monger.collection.html#var-find),
and return `com.mongodb.DBObject` instances. They can be thought of as regular Java maps with a little bit of MongoDB-specific metadata.

Other finders combine `monger.collection/find` with [monger.conversion/from-db-object](http://reference.clojuremongodb.info/monger.conversion.html#var-from-db-object) to
return Clojure maps. Some of those functions take the extra `keywordize` argument that control if resulting map keys will be turned into keywords.
An example of such finder is [monger.collection/find-one-as-map](http://reference.clojuremongodb.info/monger.collection.html#var-find-one-as-map). By default
Monger will keywordize keys.

You can use [monger.conversion/from-db-object](http://reference.clojuremongodb.info/monger.conversion.html#var-from-db-object) and [monger.conversion/to-db-object](http://reference.clojuremongodb.info/monger.conversion.html#var-to-db-object) to convert maps to `DBObject` instances and back using a custom
field name conversion strategy if you need to. Keep in mind that it likely will affect interoperability with other technologies (that may or may not use
the same naming/encoding conversion), query capabilities for cases when exact field names are not known and performance for write-heavy workloads.



### Using operators

Monger provides a convenient way to use [MongoDB query operators](http://www.mongodb.org/display/DOCS/Advanced+Queries#AdvancedQueries-ConditionalOperators). While
operators can be used in queries with strings, for example:

{% gist 7faf3e03a881f1fb8b76 %}

there is a better way to do it with Clojure. By using `monger.operators` namespace, MongoDB $operators can be written as Clojure symbols. $operators are implemented
as macros that expand at compile time. Here is what it looks like with operator macros:

{% gist 241b24026977c44bc3a4 %}



### More examples

These and other examples of Monger finders in one gist:

{% gist cc86a383c09ef1d501e8 %}



### Counting Documents

Use `monger.collection/count`, `monger.collection/empty?` and `monger.collection/any?`.



### Query DSL Overview

For cases when it is necessary to combine sorting, limiting or offseting results, pagination and even more advanced features
like cursor snapshotting or manual index hinting, Monger provides a very powerful query DSL. Here is what it looks like:

{% gist b52c7ae2312c8c6b64ee %}

It is easy to add new DSL elements, for example, adding pagination took literally less than 10 lines of Clojure code. Here is what
it looks like:

{% gist d2ddbcb27b66bba4a006 %}

Query DSL supports composition, too:

{% gist 906b1929b44deb381137 %}

Learn more in our [Querying](/articles/querying.html) guide.



## How to Update Documents with Monger

Monger's update API follows the following simple rule: the "syntax" for condition and update document structure is
the same or as close as possible to MongoDB shell and the official drivers. In addition, Monger provides several
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

Documents are removed using `monger.collection/remove` function.  `monger.collection/remove-by-id` is useful
when document id is known.

{% gist 44e3cdae129deeea7512 %}


## Integration With Other Libraries

Monger heavily relies on relatively recent Clojure features like protocols to integrate with libraries like
[Cheshire](http://github.com/dakrone/cheshire) or [clj-time](https://github.com/seancorfield/clj-time) ([Joda Time](http://joda-time.sourceforge.net/)). As the result you can focus on your
application instead of figuring out how to glue two libraries together.



### Cheshire (or clojure.data.json)

Many applications that use MongoDB and Monger have to serialize documents stored in the database to JSON and pass
them to other applications using HTTP or messaging protocols such as [AMQP 0.9.1](http://bit.ly/amqp-model-explained) or [ZeroMQ](http://zeromq.org).

This means that MongoDB data types (object ids, documents) need to be serialized. While BSON, data format used by
MongoDB, is semantically very similar to JSON, MongoDB drivers do not typically provide serialization to JSON
and JSON serialization libraries typically do not support MongoDB data types.

Monger provides a convenient feature for Cheshire, a pretty popular modern JSON serialization library
for Clojure. The way it works is Monger will add custom serializes for MongoDB Java
driver data types: `org.bson.types.ObjectId` and `com.mongodb.DBObject` if you opt-in for it.
To use it, you need to add Chshire dependency to your project, for example (with Leiningen)

{% gist 2f465e2e9c71ac9aaaef %}

and then require `monger.json` namespace like so:

{% gist 5f03c5acaa0b39cf7a09 %}

when loaded, code in that namespace will extend necessary protocols and that's it. Then you can pass documents
that contain object ids in them to JSON serialization functions from `cheshire.custom` and everything will
just work.

This feature is optional: Monger does not depend on `Cheshire` or `clojure.data.json` and won't add unused dependencies
to your project.

#### clojure.data.json Version Compatibility

Monger only works with `clojure.data.json` version `0.1.x`, as `0.2.0` completely breaks public API
and supporting both versions will require more time to investigate.



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

This topic is covered in the [Integration with 3rd party libraries](/articles/integration.html) guide.



## Wrapping Up

Congratulations, you now know how to do most common operations with Monger. Monger and MongoDB both have much
more to them to explore. Other guides explain these and other features in depth, as well as rationale and
use cases for them.

To stay up to date with Monger development, [follow @ClojureWerkz on Twitter](http://twitter.com/ClojureWerkz) and
join our [mailing list about Monger, Clojure and MongoDB](https://groups.google.com/forum/#!forum/clojure-mongodb).


## What to Read Next

The documentation is organized as [a number of guides](/articles/guides.html), covering all kinds of topics.

We recommend that you read the following guides first, if possible, in this order:

 * [Connecting to MongoDB](/articles/connecting.html)
 * [Inserting documents](/articles/inserting.html)
 * [Querying & finders](/articles/querying.html)
 * [Updating documents](/articles/updating.html)
 * [Deleting documents](/articles/deleting.html)
 * [Indexing and other collection operations](/articles/collections.html)
 * [Integration with 3rd party libraries](/articles/integration.html)
 * [Map/Reduce](/articles/mapreduce.html)
 * [GridFS support](/articles/gridfs.html)
 * [Using MongoDB Aggregation Framework](/articles/aggregation.html)
 * [Using MongoDB commands](/articles/commands.html)


## Tell Us What You Think!

Please take a moment to tell us what you think about this guide on Twitter or the [Clojure MongoDB mailing list](https://groups.google.com/forum/#!forum/clojure-mongodb)

Let us know what was unclear or what has not been covered. Maybe you do not like the guide style or grammar or discover spelling mistakes. Reader feedback is key to making the documentation better.
