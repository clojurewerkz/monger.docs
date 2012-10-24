---
title: "Monger, a Clojure MongoDB client: querying the database | MongoDB library for Clojure"
layout: article
---

## About this guide

This guide covers:

 * Querying documents with Monger
 * Working with database cursors
 * Using Monger Query DSL
 * Using query operators with Monger
 * Counting documents
 * Working with multiple databases

This work is licensed under a <a rel="license" href="http://creativecommons.org/licenses/by/3.0/">Creative Commons Attribution 3.0 Unported License</a> (including images & stylesheets). The source is available [on Github](https://github.com/clojurewerkz/monger.docs).


## What version of Monger does this guide cover?

This guide covers Monger 1.3.


## Overview

Monger provides two means of querying:

 * Regular finder functions
 * Query DSL

Regular finder functions are very much similar to those in the MongoDB shell and various MongoDB drivers. For regular finders, Monger does not invent its
own query language or "syntax": you use the same document structure as you would in the shell. This makes finder functions a lot more predictable,
easier to learn and follows the convention existing MongoDB drivers have.

Finder functions belong to the `monger.collection` namespace and always take collection name as their first argument. Some of them return database
cursors (that are iterable Java objects and thus [seqable](http://clojuredocs.org/clojure_core/clojure.core/seq)) that produce `DBObjects`, other (much
more commonly used) return lazy sequences of Clojure maps. This means that with Monger, you work with Clojure data structures
(collections and maps). It is natural to express documents as maps in Clojure and Monger fully embraces this idea.

Monger Query DSL is expressive and composable (we will demonstrate what it means later in this guide). It was designed for cases when you may need
to perform a sophisticated query that includes conditions, sorting, limit and/or offset and may benefit from paginating results or using advanced
MongoDB features like cursor snapshotting.


## Finding documents

To find multiple documents, use `monger.collection/find`:

{% gist d9c31fe7108941b3b94a %}

`monger.collection/find` takes a collection name and query conditoins and returns a database cursor you can use [clojure.core/seq](http://clojuredocs.org/clojure_core/clojure.core/seq) on.
Each element of the sequence is a `com.mongodb.DBObject` instance. They can be transformed into Clojure maps using `monger.conversion/from-db-object` fn:

{% gist 5b5e0acb431ccc9a43e2 %}

Turning the entire result set into Clojure maps is so common, however, that Monger provides a function that works exactly like `monger.collection/find` but
returns a lazy sequence of maps, `monger.collection/find-maps`:

{% gist 0b9c3d3a60d13ab43d60 %}

Normally you should prefer `monger.collection/find-maps` to `monger.collection/find`, which is considered to be part of the lower-level API.

### Sorting, limits, offsets

To use sorting, limit, offset, pagination and so on, please use Monger's Query DSL (covered later in this guide).


## Finding a single document

`monger.collection/find-one` finds one document and returns it as a `DBObject` instance:

{% gist 21d93baa49e69155419a %}


`monger.collection/find-one-as-map` is similar to `monger.collection/find-one` but converts `DBObject` instances to Clojure maps:

{% gist 21d93baa49e69155419a %}


A more convenient way of finding a document by id as Clojure map is `monger.collection/find-map-by-id`:

{% gist 976c79a1ba8eaac491b2 %}

Normally you should prefer `monger.collection/find-one-as-map` and `monger.collection/find-map-by-id` to `monger.collection/find-one`.

### Convert a string to a MongoDB (BSON) ObjectId

To convert a string in the object id form (for example, coming from a Web form) to an `ObjectId`, instantiate `ObjectId` with an argument:

{% gist 4ba11d5ae126c6b9e2b4 %}

Document ids in MongoDB do not have to be of the object id type, they also can be strings, integers and any value you can store that MongoDB
knows how to compare order (sort). However, using `ObjectId`s is usually a good idea.

To coerce an input to `ObjectId` (instantiate one from a string of the input is a string, or just return the input if it is an `ObjectId`), there
is [monger.conversion/to-object-id](http://reference.clojuremongodb.info/monger.conversion.html#var-to-object-id). 

### Convert a MongoDB (BSON) ObjectId to a string

To convert a BSON ObjectId (`org.bson.types.ObjectId` instance) to a string, just use [clojure.core/str](http://clojuredocs.org/clojure_core/clojure.core/str) to
it or call `org.bson.types.ObjectId#toString` on it.


## Loading a subset of fields

Both `monger.collection/find` and `monger.collection/find-maps` take 3rd argument that specifies what fields need to be retrieved:

{% gist a19e3a9caea159af9623 %}

This is useful to excluding very large fields from loading when you won't operate on them.

Fields can be specified as a document (just like in the MongoDB shell) but it is more common to pass them as a vector of keywords. Monger
will transform them into a document for you.


## Reaching Into Nested Documents in Conditions

To "reach into" nested documents and use them in conditions, MongoDB uses the "dot syntax" for fields. For example, with a document
that looks like this:

{% gist 816af9b1ca34a638e88c %}

it is possible to address the zip field in a condition as `"address.zip"`. This is exactly how you do it in Monger in conditions and arguments for
operators like `$set`:

{% gist b4f30dc76cec7f987155 %}


## Keyword and String Field Names

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


## Using MongoDB query operators

Monger provides a convenient way to use [MongoDB query operators](http://www.mongodb.org/display/DOCS/Advanced+Queries#AdvancedQueries-ConditionalOperators). While
operators can be used in queries with strings, for example:

{% gist 7faf3e03a881f1fb8b76 %}

there is a better way to do it with Clojure. By using `monger.operators` namespace, MongoDB $operators can be written as Clojure symbols. $operators are implemented
as macros that expand at compile time. Here is what it looks like with operator macros:

{% gist 241b24026977c44bc3a4 %}

Below are more examples that use various query operators (you can use any operator supported by the MongoDB shell with Monger):

### <, <=, >, >=

{% gist 3343bde11fd0aa553b7e %}

The `$gt`, `$lt`, `$gte`, `$lte` operators work on dates and are very commonly used for time range queries. Date values can be `java.util.Date` instances or
(highly recommended) [Joda Time](http://joda-time.sourceforge.net) dates. If you want to use Joda Time in Clojure, [clj-time](https://github.com/seancorfield/clj-time) is a good option.

### $exists

{% gist a430c745ccde1fbf258b %}


### $mod

{% gist 1d3a0c83c5279611f86b %}


### $ne

{% gist 857fb1e15124823ebb87 %}


### $all, $in, $nin

{% gist a114d7ad3ca97a60d053  %}


### $and, $or, $nor

{% gist b4a9aea666cc63176ce2 %}


### $regex (regular expression matches)

{% gist 7c72bb2a40b50f1f3a52 %}


### $elemMatch

{% gist 2978b3d8f7f04d9c2bb1 %}


### $group, $project, etc (MongoDB 2.2 Aggregation Framework support)

Monger supports a new feature in MongoDB 2.2, the Aggregation Framework. It is a vast topic that is out of scope of this guide
and will be covered in a separate one when MongoDB 2.2 reaches release candidate stages.


## More examples

These and other examples of Monger finders in one gist:

{% gist cc86a383c09ef1d501e8 %}


## Getting Distinct Documents

To get a collection of distinct documents by field or query, use the `monger.collection/distinct` function that returns a lazy sequence
of documents. There is currently no `monger.collection/distinct-maps` or similar function so to produce a sequence of Clojure maps,
it is necessary to map (`clojure.core/map`) with `monger.conversion/from-db-object` over the results.

TBD: examples


## Monger Query DSL

Queries that need sorting (and with it, commonly skip/limit/pagination) use Monger's Query DSL. It is composed of functions and macros in the `monger.query` namespace,
with additional convenience operator macros from the `monger.operators` namespace.  Monger's Query DSL is heavily inspired by [SQL Korma](http://sqlkorma.com/), is composable and easy to extend if necessary.

Queries performed via Query DSL always return sequences of Clojure maps, like `monger.collection/find-maps` does.

Lets take a look at its core features first.

### Sorting, skip and limit

Sorting documents are specified exactly as they are in the MongoDB shell (1 for ascending, -1 for descending ordering):

{% gist 85d764e2e9512ee651a7 %}

This example also demonstrates query conditions and fetching a subset of fields.


### Using pagination

Using `skip` and `limit` to do pagination in the query DSL is so common that Monger provides a DSL extension for that:

{% gist 8a214a841d5f76f4f096 %}


### Read preference

Read preference lets MongoDB clients specify whether a query should go to the master/primary node (thus guaranteeing consistency but also
putting extra load on primaries) or it's OK to read from slaves (and thus get eventual consistency, which ocassionally may result
in slightly out of date data to be returned):

{% gist ffa7a09975d5a303cac9 %}

Possible read preference values are returned by the following static methods:

 * `com.mongodb.ReadPreference/primary` (read only from master, throw an error if it is not available)
 * `com.mongodb.ReadPreference/primaryPreferred` (read from master if available, a slave otherwise)
 * `com.mongodb.ReadPreference/secondary` (read from a slave if available, otherwise throw an error)
 * `com.mongodb.ReadPreference/secondaryPreferred` (read from a slave if available, from master otherwise)
 * `com.mongodb.ReadPreference/nearest` (read from the nearest node)


### Snapshotting cursors

A MongoDB query returns data as well as a cursor ID for additional lookups, should more data exist.  Drivers lazily perform a "get more" operation as needed on the
cursor to get more data. Cursors may have latent getMore accesses that occurs after an intervening write operation on the database collection (i.e., an insert,
update, or delete).

Conceptually, a cursor has a current position. If you delete the item at the current position, the cursor automatically skips its current position forward to the
next item. Snapshotting a cursor assures that objects which update during the lifetime of a query are returned once and only once. This is most important when doing a
find-and-update loop that changes.

Here is how to snapshot a cursor with Monger query DSL:

{% gist 1d5c6c66c8b47c0929bd %}


### Index hints

While not necessary in most cases, it is possible to force query to use the given index:

{% gist 4adba414618dfb45c73f %}

Hinting only makes sense in the presence of multiple compound indexes that may be used by the optimizer and sorting
by one or both indexed fields.


### Setting batch size

Cursors fetch documents from the server in batches. It is possible to specify the size of the batch to improve performance
or limit the number of documents returned by the server:

{% gist bab0ab8a3c9b56634fec %}

If batch size is negative, it will limit of number objects returned, that fit within the max batch size limit (usually 4MB), and cursor will be closed.
For example if batch-size is -10, then the server will return a maximum of 10 documents and as many as can fit in 4MB, then close the cursor.
Note that this feature is different from limit in that documents must fit within a maximum size, and it removes the need to send a request
to close the cursor server-side.


## Counting documents

Use `monger.collection/count`, `monger.collection/empty?` and `monger.collection/any?`:

{% gist 87b737a55a961ef7d731 %}


## Working with multiple databases

Monger is optimized for applications that use only one database but it is possible to work with multiple ones. For that, use [clojure.core/binding](http://clojuredocs.org/clojure_core/clojure.core/binding) to rebind
`monger.core/*mongodb-database*`, `monger.core/*mongodb-connection*` and `monger.core/*mongodb-gridfs*` vars to different values or use convenience functions
that do that: `monger.core/with-connection`, `monger.core/with-db`, `monger.core/with-gridfs`. This is a common practice for Clojure libraries. Remember that
var bindings are thread-local.



## What to read next

The documentation is organized as [a number of guides](/articles/guides.html), covering all kinds of topics.

We recommend that you read the following guides first, if possible, in this order:

 * [Updating documents](/articles/updating.html)
 * [Deleting documents](/articles/deleting.html)
 * [Indexing and other collection operations](/articles/collections.html)
 * [Integration with 3rd party libraries](/articles/integration.html)
 * [Map/Reduce](/articles/mapreduce.html)
 * [GridFS support](/articles/gridfs.html)
 * [Using MongoDB Aggregation Framework](/articles/aggregation.html)
 * [Using MongoDB commands](/articles/commands.html)


## Tell Us What You Think!

Please take a moment to tell us what you think about this guide on Twitter or the [Monger mailing list](https://groups.google.com/forum/#!forum/clojure-mongodb)

Let us know what was unclear or what has not been covered. Maybe you do not like the guide style or grammar or discover spelling mistakes. Reader feedback is key to making the documentation better.
