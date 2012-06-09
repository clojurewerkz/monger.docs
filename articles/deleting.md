---
title: "Monger, a Clojure MongoDB client: deleting documents"
layout: article
---

## About this guide

This guide covers:

 * Deleting documents with Monger
 * Deleting a single document vs multiple documents
 * Working with multiple databases

This work is licensed under a <a rel="license" href="http://creativecommons.org/licenses/by/3.0/">Creative Commons Attribution 3.0 Unported License</a> (including images & stylesheets). The source is available [on Github](https://github.com/clojurewerkz/monger.docs).


## What version of Monger does this guide cover?

This guide covers Monger 1.0.0-beta8, the most recent pre-release version. Monger is a young project but most of the public API
is fleshed out and will not change before the 1.0 release.


## How to Remove Documents with Monger

Documents are removed using `monger.collection/remove` function that takes a collection name and conditions:

{% gist 1d5d1c1f59c13193df61 %}


## Removing a single document by id

`monger.collection/remove-by-id` is useful when document id is known:

{% gist 3d74f4d2582c922be7c0 %}




## What to read next

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
