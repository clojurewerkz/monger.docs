---
title: "Monger, a Clojure MongoDB client: Inserting documents"
layout: article
---

## About this guide

This guide covers:

 * Inserting documents
 * Inserting batches of documents
 * Checking database responses
 * Validating data with Validateur, a [Clojure validation library](https://github.com/michaelklishin/validateur)
 * Setting default write concern
 * Changing write concern for individual operations
 * Working with multiple databases

This work is licensed under a <a rel="license" href="http://creativecommons.org/licenses/by/3.0/">Creative Commons Attribution 3.0 Unported License</a> (including images & stylesheets). The source is available [on Github](https://github.com/clojurewerkz/monger.docs).


## What version of Monger does this guide cover?

This guide covers Monger 1.0.0-beta5, the most recent pre-release version. Monger is a young project but most of the public API
is fleshed out and will not change before the 1.0 release.



## Inserting documents

If we don't count upserts, there are two insert functions in Monger: `monger.collection/insert` and `monger.collection/insert-batch`. Lets first take a look at the former.
It takes two arguments: collection name and document to insert:

{% gist 94a8d55fe2de8479f127 %}

Document can be either a Clojure map (in the majority of cases, it is) or an instance of `com.mongodb.DBObject` (referred to later as `DBObject`).
In case your application obtains DBObjects from other libraries (for example), you can insert those:

{% gist 37931a5093b8890550f9 %}

### Collection (Array) Fields

Document fields that you need to be stored as arrays are typically passed as vectors, although they can also be lists, lazy sequences or your own data types
that are list- or set-like:

{% gist 9a9779ec0c281e5b3e5f %}


### Nested Documents

Nested documents are inserted just the way one would expect: as nested Clojure maps (or DBObjects):

{% gist a99395ff5838a9ce8fb6 %}


### Serialization of Clojure data types to DBObject and DBList

Monger will construct `DBObject` instances from Clojure data types for you. The serialization process has very low overhead and supports almost all core Clojure
data types:

 * Maps
 * Lists, vectors, sets
 * Keywords (serialized as strings)
 * Symbols (serialized as strings)
 * Ratios (serialized as doubles)

Several data types are serialized transparently because they are just Java data types:

 * Strings
 * Integers, longs, doubles
 * Dates (`java.util.Date`)

In case you need to manually convert a DBObject to Clojure map or vice versa, Monger has two functions that do that,
`monger.conversion/to-db-object` and `monger.conversion/from-db-object`:

{% gist 052ed3122cf227dcab42 %}

the latter takes an optional argument that controls whether document fields are converted to Clojure keywords. When
false, field names become string keys in the result.


## Inserting batches of documents

TBD


## Checking insertion results

TBD


## Validating data with [Validateur, a Clojure data validation library](https://github.com/michaelklishin/validateur)

TBD


## Setting default write concern

TBD


## Changing write concern for individual operations

TBD


## Working with multiple databases

TBD


## What to read next

TBD


## Tell Us What You Think!

Please take a moment to tell us what you think about this guide on Twitter or the [Monger mailing list](https://groups.google.com/forum/#!forum/clojure-monger)

Let us know what was unclear or what has not been covered. Maybe you do not like the guide style or grammar or discover spelling mistakes. Reader feedback is key to making the documentation better.
