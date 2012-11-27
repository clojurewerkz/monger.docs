---
title: "Monger, a Clojure MongoDB client: Map/Reduce | MongoDB library for Clojure"
layout: article
---

## About this guide

This guide covers:

 * Using MongoDB 2.2 Aggregation Framework with Clojure


This work is licensed under a <a rel="license" href="http://creativecommons.org/licenses/by/3.0/">Creative Commons Attribution 3.0 Unported License</a> (including images & stylesheets). The source is available [on Github](https://github.com/clojurewerkz/monger.docs).


## What version of Monger does this guide cover?

This guide covers Monger 1.4.


## What version of MongoDB does this guide cover?

This guide covers a feature that is going to be introduced with the release of MongoDB 2.2. Support for it was developed using
2.1.x releases (developer previews).


## Overview

MongoDB 2.2 also supports a more focused, less generic and easier to use data processing feature called the [Aggregation Framework](/articles/aggregation.html) which
makes raw map/reduce a relatively low-level facility.


## Performing MongoDB aggregation queries with Clojure

Conceptually, documents from a collection pass through an aggregation pipeline, which transforms these objects they pass through. For those familiar with UNIX-like shells
(e.g. bash,) the concept is analogous to the pipe (i.e. `|`) used to string text filters together.

In a shell environment the pipe redirects a stream of characters from the output of one process to the input of the next. The MongoDB aggregation pipeline streams MongoDB
documents from one pipeline operator to the next to process the documents.

All pipeline operators process a stream of documents and the pipeline behaves as if the operation scans a collection and passes all matching documents into the “top” of
the pipeline. Each operator in the pipleine transforms each document as it passes through the pipeline.

`monger.collection/aggregate` is the function used to perform aggregation queries with Monger. It takes a collection name and a list of aggregation
*pipeline stages* or *pipeline operators*, such as `$project` or `$multiplyBy`.

Pipeline operators are specified as documents (Clojure maps) and contain `$operators` similar to those used by perform queries and updates. They can be specified
as strings, e.g. "$project", or using predefined operators from the `monger.operators` namespace, e.g. `$project`:

{% gist ca36b5c9c99592540ea4 %}

Unlike Map/Reduce operators, aggregation queries are always returned "inline" (as a value by `monger.collection/aggregate`).


## What to read next

For an in-depth overview of the MongoDB 2.2 Aggregation Framework, please refer to this [MongoDB Aggregation Framework guide](http://docs.mongodb.org/manual/applications/aggregation/). There is also a [pipeline operator reference](http://docs.mongodb.org/manual/reference/aggregation/).

The documentation is organized as [a number of guides](/articles/guides.html), covering all kinds of topics.

We recommend that you read the following guides first, if possible, in this order:

 * [Using MongoDB commands](/articles/commands.html)


## Tell Us What You Think!

Please take a moment to tell us what you think about this guide on Twitter or the [Monger mailing list](https://groups.google.com/forum/#!forum/clojure-mongodb)

Let us know what was unclear or what has not been covered. Maybe you do not like the guide style or grammar or discover spelling mistakes. Reader feedback is key to making the documentation better.
