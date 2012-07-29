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

This guide covers Monger 1.1.


## Overview

Monger's update API follows the following simple rule: the "syntax" for condition and update document structure is
the same or as close as possible to MongoDB shell and the official drivers. In addition, Monger provides several
convenience functions for common cases, for example, finding documents by id.


## Updating multiple documents

`monger.collection/update` is the most commonly used way of updating documents. It takes a collection name, a query condition, an updated document
and a number of options, the most
commonly used of which is `:multi`. When `:multi` is set to `true`, it instructs MongoDB to update multiple documents
that match the query.

{% gist b8718a8f2bd98db2bc35 %}



## Updating a single document

`monger.collection/update` can be instructed to only update a single document by passing `:multi` option with the value of `false`:

{% gist 0b7c7eccec8f18827ec0 %}


`monger.collection/update-by-id` is useful when document id is known:

{% gist 0a617c7edd8b15f87f29 %}



## Overriding write concern

It is possible to override default write concern for a single operation using the `:write-concern` key that `monger.collection/update` and
`monger.collection/update-by-id` accept:

{% gist a64ac1fb920d7405953f %}


## Upserts

MongoDB supports upserts, "update or insert" operations. To do an upsert with Monger, use `monger.collection/update` function with `:upsert` option set to true:

{% gist 745200324f719b68bb27 %}

Note that upsert only inserts one document. Learn more about upserts in [this MongoDB documentation section](www.mongodb.org/display/DOCS/Updating#Updating-update()).


## Atomic Modifiers

### Overview

Modifier operations are highly-efficient and useful when updating existing values; for instance, they're great for incrementing counters, setting individual fields, updating fields that are arrays and so on.

MongoDB supports modifiers via update operation and Monger API works the same way: you pass a document with modifiers
to `monger.collection/update`. For example, to increment number of views for a particular page:

{% gist 06ac96f1306e274ec82b %}

Whenever you have a MongoDB shell example, query and update documents will have the same structure with Monger. Monger provides a number of macros in the
`monger.operators` namespace to make your code look a little bit cleaner: for example, instead of `"$set"` you can use `$set` and it will be expanded
to `"$set"` when your Clojure code is compiled (at the macro expansion time).

Atomic modifiers are documented in detail in this [MongoDB documentation guide](http://www.mongodb.org/display/DOCS/Atomic+Operations). Below we will
demonstrate eachsome of them with (very short) code examples, but using them in general is identical to how you use them
in the MongoDB shell.


### Using $inc operator

{% gist 06ac96f1306e274ec82b %}


### Using $set operator

`$set` operator changes (sets) one or more fields for a document (or multiple documents)

{% gist 3a8cd77671e005908a26 %}


### Using $unset operator

`$unset` operator clears (removes) a single field from a document

{% gist 06ac96f1306e274ec82b %}


### Using $push operator

`$push` operator appends a single value to an array field (and allows for duplicates):

{% gist a964f59dd1106de17636 %}


### Using $pushAll operator

`$pushAll` operator adds multiple values to an array field (and allows for duplicates):

{% gist a2f3c5216c0be7cc5dfa %}


### Using $pushAll operator

`$addToSet` operator is similar to `$push` but will filter out duplicate entries (sets are collections
that do not have duplicates):

{% gist f21b68373c134f594011 %}


### Using $pull operator

{% gist 6106b32eea2aa6d3550d %}


### Using $pullAll operator

{% gist f3c23576f286189c9035 %}


## Using save operation

`monger.collection/save` function performs insert or update (replace) based on whether or not provided document already has an id (whether the `:_id` field
is set).

With a new document, it works very much like `monger.collection/insert`:

{% gist 001e9997d8171c1f8a76 %}

`monger.collection/save-and-return` with new documents works the same way as `monger.collection/insert-and-return`:

{% gist ae2878424c43fc80cbed %}

With documents that are not new (already have the `:_id` field), `monger.collection/save` and `monger.collection/save-and-return`
will first look up the existing document by id and replace it with the one provided:

{% gist b4efe9396071feac5d6c %}



## What to read next

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

Please take a moment to tell us what you think about this guide on Twitter or the [Monger mailing list](https://groups.google.com/forum/#!forum/clojure-mongodb)

Let us know what was unclear or what has not been covered. Maybe you do not like the guide style or grammar or discover spelling mistakes. Reader feedback is key to making the documentation better.
