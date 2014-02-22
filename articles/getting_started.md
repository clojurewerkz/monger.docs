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

This guide covers Monger 1.7 (including beta releases).


## Monger Overview

Monger is an idiomatic Clojure wrapper around MongoDB Java driver. It offers powerful expressive query DSL, strives to support
every MongoDB 2.0+ feature, has next to no performance overhead and is well maintained.


### What Monger is not

Monger is not a replacement for the MongoDB Java driver, instead, Monger is symbiotic with it. Monger does not try to offer
object/document mapping functionality. With Monger, you work with native Clojure and Java data structures like maps,
vectors, strings, dates and so on. This approach has pros and cons but (we believe) closely follows Clojure's philosophy of
reducing incidental complexity. It also fits MongoDB data model very well.


## Supported Clojure versions

Monger requires Clojure 1.4+. The most recent stable release is highly
recommended.


## Supported MongoDB versions

Monger currently uses MongoDB Java driver 2.11.x under the hood and
thus supports MongoDB 2.0 and later versions. Please note that some
features may be specific to MongoDB 2.2 and later versions.


## Adding Monger Dependency To Your Project

Monger artifacts are [released to Clojars](https://clojars.org/com.novemberain/monger).

### With Leiningen

``` clojure
[com.novemberain/monger "1.7.0"]
```

### With Maven

Add Clojars repository definition to your `pom.xml`:

``` xml
<repository>
  <id>clojars.org</id>
  <url>http://clojars.org/repo</url>
</repository>
```

And then the dependency:

``` xml
<dependency>
  <groupId>com.novemberain</groupId>
  <artifactId>monger</artifactId>
  <version>1.7.0</version>
</dependency>
```

## Connecting to MongoDB

Monger supports working with multiple connections and/or databases but is optimized for applications that only use one connection
and one database.

To connect, you use `monger.core/connect!` and `monger.core/connect` functions:

``` clojure
(ns my.service.server
  (:require [monger.core :as mg])
  (:import [com.mongodb MongoOptions ServerAddress]))

;; localhost, default port
(mg/connect!)

;; given host, given port
(mg/connect! { :host "db.megacorp.internal" :port 7878 })


;; given host, given port
(mg/connect! { :host "db.megacorp.internal" :port 7878 })

;; using MongoOptions allows fine-tuning connection parameters,
;; like automatic reconnection (highly recommended for production environment)
(let [^MongoOptions opts (mg/mongo-options :threads-allowed-to-block-for-connection-multiplier 300)
      ^ServerAddress sa  (mg/server-address "127.0.0.1" 27017)]
  (mg/connect! sa opts))
```

To set default database Monger will use, use `monger.core/get-db` and `monger.core/set-db!` functions in combination:

``` clojure
(ns my.service.server
  (:require [monger.core :as mg]))

;; localhost, default port
(mg/connect!)
(mg/set-db! (mg/get-db "monger-test"))
```


## Disconnecting

To disconnect, use `monger.core/disconnect!`.


### Using URI (Heroku, CloudFoundry, etc)

In certain environments, for example, Heroku or other PaaS providers,
the only way to connect to MongoDB is via connection URI.

Monger provides `monger.core/connect-via-uri!` function that combines `monger.core/connect!`, `monger.core/set-db!` and `monger.core/authenticate`
and works with string URIs like `mongodb://userb71148a:0da0a696f23a4ce1ecf6d11382633eb2049d728e@cluster1.mongohost.com:27034/app81766662`.

It can be used to connect with or without authentication, for example:

``` clojure
;; connect without authentication
(mg/connect-via-uri! "mongodb://127.0.0.1/monger-test4")

;; connect with authentication
(mg/connect-via-uri! "mongodb://clojurewerkz/monger!:monger!@127.0.0.1/monger-test4")

;; connect using connection URI stored in an env variable, in this case, MONGOHQ_URL
(mg/connect-via-uri! (System/genenv "MONGOHQ_URL"))
```

It is also possible to pass connection options as query parameters:

``` clojure
(monger.core/connect-via-uri! "mongodb://localhost/test?maxPoolSize=128&waitQueueMultiple=5;waitQueueTimeoutMS=150;socketTimeoutMS=5500&autoConnectRetry=true;safe=false&w=1;wtimeout=2500;fsync=true")
```



## How to Insert Documents with Monger

To insert documents, `insert`, `insert-and-return` and `insert-batch` functions
in the `monger.collection` namespace are used.

``` clojure
(ns my.service.server
  (:require [monger.core :refer [connect! connect set-db! get-db]]
            [monger.collection :refer [insert insert-batch]])
  (:import [org.bson.types ObjectId]
           [com.mongodb DB WriteConcern]))

;; localhost, default port
(connect!)
(set-db! (monger.core/get-db "monger-test"))

;; with a generated document id, returns the complete
;; inserted document
(mc/insert-and-return "documents" {:name "John" :age 30})

;; with explicit document id (recommended)
(insert "documents" { :_id (ObjectId.) :first_name "John" :last_name "Lennon" })

;; multiple documents at once
(insert-batch "document" [{ :first_name "John" :last_name "Lennon" }
                          { :first_name "Paul" :last_name "McCartney" }])

;; without document id (when you don't need to use it after storing the document)
(insert "document" { :first_name "John" :last_name "Lennon" })

;; with a different write concern
(insert "documents" { :_id (ObjectId.) :first_name "John" :last_name "Lennon" } WriteConcern/JOURNAL_SAFE)

;; with a different database
(let [archive-db (get-db "monger-test.archive")]
  (insert archive-db "documents" { :first_name "John" :last_name "Lennon" } WriteConcern/NORMAL))
```

`monger.collection/insert` returns write result that
`monger.result/ok?` and similar functions can operate on.

`monger.collection/insert-and-return` returns the exact documented
inserted, including the generated document id. It is convenient
but requires manual checking for errors with `monger.core/get-last-error`.

`monger.collection/insert-batch` is a recommended way of inserting
batches of documents (from tens to hundreds of thousands) because it
is very efficient compared to sequentially or even concurrently
inserting documents one by one.


### Document ids (ObjectId)

If you insert a document without the `:_id` key, MongoDB Java driver that Monger uses under the hood will generate one for you. Unfortunately,
it does so by mutating the document you pass it. With Clojure's immutable data structures, that won't work the way MongoDB Java driver authors
expected.

So it is highly recommended to always store documents with the `:_id` key set. If you need a generated object id. You do so by instantiating
`org.bson.types.ObjectId` without arguments:

``` clojure
(ns my.service.server
  (:require [monger.core :refer [connect! connect set-db! get-db]]
            [monger.collection :refer [insert]])
  (:import [org.bson.types ObjectId]))

;; localhost, default port
(connect!)
(set-db! (monger.core/get-db "monger-test"))

(let [oid (ObjectId.)
      doc { :first_name "John" :last_name "Lennon" }]
  (insert "documents" (merge doc {:_id oid})))
```

To convert a string in the object id form (for example, coming from a
Web form) to an `ObjectId`, instantiate `ObjectId` with an argument:

``` clojure
(ns my.service.server
  (:import org.bson.types.ObjectId))

;; MongoDB: convert a string to an ObjectId:
(ObjectId. "4fea999c0364d8e880c05157") ;; => #<ObjectId 4fea999c0364d8e880c05157>
```

Document ids in MongoDB do not have to be of the object id type, they also can be strings, integers and any value you can store that MongoDB
knows how to compare order (sort). However, using `ObjectId`s is usually a good idea.


### Default WriteConcern

To set default write concern, use `monger.core/set-default-write-concern!` function:

``` clojure
(ns my.service.server
  (:require [monger.core :as mg]))

(mg/connect!)
(mg/set-db! (mg/get-db "monger-test"))

(mg/set-default-write-concern! WriteConcern/FSYNC_SAFE)
```


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

``` clojure
(ns my.service.server
  (:require [monger.collection :as mc]))

(mc/insert "documents" {:first_name "John"  :last_name "Lennon"})
(mc/insert "documents" {:first_name "Ringo" :last_name "Starr"})

(mc/find "documents" {:first_name "Ringo"})
```

`monger.collection/find-maps` is similar to `monger.collection/find` but converts `DBObject` instances to Clojure maps:

``` clojure
;; returns all documents as Clojure maps
(mc/find-maps "documents")

;; returns documents with year field value of 1998, as Clojure maps
(mc/find-maps "documents" { :year 1998 })
```


`monger.collection/find-one` finds one document and returns it as a `DBObject` instance:

``` clojure
;; find one document by id, as `com.mongodb.DBObject` instance
(mc/find-one "documents" { :_id (ObjectId. "4ec2d1a6b55634a935ea4ac8") })
```


`monger.collection/find-one-as-map` is similar to `monger.collection/find-one` but converts `DBObject` instances to Clojure maps:

``` clojure
;; find one document by id, as a Clojure map
(mc/find-one-as-map "documents" { :_id (ObjectId. "4ec2d1a6b55634a935ea4ac8") })
```


A more convenient way of finding a document by id as Clojure map is `monger.collection/find-map-by-id`:

``` clojure
(ns my.service.finders
  (:require [monger.collection :as mc]
            [monger.operators :refer :all]))

(let [oid (ObjectId.)]
  (mc/insert "documents" {:_id oid :first_name "John" :last_name "Lennon"})
  (mc/find-map-by-id "documents" oid))
```


### Keyword and String Field Names

Clojure maps commonly use keywords, however, BSON and many other
programming languages do not have a data type like that. Intead,
strings are used as keys. Several Monger finder functions are "low
level", such as
[monger.collection/find](http://reference.clojuremongodb.info/monger.collection.html#var-find),
and return `com.mongodb.DBObject` instances. They can be thought of as
regular Java maps with a little bit of MongoDB-specific metadata.

Other finders combine `monger.collection/find` with
[monger.conversion/from-db-object](http://reference.clojuremongodb.info/monger.conversion.html#var-from-db-object)
to return Clojure maps. Some of those functions take the extra
`keywordize` argument that control if resulting map keys will be
turned into keywords.  An example of such finder is
[monger.collection/find-one-as-map](http://reference.clojuremongodb.info/monger.collection.html#var-find-one-as-map). By
default Monger will keywordize keys.

You can use
[monger.conversion/from-db-object](http://reference.clojuremongodb.info/monger.conversion.html#var-from-db-object)
and
[monger.conversion/to-db-object](http://reference.clojuremongodb.info/monger.conversion.html#var-to-db-object)
to convert maps to `DBObject` instances and back using a custom field
name conversion strategy if you need to. Keep in mind that it likely
will affect interoperability with other technologies (that may or may
not use the same naming/encoding conversion), query capabilities for
cases when exact field names are not known and performance for
write-heavy workloads.



### Using operators

Monger provides a convenient way to use [MongoDB query
operators](http://www.mongodb.org/display/DOCS/Advanced+Queries#AdvancedQueries-ConditionalOperators). While
operators can be used in queries with strings, for example:

``` clojure
;; with a query that uses operators as strings
(mc/find "products" { :price_in_subunits { "$gt" 1200 "$lte" 4000 } })
```

there is a better way to do it with Clojure. By using
`monger.operators` namespace, MongoDB $operators can be written as
Clojure symbols. $operators are implemented as macros that expand at
compile time. Here is what it looks like with operator macros:

``` clojure
(ns my.app
  (:use monger.operators))

;; using MongoDB operators as symbols
(mc/find "products" { :price_in_subunits { $gt 1200 $lte 4000 } })
```



### More Examples

These and other examples of Monger finders in one gist:

``` clojure
(ns my.service.finders
  (:require [monger.collection :as mc])
  (:use monger.operators))

;; find one document by id, as Clojure map
(mc/find-map-by-id "documents" (ObjectId. "4ec2d1a6b55634a935ea4ac8"))

;; find one document by id, as `com.mongodb.DBObject` instance
(mc/find-by-id "documents" (ObjectId. "4ec2d1a6b55634a935ea4ac8"))

;; find one document as Clojure map
(mc/find-one-as-map "documents" { :_id (ObjectId. "4ec2d1a6b55634a935ea4ac8") })

;; find one document by id, as `com.mongodb.DBObject` instance
(mc/find-one "documents" { :_id (ObjectId. "4ec2d1a6b55634a935ea4ac8") })


;; all documents  as Clojure maps
(mc/find-maps "documents")

;; all documents  as `com.mongodb.DBObject` instances
(mc/find "documents")

;; with a query, as Clojure maps
(mc/find-maps "documents" { :year 1998 })

;; with a query, as `com.mongodb.DBObject` instances
(mc/find "documents" { :year 1998 })

;; with a query that uses operators
(mc/find "products" { :price_in_subunits { $gt 4000 $lte 1200 } })

;; with a query that uses operators as strings
(mc/find "products" { :price_in_subunits { "$gt" 4000 "$lte" 1200 } })
```



### Counting Documents

Use `monger.collection/count`, `monger.collection/empty?` and `monger.collection/any?`.



### Query DSL Overview

For cases when it is necessary to combine sorting, limiting or offseting results, pagination and even more advanced features
like cursor snapshotting or manual index hinting, Monger provides a very powerful query DSL. Here is what it looks like:

``` clojure
(with-collection "movies"
  (find { :year { $lt 2010 $gte 2000 } :revenue { $gt 20000000 } })  
  (fields [ :year :title :producer :cast :budget :revenue ])
  ;; note the use of sorted maps with sort
  (sort (sorted-map :revenue -1))
  (skip 10)
  (limit 20)
  (hint "year-by-year-revenue-idx")
  (snapshot))
```

It is easy to add new DSL elements, for example, adding pagination
took literally less than 10 lines of Clojure code. Here is what it
looks like:

``` clojure
(with-collection coll
                  (find {})
                  (paginate :page 1 :per-page 3)
                  (sort (sorted-map :title 1))
                  (read-preference ReadPreference/PRIMARY))
```

Query DSL supports composition, too:

``` clojure
(let
    [top3               (partial-query (limit 3))
     by-population-desc (partial-query (sort (sorted-map :population -1)))
     result             (with-collection coll
                          (find {})
                          (merge top3)
                          (merge by-population-desc))]
  ;; ...
  )
```

Learn more in our [Querying](/articles/querying.html) guide.



## How to Update Documents with Monger

Monger's update API follows the following simple rule: the "syntax"
for condition and update document structure is the same or as close as
possible to MongoDB shell and the official drivers. In addition,
Monger provides several convenience functions for common cases, for
example, finding documents by id.


### Regular Updates

`monger.collection/update` is the most commonly used way of updating
documents. `monger.collection/update-by-id` is useful when document id
is known:

``` clojure
(ns my.service
  (:require [monger.collection :as mc]))

;; updates a document by id
(mc/update-by-id "scores" oid {:score 1088})
```

### Upserts

MongoDB supports upserts, "update or insert" operations. To do an
upsert with Monger, use `monger.collection/update` function with
`:upsert` option set to true:

``` clojure
(ns my.service
  (:require [monger.collection :as mc]))

;; updates score for player "sam" if it exists; creates a new document otherwise
(mc/update "scores" {:player "sam"} {:score 1088} :upsert true)
```

Note that upsert only inserts one document. Learn more about upserts in [this MongoDB documentation section](http://docs.mongodb.org/manual/core/update/#Updating-update).


### Atomic Modifiers

Modifier operations are highly-efficient and useful when updating
existing values; for instance, they're great for incrementing
counters, setting individual fields, updating fields that are arrays
and so on.

MongoDB supports modifiers via update operation and Monger API works
the same way: you pass a document with modifiers to
`monger.collection/update`. For example, to increment number of views
for a particular page:

``` clojure
(ns my.service
  (:require [monger.collection :as mc])
  (:use monger.operators))

(mc/update "visits" {:url "http://megacorp.com"} {$inc {:visits 1}})
```



## How to Remove Documents with Monger

Documents are removed using `monger.collection/remove` function.
`monger.collection/remove-by-id` is useful when document id is known.

``` clojure
(ns my.service.server
  (:use [monger.core :only [connect! connect set-db! get-db]]
        [monger.collection :only [insert update update-by-id remove-by-id] :as mc])
  (:import [org.bson.types ObjectId]
           [com.mongodb DB WriteConcern]))

;; localhost, default port
(connect!)
(set-db! (monger.core/get-db "monger-test"))

;; insert a few documents
(insert "documents" { :language "English" :pages 38 })
(insert "documents" { :language "Spanish" :pages 78 })
(insert "documents" { :language "Unknown" :pages 87 })

;; remove multiple documents
(mc/remove "documents" { :language "English" })

;; remove ALL documents in the collection
(mc/remove "documents")

;; with a different database
(let [archive-db (get-db "monger-test.archive")]
  (mc/remove archive-db "documents" { :readers 0 :pages 0 }))


;; remove document by id
(let [oid (ObjectId.)]
  (insert "documents" { :language "English" :pages 38 :_id oid })
  (remove-by-id "documents" oid))
```


## Integration With Other Libraries

Monger heavily relies on relatively recent Clojure features like
protocols to integrate with libraries like
[Cheshire](http://github.com/dakrone/cheshire) or
[clj-time](https://github.com/seancorfield/clj-time) ([Joda
Time](http://joda-time.sourceforge.net/)). As the result you can focus
on your application instead of figuring out how to glue two libraries
together.



### Cheshire (or clojure.data.json)

Many applications that use MongoDB and Monger have to serialize
documents stored in the database to JSON and pass them to other
applications using HTTP or messaging protocols such as [AMQP
0.9.1](http://bit.ly/amqp-model-explained) or
[ZeroMQ](http://zeromq.org).

This means that MongoDB data types (object ids, documents) need to be
serialized. While BSON, data format used by MongoDB, is semantically
very similar to JSON, MongoDB drivers do not typically provide
serialization to JSON and JSON serialization libraries typically do
not support MongoDB data types.

Monger provides a convenient feature for Cheshire, a pretty popular
modern JSON serialization library for Clojure. The way it works is
Monger will add custom serializes for MongoDB Java driver data types:
`org.bson.types.ObjectId` and `com.mongodb.DBObject` if you opt-in for
it.  To use it, you need to add Cheshire dependency to your project,
for example (with Leiningen)

``` clojure
[cheshire "5.1.1"]
```

and then require `monger.json` namespace like so:

``` clojure
(ns mycompany.myservice
  (:require monger.json))
```

when loaded, code in that namespace will extend necessary protocols
and that's it. Then you can pass documents that contain object ids in
them to JSON serialization functions from `cheshire.custom` and
everything will just work.

This feature is optional: Monger does not depend on `Cheshire` or
`clojure.data.json` and won't add unused dependencies to your project.

#### clojure.data.json Version Compatibility

Monger only works `clojure.data.json` `0.2.x` and `0.1.x`. Support for versions
earlier than `0.2.x` will be dropped in one of the future releases.



### clj-time, Joda Time

Because of various shortcomings of Java date/time classes provided by
the JDK, many projects choose to use [Joda
Time](http://joda-time.sourceforge.net/) to work with dates.

To be able to insert documents with Joda Time date values in them, you
need to require `monger.joda-time` namespace:

``` clojure
(ns mycompany.myservice
  (:require monger.joda-time))
```

Just like with `clojure.data.json` integration, there is nothing else
you have to do. This feature is optional: Monger does not depend on
`clj-time` or `Joda Time` and won't add unused dependencies to your
project.



### clojure.core.cache

Monger provides a MongoDB-backed cache implementation that conforms to
the `clojure.core.cache` protocol.  It uses capped collections for
caches. You can use any many cache data structure instances as your
application may need.

This topic is covered in the [Integration with 3rd party libraries](/articles/integration.html) guide.



## Wrapping Up

Congratulations, you now know how to do most common operations with
Monger. Monger and MongoDB both have much more to them to
explore. Other guides explain these and other features in depth, as
well as rationale and use cases for them.

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

Please take a moment to tell us what you think about this guide on
Twitter or the [Clojure MongoDB mailing
list](https://groups.google.com/forum/#!forum/clojure-mongodb)

Let us know what was unclear or what has not been covered. Maybe you
do not like the guide style or grammar or discover spelling
mistakes. Reader feedback is key to making the documentation better.
