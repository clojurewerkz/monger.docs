---
title: "Monger, a Clojure MongoDB client: Indexing and other operations on collections"
layout: article
---

## About this guide

This guide covers:

 * Creating indexes with Monger
 * Dropping indexes with Monger
 * Using Monger to reindex a collection
 * Dropping a collection


This work is licensed under a <a rel="license" href="http://creativecommons.org/licenses/by/3.0/">Creative Commons Attribution 3.0 Unported License</a> (including images & stylesheets). The source is available [on Github](https://github.com/clojurewerkz/monger.docs).


## What version of Monger does this guide cover?

This guide covers Monger 1.0.0, the most recent release.


## Overview

MongoDB provides operations on collections that are not related to inserting, updating or querying data. Two examples of such operations are
operations on indexes and dropping of a collection.

### Indexes

Indexes in databases are data structures that allow for significant read query performance improvements on large data sets at the cost of
a slight performance hit on write queries (because indexes need to be updated on writes).

[MongoDB indexes](http://www.mongodb.org/display/DOCS/Indexes) are much like their relational data store counterparts: they are created for a particular collection and a set of fields,
you can specify ordering on each field and they can be dropped or re-created.


## Creating indexes

To create an index on a collection, use `monger.collection/ensure-index`. It will create the index but only if it does not already exist:

{% gist a860835fb578fe9355a9 %}

`monger.collection/create-index` takes the same arguments but will fail if the index already exists.


## Listing indexes on a collection

To get a set of indexes on a collection, use `monger.collection/indexes-on` function that takes collection name as its only argument.


## Dropping indexes

To drop an index or all indexes on a collection, use `monger.collection/drop-index` and `monger.collection/drop-indexes`:

{% gist 7b40369dc26909f0d165 %}



## Dropping a collection

To drop a collection, use `monger.collection/drop` function that takes collection name as its only argument.


## Renaming a collection

To rename a collection, use `monger.collection/rename` that takes old and new collection names as arguments.


## What to Read Next

The documentation is organized as [a number of guides](/articles/guides.html), covering all kinds of topics.

We recommend that you read the following guides first, if possible, in this order:

 * [Integration with 3rd party libraries](/articles/integration.html)
 * [Map/Reduce](/articles/mapreduce.html)
 * [GridFS support](/articles/gridfs.html)
 * [Using MongoDB Aggregation Framework](/articles/aggregation.html)
 * [Using MongoDB commands](/articles/commands.html)


## Tell Us What You Think!

Please take a moment to tell us what you think about this guide on Twitter or the [Monger mailing list](https://groups.google.com/forum/#!forum/clojure-mongodb)

Let us know what was unclear or what has not been covered. Maybe you do not like the guide style or grammar or discover spelling mistakes. Reader feedback is key to making the documentation better.
