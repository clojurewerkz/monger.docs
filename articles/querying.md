---
title: "Monger, a Clojure MongoDB client: querying the database"
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

This guide covers Monger 1.0.0-beta7, the most recent pre-release version. Monger is a young project but most of the public API
is fleshed out and will not change before the 1.0 release.


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

First lets find several documents by the language. To do this, use `monger.collection/find` function:

{% gist d9c31fe7108941b3b94a %}

`monger.collection/find` takes a collection name and query conditoins and returns a database cursor you can use [clojure.core/seq](http://clojuredocs.org/clojure_core/clojure.core/seq) on.
Each element of the sequence is a `com.mongodb.DBObject` instance. They can be transformed into Clojure maps using `monger.conversion/from-db-object` fn:

{% gist 5b5e0acb431ccc9a43e2 %}

Turning the entire result set into Clojure maps is so common, however, that Monger provides a function that works exactly like `monger.collection/find` but
returns a lazy sequence of maps, `monger.collection/find-maps`:

{% gist 0b9c3d3a60d13ab43d60 %}


## Loading a single document

`monger.collection/find-one` finds one document and returns it as a `DBObject` instance:

{% gist 21d93baa49e69155419a %}


`monger.collection/find-one-as-map` is similar to `monger.collection/find-one` but converts `DBObject` instances to Clojure maps:

{% gist 21d93baa49e69155419a %}


A more convenient way of finding a document by id as Clojure map is `monger.collection/find-map-by-id`:

{% gist 976c79a1ba8eaac491b2 %}


## Loading a subset of fields

Both `monger.collection/find` and `monger.collection/find-maps` take 3rd argument that specifies what fields need to be retrieved:

{% gist %}

This is useful to excluding very large fields from loading when you won't operate on them.

Fields can be specified as a document (just like in the MongoDB shell) but it is more common to pass them as a vector of keywords. Monger
will transform them into a document for you.


## Using MongoDB query operators

Monger provides a convenient way to use [MongoDB query operators](http://www.mongodb.org/display/DOCS/Advanced+Queries#AdvancedQueries-ConditionalOperators). While
operators can be used in queries with strings, for example:

{% gist 7faf3e03a881f1fb8b76 %}

there is a better way to do it with Clojure. By using `monger.operators` namespace, MongoDB $operators can be written as Clojure symbols. $operators are implemented
as macros that expand at compile time. Here is what it looks like with operator macros:

{% gist 241b24026977c44bc3a4 %}


## More examples

These and other examples of Monger finders in one gist:

{% gist cc86a383c09ef1d501e8 %}



## Sorting

TBD


## Using skip and limit

TBD


## Using pagination

TBD


## Read preference

TBD


## Snapshotting cursors

TBD


## Counting documents

Use `monger.collection/count`, `monger.collection/empty?` and `monger.collection/any?`:

{% gist 87b737a55a961ef7d731 %}


## Worker with multiple databases

TBD


## What to read next

The documentation is organized as a number of guides, covering all kinds of topics.

We recommend that you read the following guides first, if possible, in this order:

 * [Updating documents](/articles/updating.html)
 * [Deleting documents](/articles/deleting.html)
 * [Integration with 3rd party libraries](/articles/integration.html)
 * [Map/Reduce](/articles/mapreduce.html)
 * [GridFS support](/articles/gridfs.html)


## Tell Us What You Think!

Please take a moment to tell us what you think about this guide on Twitter or the [Monger mailing list](https://groups.google.com/forum/#!forum/clojure-monger)

Let us know what was unclear or what has not been covered. Maybe you do not like the guide style or grammar or discover spelling mistakes. Reader feedback is key to making the documentation better.
